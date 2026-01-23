package io.github.vinnih.kipty.ui.components

import androidx.compose.runtime.Composable
import io.github.vinnih.kipty.Screen
import io.github.vinnih.kipty.ui.audio.AudioController
import io.github.vinnih.kipty.ui.audio.AudioScreen
import io.github.vinnih.kipty.ui.configuration.ConfigurationController
import io.github.vinnih.kipty.ui.configuration.ConfigurationScreen
import io.github.vinnih.kipty.ui.create.CreateController
import io.github.vinnih.kipty.ui.create.CreateScreen
import io.github.vinnih.kipty.ui.edit.EditController
import io.github.vinnih.kipty.ui.edit.EditScreen
import io.github.vinnih.kipty.ui.home.HomeController
import io.github.vinnih.kipty.ui.home.HomeScreen
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
    configurationController: ConfigurationController,
    createController: CreateController,
    editController: EditController,
    onNavigate: (Screen) -> Unit,
    onBack: () -> Unit
) {
    when (currentScreen) {
        is Screen.Home -> {
            HomeScreen(
                homeController = homeController,
                audioController = audioController,
                playerController = playerController,
                notificationController = notificationController,
                onNavigate = onNavigate
            )
        }

        is Screen.Audio -> {
            AudioScreen(
                audioController = audioController,
                playerController = playerController,
                notificationController = notificationController,
                configurationController = configurationController,
                onNavigate = onNavigate,
                onBack = onBack,
                id = currentScreen.id
            )
        }

        is Screen.Create -> {
            CreateScreen(
                createController = createController,
                onBack = onBack
            )
        }

        is Screen.Notification -> {
            NotificationScreen(
                notificationController = notificationController,
                onNavigate = onNavigate,
                onBack = onBack
            )
        }

        is Screen.Configuration -> {
            ConfigurationScreen(
                configurationController = configurationController,
                onNavigate = onNavigate,
                onBack = onBack
            )
        }

        is Screen.Edit -> {
            EditScreen(
                editController = editController,
                id = currentScreen.id,
                step = currentScreen.step,
                onBack = onBack
            )
        }
    }
}
