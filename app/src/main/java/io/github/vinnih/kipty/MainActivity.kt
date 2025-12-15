package io.github.vinnih.kipty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import dagger.hilt.android.AndroidEntryPoint
import io.github.vinnih.kipty.ui.audio.AudioController
import io.github.vinnih.kipty.ui.audio.AudioScreen
import io.github.vinnih.kipty.ui.audio.AudioTopBar
import io.github.vinnih.kipty.ui.audio.AudioViewModel
import io.github.vinnih.kipty.ui.create.CreateScreen
import io.github.vinnih.kipty.ui.create.CreateTopBar
import io.github.vinnih.kipty.ui.home.HomeController
import io.github.vinnih.kipty.ui.home.HomeScreen
import io.github.vinnih.kipty.ui.home.HomeTopBar
import io.github.vinnih.kipty.ui.home.HomeViewModel
import io.github.vinnih.kipty.ui.loading.LoadingScreen
import io.github.vinnih.kipty.ui.notification.NotificationController
import io.github.vinnih.kipty.ui.notification.NotificationScreen
import io.github.vinnih.kipty.ui.notification.NotificationTopBar
import io.github.vinnih.kipty.ui.notification.NotificationViewModel
import io.github.vinnih.kipty.ui.player.PlayerController
import io.github.vinnih.kipty.ui.player.PlayerScreen
import io.github.vinnih.kipty.ui.player.PlayerViewModel
import io.github.vinnih.kipty.ui.theme.AppTheme
import io.github.vinnih.kipty.ui.theme.EnableEdgeToEdge
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

sealed interface Screen {
    data object Loading : Screen

    data object Home : Screen

    data class Audio(val id: Int) : Screen

    data object Create : Screen

    data object Notification : Screen
}

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
    private val notificationViewModel: NotificationViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            this.EnableEdgeToEdge()

            AppTheme {
                AppScaffold(
                    homeController = homeViewModel,
                    audioController = audioViewModel,
                    playerController = playerViewModel,
                    notificationController = notificationViewModel
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AppScaffold(
        homeController: HomeController,
        audioController: AudioController,
        playerController: PlayerController,
        notificationController: NotificationController
    ) {
        val backstack = remember { mutableStateListOf<Screen>(Screen.Loading) }
        val scaffoldState = rememberBottomSheetScaffoldState()
        val scope = rememberCoroutineScope()

        BackHandler(
            enabled = scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded
        ) {
            scope.launch { scaffoldState.bottomSheetState.partialExpand() }
        }

        BottomSheetScaffold(
            topBar = {
                AppTopBar(
                    currentScreen = backstack.last(),
                    notificationController = notificationController,
                    audioController = audioController,
                    playerController = playerController,
                    onNavigate = { screen -> backstack.add(screen) },
                    onBack = { backstack.removeLastOrNull() }
                )
            },
            scaffoldState = scaffoldState,
            sheetPeekHeight = 148.dp,
            sheetContent = {
                PlayerScreen(
                    playerController = playerController,
                    scaffoldState = scaffoldState
                )
            },
            sheetShape = RectangleShape,
            sheetDragHandle = null
        ) { paddingValues ->
            NavDisplay(
                modifier = Modifier.padding(paddingValues),
                backStack = backstack,
                onBack = { backstack.removeLastOrNull() },
                entryProvider = { key ->
                    when (key) {
                        is Screen -> NavEntry(key) {
                            AppNavigation(
                                currentScreen = key,
                                homeController = homeController,
                                audioController = audioController,
                                playerController = playerController,
                                notificationController = notificationController,
                                onNavigate = { screen -> backstack.add(screen) },
                                onBack = { backstack.removeLastOrNull() }
                            )
                        }
                    }
                }
            )
        }
    }

    @Composable
    fun AppTopBar(
        currentScreen: Screen,
        notificationController: NotificationController,
        audioController: AudioController,
        playerController: PlayerController,
        onNavigate: (Screen) -> Unit,
        onBack: () -> Unit
    ) {
        when (currentScreen) {
            is Screen.Home -> {
                HomeTopBar(
                    notificationController = notificationController,
                    onNotificationClick = { onNavigate(Screen.Notification) },
                    onCreateClick = { onNavigate(Screen.Create) }
                )
            }

            is Screen.Audio -> {
                AudioTopBar(
                    id = currentScreen.id,
                    notificationController = notificationController,
                    audioController = audioController,
                    playerController = playerController,
                    onBack = onBack
                )
            }

            is Screen.Create -> {
                CreateTopBar(onBack = onBack)
            }

            is Screen.Loading -> {
            }

            is Screen.Notification -> {
                NotificationTopBar(onBack = onBack)
            }
        }
    }

    @Composable
    fun AppNavigation(
        currentScreen: Screen,
        homeController: HomeController,
        audioController: AudioController,
        playerController: PlayerController,
        notificationController: NotificationController,
        onNavigate: (Screen) -> Unit,
        onBack: () -> Unit
    ) {
        when (currentScreen) {
            is Screen.Home -> {
                HomeScreen(
                    audioController = audioController,
                    onClick = {
                        onNavigate(Screen.Audio(it.uid))
                    }
                )
            }

            is Screen.Audio -> {
                AudioScreen(
                    audioController = audioController,
                    playerController = playerController,
                    id = currentScreen.id
                )
            }

            is Screen.Create -> {
                CreateScreen(
                    homeController = homeController,
                    onBack = onBack
                )
            }

            is Screen.Loading -> {
                LoadingScreen(
                    homeController = homeController,
                    audioController = audioController,
                    onBack = {
                        onBack.invoke()
                        onNavigate(Screen.Home)
                    }
                )
            }

            is Screen.Notification -> {
                NotificationScreen(
                    notificationController = notificationController
                )
            }
        }
    }
}
