package io.github.vinnih.kipty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import dagger.hilt.android.AndroidEntryPoint
import io.github.vinnih.kipty.ui.audio.AudioScreen
import io.github.vinnih.kipty.ui.audio.AudioViewModel
import io.github.vinnih.kipty.ui.create.CreateScreen
import io.github.vinnih.kipty.ui.home.HomeScreen
import io.github.vinnih.kipty.ui.home.HomeViewModel
import io.github.vinnih.kipty.ui.loading.LoadingScreen
import io.github.vinnih.kipty.ui.player.PlayerScreen
import io.github.vinnih.kipty.ui.player.PlayerViewModel
import io.github.vinnih.kipty.ui.theme.AppTheme
import io.github.vinnih.kipty.ui.theme.EnableEdgeToEdge
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

private data object Loading

private data object Home

private data class Audio(val id: Int)

private data object Create

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

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var currentTopbar: (@Composable () -> Unit)? by remember { mutableStateOf(null) }
            val backstack = remember { mutableStateListOf<Any>(Loading) }
            val scope = rememberCoroutineScope()
            val scaffoldState = rememberBottomSheetScaffoldState(
                bottomSheetState = rememberStandardBottomSheetState(skipHiddenState = false)
            )

            this.EnableEdgeToEdge()

            AppTheme {
                BackHandler(
                    enabled = scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded
                ) {
                    scope.launch {
                        scaffoldState.bottomSheetState.partialExpand()
                    }
                }

                BottomSheetScaffold(
                    topBar = { currentTopbar?.invoke() },
                    scaffoldState = scaffoldState,
                    sheetPeekHeight = 148.dp,
                    sheetContent = {
                        PlayerScreen(
                            playerController = playerViewModel,
                            scaffoldState = scaffoldState
                        )
                    },
                    sheetShape = RectangleShape,
                    sheetDragHandle = null,
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ) { paddingValues ->
                    NavDisplay(
                        modifier = Modifier.padding(
                            paddingValues
                        ),
                        backStack = backstack,
                        onBack = {
                            if (scaffoldState.bottomSheetState.currentValue ==
                                SheetValue.Expanded
                            ) {
                                scope.launch {
                                    scaffoldState.bottomSheetState.partialExpand()
                                }
                                return@NavDisplay
                            }

                            backstack.removeLastOrNull()
                        },
                        entryProvider = { key ->
                            when (key) {
                                is Home -> NavEntry(key) {
                                    HomeScreen(
                                        controller = homeViewModel,
                                        onClick = {
                                            backstack.add(Audio(it.uid))
                                        },
                                        onNotificationClick = {},
                                        onCreateClick = {
                                            backstack.add(Create)
                                        },
                                        onTopBarChange = { topbar ->
                                            currentTopbar =
                                                topbar
                                        }
                                    )
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
