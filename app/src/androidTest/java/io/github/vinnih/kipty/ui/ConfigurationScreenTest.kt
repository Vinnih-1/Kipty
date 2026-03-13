package io.github.vinnih.kipty.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import io.github.vinnih.kipty.R
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.TranscriptionState
import io.github.vinnih.kipty.data.settings.AppSettings
import io.github.vinnih.kipty.ui.configuration.ConfigurationScreen
import io.github.vinnih.kipty.ui.configuration.ConfigurationsUiState
import io.github.vinnih.kipty.ui.configuration.FakeConfigurationViewModel
import io.github.vinnih.kipty.ui.configuration.StatItem
import io.github.vinnih.kipty.ui.configuration.StatsRow
import io.github.vinnih.kipty.ui.configuration.SwitchSettingItem
import io.github.vinnih.kipty.ui.configuration.UsernameSection
import io.github.vinnih.kipty.ui.theme.AppTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ConfigurationScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val fakeSettings = AppSettings(
        showTimestamp = true,
        minimumThreads = 4,
        receiveAlert = true,
        username = "Vinicius",
        profileIconPath = "",
        profileIconUpdatedAt = 0L
    )

    private fun buildAudio(id: Int, transcribed: Boolean = false) = AudioEntity(
        uid = id,
        name = "Audio $id",
        createdAt = "2024-01-01T00:00:00",
        audioPath = "",
        imagePath = "",
        isDefault = false,
        duration = 60000L,
        audioSize = 1024L,
        state = TranscriptionState.NONE,
        transcription = if (transcribed) emptyList() else null
    )

    @Test
    fun configurationScreen_whileLoading_rendersNothing() {
        composeRule.setContent {
            AppTheme {
                ConfigurationScreen(
                    configurationController = FakeConfigurationViewModel(
                        ConfigurationsUiState(
                            isLoadingSettings = true,
                            appSettings = null
                        )
                    ),
                    onNavigate = {},
                    onBack = {}
                )
            }
        }

        composeRule.onNodeWithText("Settings").assertDoesNotExist()
    }

    @Test
    fun configurationScreen_showsSettingsTitle() {
        composeRule.setContent {
            AppTheme {
                ConfigurationScreen(
                    configurationController = FakeConfigurationViewModel(
                        ConfigurationsUiState(
                            isLoadingSettings = false,
                            appSettings = fakeSettings
                        )
                    ),
                    onNavigate = {},
                    onBack = {}
                )
            }
        }

        composeRule.onNodeWithText("Settings").assertIsDisplayed()
        composeRule.onNodeWithText("Customize your experience").assertIsDisplayed()
    }

    @Test
    fun configurationScreen_backButton_triggersOnBack() {
        var backPressed = false

        composeRule.setContent {
            AppTheme {
                ConfigurationScreen(
                    configurationController = FakeConfigurationViewModel(
                        ConfigurationsUiState(
                            isLoadingSettings = false,
                            appSettings = fakeSettings
                        )
                    ),
                    onNavigate = {},
                    onBack = { backPressed = true }
                )
            }
        }

        composeRule.onNode(
            hasContentDescription("back button") and hasClickAction()
        ).performClick()

        assertTrue(backPressed)
    }

    @Test
    fun configurationScreen_showsTranscriptionSection() {
        composeRule.setContent {
            AppTheme {
                ConfigurationScreen(
                    configurationController = FakeConfigurationViewModel(
                        ConfigurationsUiState(
                            isLoadingSettings = false,
                            appSettings = fakeSettings
                        )
                    ),
                    onNavigate = {},
                    onBack = {}
                )
            }
        }

        composeRule.onNodeWithText("TRANSCRIPTION").assertIsDisplayed()
        composeRule.onNodeWithText("Show timestamp").assertIsDisplayed()
        composeRule.onNodeWithText("Processing threads").assertIsDisplayed()
    }

    @Test
    fun configurationScreen_showsNotificationsSection() {
        composeRule.setContent {
            AppTheme {
                ConfigurationScreen(
                    configurationController = FakeConfigurationViewModel(
                        ConfigurationsUiState(
                            isLoadingSettings = false,
                            appSettings = fakeSettings
                        )
                    ),
                    onNavigate = {},
                    onBack = {}
                )
            }
        }

        composeRule.onNodeWithText("Notifications").performScrollTo()

        composeRule.onNodeWithText("NOTIFICATIONS").assertIsDisplayed()
        composeRule.onNodeWithText("Notifications").assertIsDisplayed()
    }

    @Test
    fun configurationScreen_showsAboutSection() {
        composeRule.setContent {
            AppTheme {
                ConfigurationScreen(
                    configurationController = FakeConfigurationViewModel(
                        ConfigurationsUiState(
                            isLoadingSettings = false,
                            appSettings = fakeSettings
                        )
                    ),
                    onNavigate = {},
                    onBack = {}
                )
            }
        }

        composeRule.onNodeWithText("App version").performScrollTo()

        composeRule.onNodeWithText("ABOUT").assertIsDisplayed()
        composeRule.onNodeWithText("Send feedback").assertIsDisplayed()
        composeRule.onNodeWithText("Rate the app").assertIsDisplayed()
    }

    @Test
    fun switchSettingItem_whenCheckedTrue_switchIsOn() {
        composeRule.setContent {
            AppTheme {
                SwitchSettingItem(
                    title = "Show timestamp",
                    description = "Display time markers",
                    iconRes = R.drawable.schedule,
                    checked = true,
                    onCheckedChange = {}
                )
            }
        }

        composeRule.onNode(isToggleable()).assertIsOn()
    }

    @Test
    fun switchSettingItem_whenCheckedFalse_switchIsOff() {
        composeRule.setContent {
            AppTheme {
                SwitchSettingItem(
                    title = "Show timestamp",
                    description = "Display time markers",
                    iconRes = R.drawable.schedule,
                    checked = false,
                    onCheckedChange = {}
                )
            }
        }

        composeRule.onNode(isToggleable()).assertIsOff()
    }

    @Test
    fun switchSettingItem_toggling_callsCallback() {
        var toggled = false
        composeRule.setContent {
            AppTheme {
                SwitchSettingItem(
                    title = "Show timestamp",
                    description = "Display time markers",
                    iconRes = R.drawable.schedule,
                    checked = true,
                    onCheckedChange = { toggled = true }
                )
            }
        }

        composeRule.onNode(isToggleable()).performClick()
        assertTrue(toggled)
    }

    @Test
    fun usernameSection_showsCurrentUsername() {
        composeRule.setContent {
            AppTheme {
                UsernameSection(username = "Vinicius", onUsernameChange = {})
            }
        }

        composeRule.onNodeWithText("Vinicius").assertIsDisplayed()
    }

    @Test
    fun statsRow_showsAudioCount() {
        val audios = listOf(buildAudio(1), buildAudio(2), buildAudio(3))
        composeRule.setContent {
            AppTheme {
                StatsRow(audioList = audios, isLoading = false)
            }
        }

        composeRule.onNodeWithText("3").assertIsDisplayed()
        composeRule.onNodeWithText("Audios").assertIsDisplayed()
    }

    @Test
    fun statsRow_showsTranscribedCount() {
        val audios = listOf(buildAudio(1, transcribed = true), buildAudio(2), buildAudio(3))
        composeRule.setContent {
            AppTheme {
                StatsRow(audioList = audios, isLoading = false)
            }
        }

        composeRule.onNodeWithText("1").assertIsDisplayed()
        composeRule.onNodeWithText("Transcribed").assertIsDisplayed()
    }

    @Test
    fun statItem_displaysCountAndLabel() {
        composeRule.setContent {
            AppTheme {
                StatItem(count = "42", label = "Episodes")
            }
        }

        composeRule.onNodeWithText("42").assertIsDisplayed()
        composeRule.onNodeWithText("Episodes").assertIsDisplayed()
    }

    @Test
    fun configurationScreen_showsUsername() {
        composeRule.setContent {
            AppTheme {
                ConfigurationScreen(
                    configurationController = FakeConfigurationViewModel(
                        ConfigurationsUiState(
                            isLoadingSettings = false,
                            appSettings = fakeSettings
                        )
                    ),
                    onNavigate = {},
                    onBack = {}
                )
            }
        }

        composeRule.onNodeWithText("Vinicius").assertIsDisplayed()
    }
}
