package io.github.vinnih.kipty

import android.os.Bundle
import androidx.activity.ComponentActivity
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import dagger.hilt.android.AndroidEntryPoint
import io.github.vinnih.kipty.ui.components.AppNavigation
import io.github.vinnih.kipty.ui.create.Step
import io.github.vinnih.kipty.ui.notification.NotificationController
import io.github.vinnih.kipty.ui.notification.NotificationViewModel
import io.github.vinnih.kipty.ui.player.PlayerController
import io.github.vinnih.kipty.ui.player.PlayerScreen
import io.github.vinnih.kipty.ui.player.PlayerViewModel
import io.github.vinnih.kipty.ui.theme.AppTheme
import io.github.vinnih.kipty.ui.theme.EnableEdgeToEdge
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

    private val playerViewModel: PlayerViewModel by viewModels()
    private val notificationViewModel: NotificationViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            this.EnableEdgeToEdge()

            AppTheme {
                AppScaffold(
                    splashScreen = splashScreen,
                    playerController = playerViewModel,
                    notificationController = notificationViewModel
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AppScaffold(
        splashScreen: SplashScreen,
        playerController: PlayerController,
        notificationController: NotificationController,
        modifier: Modifier = Modifier
    ) {
        val backstack = remember { mutableStateListOf<Screen>(Screen.Home) }
        val scaffoldState = rememberBottomSheetScaffoldState()
        var loading by remember { mutableStateOf(true) }

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

        val safeOnBack: () -> Unit = remember {
            {
                if (backstack.size > 1) {
                    backstack.removeAt(backstack.size - 1)
                }
            }
        }

        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = animatedPeekHeight,
            sheetContent = {
                PlayerScreen(
                    playerController = playerController,
                    notificationController = notificationController,
                    onNavigate = { screen -> backstack.add(screen) },
                    scaffoldState = scaffoldState
                )
            },
            sheetShape = RectangleShape,
            sheetDragHandle = null,
            modifier = modifier
        ) { paddingValues ->
            NavDisplay(
                modifier = Modifier.padding(paddingValues),
                backStack = backstack,
                onBack = {
                    if (scaffoldState.bottomSheetState.currentValue != SheetValue.Expanded) {
                        backstack.removeLastOrNull()
                    }
                },
                entryProvider = { key ->
                    when (key) {
                        is Screen -> NavEntry(key) {
                            AppNavigation(
                                currentScreen = key,
                                playerController = playerController,
                                notificationController = notificationController,
                                onNavigate = { screen -> backstack.add(screen) },
                                onBack = safeOnBack,
                                onDatabasePopulated = { loading = false }
                            )
                        }
                    }
                }
            )
        }
    }
}
