package io.github.vinnih.kipty.ui.components

import androidx.compose.runtime.Composable
import io.github.vinnih.kipty.Screen
import io.github.vinnih.kipty.ui.audio.AudioController
import io.github.vinnih.kipty.ui.audio.AudioScreen
import io.github.vinnih.kipty.ui.create.CreateScreen
import io.github.vinnih.kipty.ui.home.HomeController
import io.github.vinnih.kipty.ui.home.HomeScreen
import io.github.vinnih.kipty.ui.loading.LoadingScreen
import io.github.vinnih.kipty.ui.notification.NotificationController
import io.github.vinnih.kipty.ui.notification.NotificationScreen
import io.github.vinnih.kipty.ui.player.PlayerController

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
                homeController = homeController,
                audioController = audioController,
                notificationController = notificationController,
                onNavigate = { onNavigate(it) }
            )
        }

        is Screen.Audio -> {
            AudioScreen(
                audioController = audioController,
                playerController = playerController,
                notificationController = notificationController,
                onBack = onBack,
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
                notificationController = notificationController,
                onBack = onBack
            )
        }
    }
}
