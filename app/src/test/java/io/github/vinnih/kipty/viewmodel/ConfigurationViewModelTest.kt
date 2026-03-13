package io.github.vinnih.kipty.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.work.WorkInfo
import androidx.work.WorkManager
import app.cash.turbine.test
import io.github.vinnih.kipty.data.settings.AppSettings
import io.github.vinnih.kipty.domain.usecase.audio.GetAudiosUseCase
import io.github.vinnih.kipty.domain.usecase.settings.GetAppSettingsUseCase
import io.github.vinnih.kipty.domain.usecase.settings.UpdateMinimumThreadsUseCase
import io.github.vinnih.kipty.domain.usecase.settings.UpdateProfileIconUseCase
import io.github.vinnih.kipty.domain.usecase.settings.UpdateReceiveAlertUseCase
import io.github.vinnih.kipty.domain.usecase.settings.UpdateShowTimestampUseCase
import io.github.vinnih.kipty.domain.usecase.settings.UpdateUsernameUseCase
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.TranscriptionState
import io.github.vinnih.kipty.ui.configuration.ConfigurationViewModel
import io.mockk.coVerify
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File

class ConfigurationViewModelTest {

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private val workManager = mockk<WorkManager>(relaxed = true)
    private val getAppSettingsUseCase = mockk<GetAppSettingsUseCase>()
    private val getAudiosUseCase = mockk<GetAudiosUseCase>()
    private val updateShowTimestampUseCase = mockk<UpdateShowTimestampUseCase>(relaxed = true)
    private val updateMinimumThreadsUseCase = mockk<UpdateMinimumThreadsUseCase>(relaxed = true)
    private val updateReceiveAlertUseCase = mockk<UpdateReceiveAlertUseCase>(relaxed = true)
    private val updateProfileIconUseCase = mockk<UpdateProfileIconUseCase>(relaxed = true)
    private val updateUsernameUseCase = mockk<UpdateUsernameUseCase>(relaxed = true)

    private val fakeSettings = AppSettings(
        showTimestamp = false,
        minimumThreads = 4,
        receiveAlert = true,
        username = "Vinicius",
        profileIconPath = "/files/profile.png",
        profileIconUpdatedAt = 123456789L
    )

    private fun buildAudio(id: Int) = AudioEntity(
        uid = id,
        name = "Audio $id",
        createdAt = "2024-01-01",
        audioPath = "path/audio.opus",
        imagePath = "path/image.jpg",
        isDefault = false,
        duration = 60000L,
        audioSize = 2048L,
        state = TranscriptionState.NONE
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        // Default: no work running
        every { workManager.getWorkInfosByTagFlow(any()) } returns flowOf(emptyList())
        every { getAppSettingsUseCase() } returns flowOf(fakeSettings)
        every { getAudiosUseCase() } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = ConfigurationViewModel(
        workManager = workManager,
        getAppSettingsUseCase = getAppSettingsUseCase,
        getAudiosUseCase = getAudiosUseCase,
        updateShowTimestampUseCase = updateShowTimestampUseCase,
        updateMinimumThreadsUseCase = updateMinimumThreadsUseCase,
        updateReceiveAlertUseCase = updateReceiveAlertUseCase,
        updateProfileIconUseCase = updateProfileIconUseCase,
        updateUsernameUseCase = updateUsernameUseCase
    )

    // ── Initial / loaded state ────────────────────────────────────────────────

    @Test
    fun `given settings loaded, uiState should reflect appSettings`() = runTest {
        val vm = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        vm.uiState.test {
            val state = awaitItem()
            assertEquals(fakeSettings, state.appSettings)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given null settings, isLoadingSettings should be true`() = runTest {
        every { getAppSettingsUseCase() } returns flowOf(null)
        val vm = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        vm.uiState.test {
            assertTrue(awaitItem().isLoadingSettings)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given settings present, isLoadingSettings should be false`() = runTest {
        val vm = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        vm.uiState.test {
            assertFalse(awaitItem().isLoadingSettings)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given audio list loaded, uiState should reflect audioList`() = runTest {
        val audios = listOf(buildAudio(1), buildAudio(2))
        every { getAudiosUseCase() } returns flowOf(audios)
        val vm = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        vm.uiState.test {
            val state = awaitItem()
            assertEquals(audios, state.audioList)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given no running work, canCreate should be true`() = runTest {
        every { workManager.getWorkInfosByTagFlow(any()) } returns flowOf(emptyList())
        val vm = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        vm.uiState.test {
            assertTrue(awaitItem().canCreate)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given finished work, canCreate should be true`() = runTest {
        val finishedWork = mockk<WorkInfo> {
            every { state } returns WorkInfo.State.SUCCEEDED
        }
        every { workManager.getWorkInfosByTagFlow(any()) } returns flowOf(listOf(finishedWork))
        val vm = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        vm.uiState.test {
            assertTrue(awaitItem().canCreate)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given running work, canCreate should be false`() = runTest {
        val runningWork = mockk<WorkInfo> {
            every { state } returns WorkInfo.State.RUNNING
        }
        every { workManager.getWorkInfosByTagFlow(any()) } returns flowOf(listOf(runningWork))
        val vm = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        vm.uiState.test {
            assertFalse(awaitItem().canCreate)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ── updateShowTimestamp ───────────────────────────────────────────────────

    @Test
    fun `updateShowTimestamp true should invoke use case with true`() = runTest {
        val vm = buildViewModel()

        vm.updateShowTimestamp(true)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { updateShowTimestampUseCase(true) }
    }

    @Test
    fun `updateShowTimestamp false should invoke use case with false`() = runTest {
        val vm = buildViewModel()

        vm.updateShowTimestamp(false)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { updateShowTimestampUseCase(false) }
    }

    // ── updateMinimumThreads ──────────────────────────────────────────────────

    @Test
    fun `updateMinimumThreads should invoke use case with the given value`() = runTest {
        val vm = buildViewModel()

        vm.updateMinimumThreads(8)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { updateMinimumThreadsUseCase(8) }
    }

    @Test
    fun `updateMinimumThreads with 1 should invoke use case with 1`() = runTest {
        val vm = buildViewModel()

        vm.updateMinimumThreads(1)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { updateMinimumThreadsUseCase(1) }
    }

    // ── updateReceiveAlert ────────────────────────────────────────────────────

    @Test
    fun `updateReceiveAlert true should invoke use case with true`() = runTest {
        val vm = buildViewModel()

        vm.updateReceiveAlert(true)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { updateReceiveAlertUseCase(true) }
    }

    @Test
    fun `updateReceiveAlert false should invoke use case with false`() = runTest {
        val vm = buildViewModel()

        vm.updateReceiveAlert(false)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { updateReceiveAlertUseCase(false) }
    }

    // ── updateProfileIcon ─────────────────────────────────────────────────────

    @Test
    fun `updateProfileIcon should invoke use case with the given file`() = runTest {
        val vm = buildViewModel()
        val fakeFile = mockk<File>()

        vm.updateProfileIcon(fakeFile)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { updateProfileIconUseCase(fakeFile) }
    }

    // ── updateUsername ────────────────────────────────────────────────────────

    @Test
    fun `updateUsername should invoke use case with the given string`() = runTest {
        val vm = buildViewModel()

        vm.updateUsername("Kipty User")
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { updateUsernameUseCase("Kipty User") }
    }

    @Test
    fun `updateUsername with empty string should invoke use case with empty string`() = runTest {
        val vm = buildViewModel()

        vm.updateUsername("")
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { updateUsernameUseCase("") }
    }
}
