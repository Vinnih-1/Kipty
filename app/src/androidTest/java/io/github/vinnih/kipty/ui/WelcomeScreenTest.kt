package io.github.vinnih.kipty.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import io.github.vinnih.kipty.ui.theme.AppTheme
import io.github.vinnih.kipty.ui.welcome.FakeWelcomeViewModel
import io.github.vinnih.kipty.ui.welcome.WelcomeScreen
import io.github.vinnih.kipty.ui.welcome.WelcomeStep
import io.github.vinnih.kipty.ui.welcome.WelcomeUiState
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class WelcomeScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun welcomeScreen_intro_showsAppName() {
        composeRule.setContent {
            AppTheme {
                WelcomeScreen(
                    welcomeController = FakeWelcomeViewModel(
                        WelcomeUiState(step = WelcomeStep.INTRO, username = "")
                    ),
                    onGetStarted = {},
                    onDatabasePopulated = {}
                )
            }
        }

        composeRule.onNodeWithText("Kipty").assertIsDisplayed()
    }

    @Test
    fun welcomeScreen_intro_showsWelcomeTitle() {
        composeRule.setContent {
            AppTheme {
                WelcomeScreen(
                    welcomeController = FakeWelcomeViewModel(
                        WelcomeUiState(step = WelcomeStep.INTRO, username = "")
                    ),
                    onGetStarted = {},
                    onDatabasePopulated = {}
                )
            }
        }

        composeRule.onNodeWithText("Welcome to Kipty").assertIsDisplayed()
    }

    @Test
    fun welcomeScreen_intro_showsFeatureItems() {
        composeRule.setContent {
            AppTheme {
                WelcomeScreen(
                    welcomeController = FakeWelcomeViewModel(
                        WelcomeUiState(step = WelcomeStep.INTRO, username = "")
                    ),
                    onGetStarted = {},
                    onDatabasePopulated = {}
                )
            }
        }

        composeRule.onNodeWithText("Listen to podcasts").assertIsDisplayed()
        composeRule.onNodeWithText("Read along").assertIsDisplayed()
        composeRule.onNodeWithText("Practice pronunciation").assertIsDisplayed()
    }

    @Test
    fun welcomeScreen_intro_showsGetStartedButton() {
        composeRule.setContent {
            AppTheme {
                WelcomeScreen(
                    welcomeController = FakeWelcomeViewModel(
                        WelcomeUiState(step = WelcomeStep.INTRO, username = "")
                    ),
                    onGetStarted = {},
                    onDatabasePopulated = {}
                )
            }
        }

        composeRule.onNodeWithText("Get started").performScrollTo()

        composeRule.onNodeWithText("Get started").assertIsDisplayed()
    }

    @Test
    fun welcomeScreen_profileSetup_showsSetupTitle() {
        composeRule.setContent {
            AppTheme {
                WelcomeScreen(
                    welcomeController = FakeWelcomeViewModel(
                        WelcomeUiState(step = WelcomeStep.PROFILE_SETUP, username = "")
                    ),
                    onGetStarted = {},
                    onDatabasePopulated = {}
                )
            }
        }

        composeRule.onNodeWithText("Set up your profile").assertIsDisplayed()
    }

    @Test
    fun welcomeScreen_profileSetup_showsNameLabel() {
        composeRule.setContent {
            AppTheme {
                WelcomeScreen(
                    welcomeController = FakeWelcomeViewModel(
                        WelcomeUiState(step = WelcomeStep.PROFILE_SETUP, username = "")
                    ),
                    onGetStarted = {},
                    onDatabasePopulated = {}
                )
            }
        }

        composeRule.onNodeWithText("YOUR NAME").assertIsDisplayed()
    }

    @Test
    fun welcomeScreen_profileSetup_showsContinueButton() {
        composeRule.setContent {
            AppTheme {
                WelcomeScreen(
                    welcomeController = FakeWelcomeViewModel(
                        WelcomeUiState(step = WelcomeStep.PROFILE_SETUP, username = "")
                    ),
                    onGetStarted = {},
                    onDatabasePopulated = {}
                )
            }
        }

        composeRule.onNodeWithText("Continue").assertIsDisplayed()
    }

    @Test
    fun welcomeScreen_profileSetup_showsHintText() {
        composeRule.setContent {
            AppTheme {
                WelcomeScreen(
                    welcomeController = FakeWelcomeViewModel(
                        WelcomeUiState(step = WelcomeStep.PROFILE_SETUP, username = "")
                    ),
                    onGetStarted = {},
                    onDatabasePopulated = {}
                )
            }
        }

        composeRule.onNodeWithText(
            "This will be displayed on your home screen."
        ).assertIsDisplayed()
    }

    @Test
    fun welcomeScreen_allSet_showsAllSetTitle() {
        composeRule.setContent {
            AppTheme {
                WelcomeScreen(
                    welcomeController = FakeWelcomeViewModel(
                        WelcomeUiState(step = WelcomeStep.ALL_SET, username = "Vinicius")
                    ),
                    onGetStarted = {},
                    onDatabasePopulated = {}
                )
            }
        }

        composeRule.onNodeWithText("You're all set!").assertIsDisplayed()
    }

    @Test
    fun welcomeScreen_allSet_showsWelcomeUsername() {
        composeRule.setContent {
            AppTheme {
                WelcomeScreen(
                    welcomeController = FakeWelcomeViewModel(
                        WelcomeUiState(step = WelcomeStep.ALL_SET, username = "Vinicius")
                    ),
                    onGetStarted = {},
                    onDatabasePopulated = {}
                )
            }
        }

        composeRule.onAllNodesWithText("Vinicius").onLast().assertIsDisplayed()
    }

    @Test
    fun welcomeScreen_allSet_showsGoToHomeButton() {
        composeRule.setContent {
            AppTheme {
                WelcomeScreen(
                    welcomeController = FakeWelcomeViewModel(
                        WelcomeUiState(step = WelcomeStep.ALL_SET, username = "Vinicius")
                    ),
                    onGetStarted = {},
                    onDatabasePopulated = {}
                )
            }
        }

        composeRule.onNodeWithText("Go to Home").assertIsDisplayed()
    }

    @Test
    fun welcomeScreen_allSet_clickGoToHome_callsOnGetStarted() {
        var getStartedCalled = false
        composeRule.setContent {
            AppTheme {
                WelcomeScreen(
                    welcomeController = FakeWelcomeViewModel(
                        WelcomeUiState(step = WelcomeStep.ALL_SET, username = "Vinicius")
                    ),
                    onGetStarted = { getStartedCalled = true },
                    onDatabasePopulated = {}
                )
            }
        }

        composeRule.onNodeWithText("Go to Home").performClick()
        composeRule.mainClock.advanceTimeBy(300)

        assertTrue(getStartedCalled)
    }

    @Test
    fun welcomeScreen_allSet_fallbackUsername_whenBlank() {
        composeRule.setContent {
            AppTheme {
                WelcomeScreen(
                    welcomeController = FakeWelcomeViewModel(
                        WelcomeUiState(step = WelcomeStep.ALL_SET, username = "")
                    ),
                    onGetStarted = {},
                    onDatabasePopulated = {}
                )
            }
        }

        composeRule.onAllNodesWithText("Account User").onLast().assertIsDisplayed()
    }
}
