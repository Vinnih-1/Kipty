package io.github.vinnih.kipty.ui.configuration

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.work.WorkManager
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.TranscriptionState
import io.github.vinnih.kipty.data.settings.AppSettings
import io.github.vinnih.kipty.domain.usecase.audio.GetAudiosUseCase
import io.github.vinnih.kipty.domain.usecase.settings.GetAppSettingsUseCase
import io.github.vinnih.kipty.domain.usecase.settings.UpdateMinimumThreadsUseCase
import io.github.vinnih.kipty.domain.usecase.settings.UpdateProfileIconUseCase
import io.github.vinnih.kipty.domain.usecase.settings.UpdateReceiveAlertUseCase
import io.github.vinnih.kipty.domain.usecase.settings.UpdateShowTimestampUseCase
import io.github.vinnih.kipty.domain.usecase.settings.UpdateUsernameUseCase
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

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
        every { workManager.getWorkInfosByTagFlow(any()) } returns flowOf(emptyList())
        every { getAppSettingsUseCase() } returns flowOf(fakeSettings)
        every { getAudiosUseCase() } returns flowOf(listOf(buildAudio(1)))
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

    @Test
    fun `updateShowTimestamp should call use case`() = runTest {
        val viewModel = buildViewModel()
        viewModel.updateShowTimestamp(false)
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { updateShowTimestampUseCase(false) }
    }

    @Test
    fun `updateMinimumThreads should call use case`() = runTest {
        val viewModel = buildViewModel()
        viewModel.updateMinimumThreads(8)
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { updateMinimumThreadsUseCase(8) }
    }

    @Test
    fun `updateReceiveAlert should call use case`() = runTest {
        val viewModel = buildViewModel()
        viewModel.updateReceiveAlert(false)
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { updateReceiveAlertUseCase(false) }
    }

    @Test
    fun `updateUsername should call use case`() = runTest {
        val viewModel = buildViewModel()
        viewModel.updateUsername("NewName")
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { updateUsernameUseCase("NewName") }
    }

    @Test
    fun `updateProfileIcon should call use case with given file`() = runTest {
        val file = mockk<File>()
        val viewModel = buildViewModel()
        viewModel.updateProfileIcon(file)
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { updateProfileIconUseCase(file) }
    }
}
