package io.github.vinnih.kipty.ui.components

import androidx.compose.runtime.Composable
import io.github.vinnih.kipty.Screen
import io.github.vinnih.kipty.ui.audio.AudioController
import io.github.vinnih.kipty.ui.audio.AudioTopBar
import io.github.vinnih.kipty.ui.create.CreateTopBar
import io.github.vinnih.kipty.ui.home.HomeTopBar
import io.github.vinnih.kipty.ui.notification.NotificationController
import io.github.vinnih.kipty.ui.notification.NotificationTopBar
import io.github.vinnih.kipty.ui.player.PlayerController

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
