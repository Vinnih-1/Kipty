package io.github.vinnih.kipty.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.TranscriptionState
import io.github.vinnih.kipty.data.settings.AppSettings
import io.github.vinnih.kipty.ui.home.FakeHomeViewModel
import io.github.vinnih.kipty.ui.home.HomeScreen
import io.github.vinnih.kipty.ui.home.HomeTopBar
import io.github.vinnih.kipty.ui.home.HomeUiState
import io.github.vinnih.kipty.ui.notification.FakeNotificationViewModel
import io.github.vinnih.kipty.ui.player.FakePlayerViewModel
import io.github.vinnih.kipty.ui.theme.AppTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val fakeSettings = AppSettings(
        showTimestamp = true,
        minimumThreads = 2,
        receiveAlert = true,
        username = "Vinicius",
        profileIconPath = "",
        profileIconUpdatedAt = 0L
    )

    private fun buildAudio(id: Int, name: String = "Audio $id") = AudioEntity(
        uid = id,
        name = name,
        description = "Description $id",
        createdAt = "2024-01-01T00:00:00",
        audioPath = "",
        imagePath = "",
        isDefault = false,
        duration = 120000L,
        audioSize = 1024L,
        state = TranscriptionState.NONE
    )

    @Test
    fun homeTopBar_showsWelcomeAndUsername() {
        composeRule.setContent {
            AppTheme {
                HomeTopBar(
                    appSettings = fakeSettings,
                    unreadList = emptyList(),
                    onNotificationClick = {},
                    onNavigate = {},
                    onSearchClick = {}
                )
            }
        }

        composeRule.onNodeWithText("Welcome").assertIsDisplayed()
        composeRule.onNodeWithText("Vinicius").assertIsDisplayed()
    }

    @Test
    fun homeTopBar_clickingNotification_triggersCallback() {
        var clicked = false
        composeRule.setContent {
            AppTheme {
                HomeTopBar(
                    appSettings = fakeSettings,
                    unreadList = emptyList(),
                    onNotificationClick = { clicked = true },
                    onNavigate = {},
                    onSearchClick = {}
                )
            }
        }

        composeRule.onNode(
            hasContentDescription("Notification icon button")
        ).performClick()

        assertTrue(clicked)
    }

    @Test
    fun homeTopBar_clickingSearch_triggersCallback() {
        var searchClicked = false
        composeRule.setContent {
            AppTheme {
                HomeTopBar(
                    appSettings = fakeSettings,
                    unreadList = emptyList(),
                    onNotificationClick = {},
                    onNavigate = {},
                    onSearchClick = { searchClicked = true }
                )
            }
        }

        composeRule.onNode(
            hasContentDescription("Search icon button")
        ).performClick()

        assertTrue(searchClicked)
    }

    @Test
    fun homeScreen_withEmptyAudioList_showsEmptyState() {
        composeRule.setContent {
            AppTheme {
                HomeScreen(
                    homeController = FakeHomeViewModel(
                        HomeUiState(
                            audioList = emptyList(),
                            appSettings = fakeSettings,
                            isAudioLoading = false
                        )
                    ),
                    playerController = FakePlayerViewModel(),
                    notificationController = FakeNotificationViewModel(),
                    onNavigate = {}
                )
            }
        }

        composeRule.onNodeWithText("No audios found").assertIsDisplayed()
        composeRule.onNodeWithText("Create a Transcription").assertIsDisplayed()
    }

    @Test
    fun homeScreen_emptyState_createButton_triggersNavigation() {
        var navigated = false

        composeRule.setContent {
            AppTheme {
                HomeScreen(
                    homeController = FakeHomeViewModel(
                        HomeUiState(
                            audioList = emptyList(),
                            appSettings = fakeSettings,
                            isAudioLoading = false
                        )
                    ),
                    playerController = FakePlayerViewModel(),
                    notificationController = FakeNotificationViewModel(),
                    onNavigate = { navigated = true }
                )
            }
        }

        composeRule.onNodeWithText("Create a Transcription").performClick()
        assertTrue(navigated)
    }

    @Test
    fun homeScreen_whileLoading_showsLoadingText() {
        composeRule.setContent {
            AppTheme {
                HomeScreen(
                    homeController = FakeHomeViewModel(
                        HomeUiState(
                            audioList = emptyList(),
                            appSettings = fakeSettings,
                            isAudioLoading = true
                        )
                    ),
                    playerController = FakePlayerViewModel(),
                    notificationController = FakeNotificationViewModel(),
                    onNavigate = {}
                )
            }
        }

        composeRule.onNodeWithText("Loading audios, please wait...").assertIsDisplayed()
    }

    @Test
    fun homeScreen_withAudios_displaysAudioNames() {
        val audios = listOf(buildAudio(1, "My First Podcast"), buildAudio(2, "English Lesson 2"))

        composeRule.setContent {
            AppTheme {
                HomeScreen(
                    homeController = FakeHomeViewModel(
                        HomeUiState(
                            audioList = audios,
                            appSettings = fakeSettings,
                            isAudioLoading = false
                        )
                    ),
                    playerController = FakePlayerViewModel(),
                    notificationController = FakeNotificationViewModel(),
                    onNavigate = {}
                )
            }
        }

        composeRule.onNodeWithText("My First Podcast").assertIsDisplayed()
        composeRule.onNodeWithText("English Lesson 2").assertIsDisplayed()
    }
}
