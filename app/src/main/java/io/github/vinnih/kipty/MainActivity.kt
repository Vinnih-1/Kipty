package io.github.vinnih.kipty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import dagger.hilt.android.AndroidEntryPoint
import io.github.vinnih.kipty.ui.audio.AudioScreen
import io.github.vinnih.kipty.ui.audio.AudioViewModel
import io.github.vinnih.kipty.ui.components.FloatingAddButton
import io.github.vinnih.kipty.ui.components.KiptyBottomBar
import io.github.vinnih.kipty.ui.create.CreateScreen
import io.github.vinnih.kipty.ui.home.HomeScreen
import io.github.vinnih.kipty.ui.home.HomeViewModel
import io.github.vinnih.kipty.ui.loading.LoadingScreen
import io.github.vinnih.kipty.ui.player.PlayerScreen
import io.github.vinnih.kipty.ui.player.PlayerViewModel
import io.github.vinnih.kipty.ui.theme.AppTheme
import io.github.vinnih.kipty.ui.theme.EnableEdgeToEdge
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

        setContent {
            var currentTopbar: (@Composable () -> Unit)? by remember { mutableStateOf(null) }
            val backstack = remember { mutableStateListOf<Any>(Loading) }

            this.EnableEdgeToEdge()

            AppTheme {
                Scaffold(
                    topBar = { currentTopbar?.invoke() },
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
                                    }, onTopBarChange = { topbar -> currentTopbar = topbar })
                                }

                                is Audio -> NavEntry(key) {
                                    AudioScreen(
                                        audioController = audioViewModel,
                                        playerController = playerViewModel,
                                        id = key.id,
                                        onBack = { backstack.removeLastOrNull() },
                                        onTopBarChange = { topbar -> currentTopbar = topbar }
                                    )
                                }

                                is Create -> NavEntry(key) {
                                    CreateScreen(homeController = homeViewModel, onBack = {
                                        backstack.removeLastOrNull()
                                    }, onTopBarChange = { topbar -> currentTopbar = null })
                                }

                                is Player -> NavEntry(key) {
                                    PlayerScreen(playerController = playerViewModel, onDismiss = {
                                        backstack.removeLastOrNull()
                                    }, onTopBarChange = { topbar ->
                                        currentTopbar = null
                                    }, modifier = Modifier.padding(paddingValues))
                                }

                                is Loading -> NavEntry(key) {
                                    LoadingScreen(
                                        homeController = homeViewModel,
                                        audioController = audioViewModel,
                                        text = "Loading...",
                                        onLoad = {
                                            backstack.remove(Loading)
                                            backstack.add(Home)
                                        },
                                        modifier = Modifier.padding(paddingValues),
                                        onTopBarChange = { topbar -> currentTopbar = null }
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
