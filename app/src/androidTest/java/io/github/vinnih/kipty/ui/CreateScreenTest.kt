package io.github.vinnih.kipty.ui

import android.net.Uri
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import io.github.vinnih.kipty.ui.create.CreateScreen
import io.github.vinnih.kipty.ui.create.CreateUiState
import io.github.vinnih.kipty.ui.create.FakeCreateViewModel
import io.github.vinnih.kipty.ui.create.Step
import io.github.vinnih.kipty.ui.theme.AppTheme
import io.mockk.mockk
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CreateScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun createScreen_initialStep_showsFileStep() {
        composeRule.setContent {
            AppTheme {
                CreateScreen(createController = FakeCreateViewModel(), onBack = {})
            }
        }

        composeRule.onNodeWithText("New Transcription").assertIsDisplayed()
        composeRule.onNodeWithText("Step 1 of 4").assertIsDisplayed()
        composeRule.onNodeWithText("Select audio file").assertIsDisplayed()
    }

    @Test
    fun createScreen_fileStep_showsTapToSelectAudio() {
        composeRule.setContent {
            AppTheme {
                CreateScreen(createController = FakeCreateViewModel(), onBack = {})
            }
        }

        composeRule.onNodeWithText("Tap to select audio").assertIsDisplayed()
        composeRule.onNodeWithText("MP3, WAV M4A supported").assertIsDisplayed()
    }

    @Test
    fun createScreen_fileStep_continueButtonIsDisabledWithNoUri() {
        composeRule.setContent {
            AppTheme {
                CreateScreen(createController = FakeCreateViewModel(), onBack = {})
            }
        }

        composeRule.onNode(hasText("Continue")).assertIsNotEnabled()
    }

    @Test
    fun createScreen_fileStep_continueButtonIsEnabledWithUri() {
        val fakeUri = mockk<Uri>(relaxed = true)

        composeRule.setContent {
            AppTheme {
                CreateScreen(
                    createController = FakeCreateViewModel(
                        CreateUiState(
                            step = Step.FILE,
                            data = CreateUiState.Data(audioUri = fakeUri)
                        )
                    ),
                    onBack = {}
                )
            }
        }

        composeRule.onNode(hasText("Continue")).assertIsEnabled()
    }

    @Test
    fun createScreen_backButton_triggersOnBack() {
        var backPressed = false
        composeRule.setContent {
            AppTheme {
                CreateScreen(
                    createController = FakeCreateViewModel(),
                    onBack = { backPressed = true }
                )
            }
        }

        composeRule.onNode(
            hasContentDescription("back button")
        ).performClick()

        assertTrue(backPressed)
    }

    @Test
    fun createScreen_detailsStep_showsTitleAndDescriptionFields() {
        composeRule.setContent {
            AppTheme {
                CreateScreen(
                    createController = FakeCreateViewModel(
                        CreateUiState(
                            step = Step.DETAILS,
                            data = CreateUiState.Data(audioUri = mockk(relaxed = true))
                        )
                    ),
                    onBack = {}
                )
            }
        }

        composeRule.onNodeWithText("Step 2 of 4").assertIsDisplayed()
        composeRule.onNodeWithText("Add details").assertIsDisplayed()
        composeRule.onNodeWithText("Title (Optional)").assertIsDisplayed()
        composeRule.onNodeWithText("Description (Optional)").assertIsDisplayed()
    }

    @Test
    fun createScreen_imageStep_showsCoverImageUI() {
        composeRule.setContent {
            AppTheme {
                CreateScreen(
                    createController = FakeCreateViewModel(
                        CreateUiState(
                            step = Step.IMAGE,
                            data = CreateUiState.Data(audioUri = mockk(relaxed = true))
                        )
                    ),
                    onBack = {}
                )
            }
        }

        composeRule.onNodeWithText("Step 3 of 4").assertIsDisplayed()
        composeRule.onNodeWithText("Add cover image").assertIsDisplayed()
        composeRule.onNodeWithText("Tap to select image").assertIsDisplayed()
        composeRule.onNodeWithText("JPG, PNG supported").assertIsDisplayed()
    }

    @Test
    fun createScreen_reviewStep_showsCreateTranscriptionButton() {
        val fakeUri = mockk<Uri>(relaxed = true)

        composeRule.setContent {
            AppTheme {
                CreateScreen(
                    createController = FakeCreateViewModel(
                        CreateUiState(
                            step = Step.REVIEW,
                            data = CreateUiState.Data(
                                audioUri = fakeUri,
                                title = "My Episode",
                                description = "A description"
                            )
                        )
                    ),
                    onBack = {}
                )
            }
        }

        composeRule.onNodeWithText("Step 4 of 4").assertIsDisplayed()
        composeRule.onNodeWithText("Review & create").assertIsDisplayed()
        composeRule.onNodeWithText("Create Transcription").assertIsDisplayed()
    }

    @Test
    fun createScreen_reviewStep_createButton_callsOnBack() {
        var backCalled = false
        val fakeUri = mockk<Uri>(relaxed = true)

        composeRule.setContent {
            AppTheme {
                CreateScreen(
                    createController = FakeCreateViewModel(
                        CreateUiState(
                            step = Step.REVIEW,
                            data = CreateUiState.Data(audioUri = fakeUri)
                        )
                    ),
                    onBack = { backCalled = true }
                )
            }
        }

        composeRule.onNodeWithText("Create Transcription").performClick()

        assertTrue(backCalled)
    }

    @Test
    fun createScreen_processingInfoSection_isVisible() {
        composeRule.setContent {
            AppTheme {
                CreateScreen(createController = FakeCreateViewModel(), onBack = {})
            }
        }

        composeRule.onNodeWithText("Processing information").assertIsDisplayed()
    }

    @Test
    fun createScreen_processingInfoSection_expandsOnClick() {
        composeRule.setContent {
            AppTheme {
                CreateScreen(createController = FakeCreateViewModel(), onBack = {})
            }
        }

        composeRule.onNodeWithText("Processing information").performClick()
        composeRule.onNodeWithText("This may take some time.").assertIsDisplayed()
    }
}
