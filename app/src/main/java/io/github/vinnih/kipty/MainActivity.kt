package io.github.vinnih.kipty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import dagger.hilt.android.AndroidEntryPoint
import io.github.vinnih.kipty.ui.audio.AudioController
import io.github.vinnih.kipty.ui.audio.AudioViewModel
import io.github.vinnih.kipty.ui.components.AppNavigation
import io.github.vinnih.kipty.ui.configuration.ConfigurationController
import io.github.vinnih.kipty.ui.configuration.ConfigurationViewModel
import io.github.vinnih.kipty.ui.create.CreateController
import io.github.vinnih.kipty.ui.create.CreateViewModel
import io.github.vinnih.kipty.ui.create.Step
import io.github.vinnih.kipty.ui.edit.EditController
import io.github.vinnih.kipty.ui.edit.EditViewModel
import io.github.vinnih.kipty.ui.home.HomeController
import io.github.vinnih.kipty.ui.home.HomeViewModel
import io.github.vinnih.kipty.ui.notification.NotificationController
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
    data object Home : Screen

    data class Audio(val id: Int) : Screen

    data object Create : Screen

    data object Notification : Screen

    data object Configuration : Screen

    data class Edit(val id: Int, val step: Step) : Screen
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
    private val configurationViewModel: ConfigurationViewModel by viewModels()
    private val createViewModel: CreateViewModel by viewModels()
    private val editViewModel: EditViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            this.EnableEdgeToEdge()

            AppTheme {
                AppScaffold(
                    splashScreen = splashScreen,
                    homeController = homeViewModel,
                    audioController = audioViewModel,
                    playerController = playerViewModel,
                    notificationController = notificationViewModel,
                    configurationController = configurationViewModel,
                    createController = createViewModel,
                    editController = editViewModel
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AppScaffold(
        splashScreen: SplashScreen,
        homeController: HomeController,
        audioController: AudioController,
        playerController: PlayerController,
        notificationController: NotificationController,
        configurationController: ConfigurationController,
        createController: CreateController,
        editController: EditController
    ) {
        val backstack = remember { mutableStateListOf<Screen>(Screen.Home) }
        val scaffoldState = rememberBottomSheetScaffoldState()
        val scope = rememberCoroutineScope()
        var loading by remember { mutableStateOf(true) }

        LaunchedEffect(Unit) {
            homeController.populateDatabase {
                loading = false
            }
        }

        splashScreen.setKeepOnScreenCondition { loading }

        val shouldShowBottomSheet = when (backstack.lastOrNull()) {
            is Screen.Create -> false
            is Screen.Edit -> false
            else -> true
        }

        val animatedPeekHeight by animateDpAsState(
            targetValue = if (shouldShowBottomSheet) 148.dp else 0.dp,
            animationSpec = tween(durationMillis = 300),
            label = "bottomSheetPeekHeight"
        )

        BackHandler(
            enabled = scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded
        ) {
            scope.launch { scaffoldState.bottomSheetState.partialExpand() }
        }

        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = animatedPeekHeight,
            sheetContent = {
                PlayerScreen(
                    playerController = playerController,
                    configurationController = configurationController,
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
                                configurationController = configurationController,
                                createController = createController,
                                editController = editController,
                                onNavigate = { screen -> backstack.add(screen) },
                                onBack = { backstack.removeLastOrNull() }
                            )
                        }
                    }
                }
            )
        }
    }
}
