package io.github.vinnih.kipty.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import io.github.vinnih.kipty.data.database.entity.NotificationCategory
import io.github.vinnih.kipty.data.database.entity.NotificationEntity
import io.github.vinnih.kipty.ui.notification.FakeNotificationViewModel
import io.github.vinnih.kipty.ui.notification.NotificationScreen
import io.github.vinnih.kipty.ui.notification.NotificationTopBar
import io.github.vinnih.kipty.ui.notification.NotificationUiState
import io.github.vinnih.kipty.ui.theme.AppTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class NotificationScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun makeNotification(
        id: Int,
        title: String = "Notification $id",
        content: String = "Content of notification $id",
        read: Boolean = false,
        channel: NotificationCategory = NotificationCategory.TRANSCRIPTION_INIT,
        audioId: Int = 1,
        audioName: String = "audio.mp3",
        createdAt: String = "2025-12-14T12:46:48.849"
    ) = NotificationEntity(
        uid = id,
        title = title,
        content = content,
        read = read,
        createdAt = createdAt,
        audioId = audioId,
        audioName = audioName,
        channel = channel
    )

    @Test
    fun notificationTopBar_showsTitle() {
        composeRule.setContent {
            AppTheme {
                NotificationTopBar(onBack = {}, unreadNotifications = 0)
            }
        }

        composeRule.onNodeWithText("Notifications").assertIsDisplayed()
    }

    @Test
    fun notificationTopBar_withUnread_showsUnreadCount() {
        composeRule.setContent {
            AppTheme {
                NotificationTopBar(onBack = {}, unreadNotifications = 3)
            }
        }

        composeRule.onNodeWithText("3").assertIsDisplayed()
    }

    @Test
    fun notificationTopBar_withZeroUnread_doesNotShowBadge() {
        composeRule.setContent {
            AppTheme {
                NotificationTopBar(onBack = {}, unreadNotifications = 0)
            }
        }

        composeRule.onNodeWithText("0").assertDoesNotExist()
    }

    @Test
    fun notificationTopBar_backButton_triggersCallback() {
        var backPressed = false
        composeRule.setContent {
            AppTheme {
                NotificationTopBar(onBack = { backPressed = true }, unreadNotifications = 0)
            }
        }

        composeRule.onNode(hasContentDescription("back button"))
            .performClick()

        assertTrue(backPressed)
    }

    @Test
    fun notificationScreen_withNoNotifications_showsEmptyState() {
        composeRule.setContent {
            AppTheme {
                NotificationScreen(
                    notificationController = FakeNotificationViewModel(NotificationUiState()),
                    onNavigate = {},
                    onBack = {}
                )
            }
        }

        composeRule.onNodeWithText("You have no notifications").assertIsDisplayed()
        composeRule.onNodeWithText("Check back later for updates").assertIsDisplayed()
    }

    @Test
    fun notificationScreen_withTodayNotifications_showsTodaySection() {
        val notification = makeNotification(1, title = "Transcription started")

        composeRule.setContent {
            AppTheme {
                NotificationScreen(
                    notificationController = FakeNotificationViewModel(
                        NotificationUiState(
                            today = listOf(notification),
                            unread = listOf(notification)
                        )
                    ),
                    onNavigate = {},
                    onBack = {}
                )
            }
        }

        composeRule.onNodeWithText("Today").assertIsDisplayed()
        composeRule.onNodeWithText("Transcription started").assertIsDisplayed()
    }

    @Test
    fun notificationScreen_transcriptionInit_showsViewButton() {
        val notification = makeNotification(
            id = 1,
            title = "Processing your audio",
            channel = NotificationCategory.TRANSCRIPTION_INIT
        )

        composeRule.setContent {
            AppTheme {
                NotificationScreen(
                    notificationController = FakeNotificationViewModel(
                        NotificationUiState(today = listOf(notification))
                    ),
                    onNavigate = {},
                    onBack = {}
                )
            }
        }

        composeRule.onNodeWithText("View").assertIsDisplayed()
    }

    @Test
    fun notificationScreen_transcriptionDone_showsListenButton() {
        val notification = makeNotification(
            id = 1,
            title = "Transcription complete",
            channel = NotificationCategory.TRANSCRIPTION_DONE
        )

        composeRule.setContent {
            AppTheme {
                NotificationScreen(
                    notificationController = FakeNotificationViewModel(
                        NotificationUiState(today = listOf(notification))
                    ),
                    onNavigate = {},
                    onBack = {}
                )
            }
        }

        composeRule.onNodeWithText("Listen").assertIsDisplayed()
    }

    @Test
    fun notificationScreen_clickingListenButton_navigatesToAudio() {
        var navigated = false
        val notification = makeNotification(
            id = 1,
            title = "Transcription complete",
            channel = NotificationCategory.TRANSCRIPTION_DONE,
            audioId = 42
        )

        composeRule.setContent {
            AppTheme {
                NotificationScreen(
                    notificationController = FakeNotificationViewModel(
                        NotificationUiState(today = listOf(notification))
                    ),
                    onNavigate = { navigated = true },
                    onBack = {}
                )
            }
        }

        composeRule.onNodeWithText("Listen").performClick()
        assertTrue(navigated)
    }
}
