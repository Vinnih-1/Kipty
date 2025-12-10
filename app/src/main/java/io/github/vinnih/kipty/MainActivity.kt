package io.github.vinnih.kipty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import dagger.hilt.android.AndroidEntryPoint
import io.github.vinnih.kipty.ui.audio.AudioScreen
import io.github.vinnih.kipty.ui.audio.AudioViewModel
import io.github.vinnih.kipty.ui.components.FloatingAddButton
import io.github.vinnih.kipty.ui.components.KiptyBottomBar
import io.github.vinnih.kipty.ui.components.KiptyTopBar
import io.github.vinnih.kipty.ui.create.CreateScreen
import io.github.vinnih.kipty.ui.home.HomeScreen
import io.github.vinnih.kipty.ui.home.HomeViewModel
import io.github.vinnih.kipty.ui.loading.LoadingScreen
import io.github.vinnih.kipty.ui.player.PlayerScreen
import io.github.vinnih.kipty.ui.player.PlayerViewModel
import io.github.vinnih.kipty.ui.theme.AppTheme
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

private data object Loading

private data object Home

private data class Audio(val id: Int)

private data object Create

private data object Player

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

        setContent {
            val backstack = remember { mutableStateListOf<Any>(Loading) }
            val screen = backstack.last()

            AppTheme {
                Scaffold(
                    topBar = { if (screen is Home) KiptyTopBar("Home") },
                    bottomBar = {
                        KiptyBottomBar(onClick = {
                            backstack.add(Player)
                        }, playerController = playerViewModel)
                    },
                    floatingActionButton = {
                        FloatingAddButton(onClick = {
                            backstack.add(Create)
                        }, modifier = Modifier.size(72.dp))
                    }
                ) { paddingValues ->
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
                                    AudioScreen(
                                        audioController = audioViewModel,
                                        playerController = playerViewModel,
                                        id = key.id,
                                        onBack = { backstack.removeLastOrNull() }
                                    )
                                }

                                is Create -> NavEntry(key) {
                                    CreateScreen(homeController = homeViewModel, onBack = {
                                        backstack.removeLastOrNull()
                                    })
                                }

                                is Player -> NavEntry(key) {
                                    PlayerScreen(playerController = playerViewModel, onDismiss = {
                                        backstack.removeLastOrNull()
                                    }, modifier = Modifier.padding(paddingValues))
                                }

                                is Loading -> NavEntry(key) {
                                    LoadingScreen(
                                        homeController = homeViewModel,
                                        text = "Loading...",
                                        onLoad = {
                                            backstack.remove(Loading)
                                            backstack.add(Home)
                                        },
                                        modifier = Modifier.padding(paddingValues)
                                    )
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
