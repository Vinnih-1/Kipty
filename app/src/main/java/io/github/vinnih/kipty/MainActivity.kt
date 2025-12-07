package io.github.vinnih.kipty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import dagger.hilt.android.AndroidEntryPoint
import io.github.vinnih.androidtranscoder.utils.toWavReader
import io.github.vinnih.kipty.data.application.AppConfig
import io.github.vinnih.kipty.ui.audio.AudioScreen
import io.github.vinnih.kipty.ui.audio.AudioViewModel
import io.github.vinnih.kipty.ui.components.FloatingAddButton
import io.github.vinnih.kipty.ui.components.KiptyBottomBar
import io.github.vinnih.kipty.ui.components.KiptyTopBar
import io.github.vinnih.kipty.ui.home.HomeScreen
import io.github.vinnih.kipty.ui.home.HomeViewModel
import io.github.vinnih.kipty.ui.player.PlayerScreen
import io.github.vinnih.kipty.ui.player.PlayerViewModel
import io.github.vinnih.kipty.ui.theme.AppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

private data object Home

private data class Audio(val id: Int)

@OptIn(ExperimentalSerializationApi::class)
val json = Json {
    allowTrailingComma = true
    ignoreUnknownKeys = true
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()
    private val playerViewModel: PlayerViewModel by viewModels()
    private val audioViewModel: AudioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (!AppConfig(applicationContext).read().defaultSamplesLoaded) {
            lifecycleScope.launch(Dispatchers.IO) {
                homeViewModel.copyAssets().map {
                    it.toWavReader(applicationContext.cacheDir)
                }.forEach { reader ->
                    homeViewModel.createAudio(reader)
                    reader.dispose()
                }
            }
        }

        setContent {
            val backstack = remember { mutableStateListOf<Any>(Home) }
            var showPlayerScreen by remember { mutableStateOf(false) }
            val screen = backstack.last()

            AppTheme {
                Scaffold(
                    topBar = { if (screen is Home) KiptyTopBar("Home") },
                    bottomBar = {
                        KiptyBottomBar(onClick = {
                            showPlayerScreen = true
                        }, playerController = playerViewModel)
                    },
                    floatingActionButton = {
                        FloatingAddButton(modifier = Modifier.size(72.dp))
                    }
                ) { paddingValues ->
                    if (showPlayerScreen) {
                        PlayerScreen(playerController = playerViewModel, onDismiss = {
                            showPlayerScreen = false
                        })
                    }

                    NavDisplay(
                        modifier = Modifier.padding(
                            paddingValues
                        ),
                        backStack = backstack,
                        onBack = {
                            backstack.removeLastOrNull()
                        },
                        entryProvider = { key ->
                            when (key) {
                                is Home -> NavEntry(key) {
                                    HomeScreen(controller = homeViewModel, onClick = {
                                        backstack.add(Audio(it.uid))
                                    })
                                }

                                is Audio -> NavEntry(key) {
                                    AudioScreen(audioController = audioViewModel, playerController = playerViewModel, id = key.id, onBack = {
                                        backstack.removeLastOrNull()
                                    })
                                }

                                else -> {
                                    error("Unknown key: $key")
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
