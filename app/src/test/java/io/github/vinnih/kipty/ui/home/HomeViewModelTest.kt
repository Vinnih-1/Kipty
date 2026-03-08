package io.github.vinnih.kipty.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.TranscriptionState
import io.github.vinnih.kipty.data.settings.AppSettings
import io.github.vinnih.kipty.domain.usecase.audio.GetAudiosUseCase
import io.github.vinnih.kipty.domain.usecase.audio.GetPlayTimeUseCase
import io.github.vinnih.kipty.domain.usecase.settings.GetAppSettingsUseCase
import io.github.vinnih.kipty.domain.usecase.settings.OpenNotificationSettingsUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private val getAudiosUseCase = mockk<GetAudiosUseCase>()
    private val getAppSettingsUseCase = mockk<GetAppSettingsUseCase>()
    private val getPlayTimeUseCase = mockk<GetPlayTimeUseCase>()
    private val openNotificationSettingsUseCase = mockk<OpenNotificationSettingsUseCase>(relaxed = true)

    private val fakeSettings = AppSettings(
        showTimestamp = true,
        minimumThreads = 2,
        receiveAlert = true,
        username = "Vinicius",
        profileIconPath = "",
        profileIconUpdatedAt = 0L
    )

    private fun buildAudio(id: Int) = AudioEntity(
        uid = id,
        name = "Audio $id",
        createdAt = "2024-01-01",
        audioPath = "path/audio.opus",
        imagePath = "path/image.jpg",
        isDefault = false,
        duration = 5000L,
        audioSize = 1024L,
        state = TranscriptionState.NONE
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = HomeViewModel(
        getAudiosUseCase = getAudiosUseCase,
        getAppSettingsUseCase = getAppSettingsUseCase,
        getPlayTimeUseCase = getPlayTimeUseCase,
        openNotificationSettingsUseCase = openNotificationSettingsUseCase
    )

    @Test
    fun `given audios and settings, uiState should reflect both`() = runTest {
        val audios = listOf(buildAudio(1), buildAudio(2))
        every { getAudiosUseCase() } returns flowOf(audios)
        every { getAppSettingsUseCase() } returns flowOf(fakeSettings)

        val viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.homeUiState.test {
            val state = awaitItem()
            assertEquals(audios, state.audioList)
            assertEquals(fakeSettings, state.appSettings)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given empty audio list, isAudioLoading should be true`() = runTest {
        every { getAudiosUseCase() } returns flowOf(emptyList())
        every { getAppSettingsUseCase() } returns flowOf(fakeSettings)

        val viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.homeUiState.test {
            assertTrue(awaitItem().isAudioLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given non-empty audio list, isAudioLoading should be false`() = runTest {
        every { getAudiosUseCase() } returns flowOf(listOf(buildAudio(1)))
        every { getAppSettingsUseCase() } returns flowOf(fakeSettings)

        val viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.homeUiState.test {
            assertFalse(awaitItem().isAudioLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given null settings, isSettingsLoading should be true`() = runTest {
        every { getAudiosUseCase() } returns flowOf(emptyList())
        every { getAppSettingsUseCase() } returns flowOf(null)

        val viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.homeUiState.test {
            assertTrue(awaitItem().isSettingsLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `openNotificationSettings should invoke use case`() = runTest {
        every { getAudiosUseCase() } returns flowOf(emptyList())
        every { getAppSettingsUseCase() } returns flowOf(fakeSettings)

        val viewModel = buildViewModel()
        viewModel.openNotificationSettings()

        verify { openNotificationSettingsUseCase() }
    }

    @Test
    fun `getPlayTimeById should delegate to use case`() = runTest {
        every { getAudiosUseCase() } returns flowOf(emptyList())
        every { getAppSettingsUseCase() } returns flowOf(fakeSettings)
        every { getPlayTimeUseCase(42) } returns flowOf(9999L)

        val viewModel = buildViewModel()

        viewModel.getPlayTimeById(42).test {
            assertEquals(9999L, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
