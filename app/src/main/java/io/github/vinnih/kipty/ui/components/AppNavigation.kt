package io.github.vinnih.kipty.ui.components

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import io.github.vinnih.kipty.Screen
import io.github.vinnih.kipty.ui.audio.AudioScreen
import io.github.vinnih.kipty.ui.audio.AudioViewModel
import io.github.vinnih.kipty.ui.configuration.ConfigurationScreen
import io.github.vinnih.kipty.ui.configuration.ConfigurationViewModel
import io.github.vinnih.kipty.ui.create.CreateScreen
import io.github.vinnih.kipty.ui.create.CreateViewModel
import io.github.vinnih.kipty.ui.edit.EditScreen
import io.github.vinnih.kipty.ui.edit.EditViewModel
import io.github.vinnih.kipty.ui.home.HomeScreen
import io.github.vinnih.kipty.ui.home.HomeViewModel
import io.github.vinnih.kipty.ui.notification.NotificationController
import io.github.vinnih.kipty.ui.notification.NotificationScreen
import io.github.vinnih.kipty.ui.player.PlayerController
import io.github.vinnih.kipty.ui.welcome.WelcomeScreen
import io.github.vinnih.kipty.ui.welcome.WelcomeViewModel

@Composable
fun AppNavigation(
    currentScreen: Screen,
    playerController: PlayerController,
    notificationController: NotificationController,
    onNavigate: (Screen) -> Unit,
    onBack: () -> Unit,
    onDatabasePopulated: () -> Unit,
    onGetStarted: () -> Unit
) {
    when (currentScreen) {
        is Screen.Welcome -> {
            val viewModel = hiltViewModel<WelcomeViewModel>()
            WelcomeScreen(
                onGetStarted = onGetStarted,
                welcomeController = viewModel,
                onDatabasePopulated = onDatabasePopulated
            )
        }

        is Screen.Home -> {
            val viewModel = hiltViewModel<HomeViewModel>()
            HomeScreen(
                homeController = viewModel,
                playerController = playerController,
                notificationController = notificationController,
                onNavigate = onNavigate
            )
        }

        is Screen.Audio -> {
            val viewModel = hiltViewModel<AudioViewModel>()
            AudioScreen(
                audioController = viewModel,
                playerController = playerController,
                notificationController = notificationController,
                onNavigate = onNavigate,
                onBack = onBack,
                id = currentScreen.id
            )
        }

        is Screen.Create -> {
            val viewModel = hiltViewModel<CreateViewModel>()
            CreateScreen(
                createController = viewModel,
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
            val viewModel = hiltViewModel<ConfigurationViewModel>()
            ConfigurationScreen(
                configurationController = viewModel,
                onNavigate = onNavigate,
                onBack = onBack
            )
        }

        is Screen.Edit -> {
            val viewModel = hiltViewModel<EditViewModel>()
            EditScreen(
                editController = viewModel,
                id = currentScreen.id,
                step = currentScreen.step,
                onBack = onBack
            )
        }
    }
}
