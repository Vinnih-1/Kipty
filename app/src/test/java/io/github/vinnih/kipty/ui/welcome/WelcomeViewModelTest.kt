package io.github.vinnih.kipty.ui.welcome

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.github.vinnih.kipty.data.settings.AppSettings
import io.github.vinnih.kipty.domain.usecase.settings.GetAppSettingsUseCase
import io.github.vinnih.kipty.domain.usecase.settings.UpdateProfileIconUseCase
import io.github.vinnih.kipty.domain.usecase.settings.UpdateUsernameUseCase
import io.github.vinnih.kipty.domain.usecase.worker.PopulateDatabaseUseCase
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class WelcomeViewModelTest {

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()
    private val getAppSettingsUseCase = mockk<GetAppSettingsUseCase>()
    private val updateUsernameUseCase = mockk<UpdateUsernameUseCase>(relaxed = true)
    private val updateProfileIconUseCase = mockk<UpdateProfileIconUseCase>(relaxed = true)
    private val populateDatabaseUseCase = mockk<PopulateDatabaseUseCase>(relaxed = true)

    private val fakeSettings = AppSettings(
        showTimestamp = true,
        minimumThreads = 2,
        receiveAlert = true,
        username = "User",
        profileIconPath = "/files/profile_icon.png",
        profileIconUpdatedAt = 0L
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { getAppSettingsUseCase() } returns flowOf(fakeSettings)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = WelcomeViewModel(
        populateDatabaseUseCase = populateDatabaseUseCase,
        updateUsernameUseCase = updateUsernameUseCase,
        updateProfileIconUseCase = updateProfileIconUseCase,
        getAppSettingsUseCase = getAppSettingsUseCase
    )

    @Test
    fun `on init, should load username and profileIconPath from settings`() = runTest {
        val viewModel = buildViewModel()

        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("User", viewModel.uiState.value.username)
        assertEquals("/files/profile_icon.png", viewModel.uiState.value.profileIconPath)
    }

    @Test
    fun `nextStep should advance to next WelcomeStep`() = runTest {
        val viewModel = buildViewModel()
        val initialStep = viewModel.uiState.value.step

        viewModel.nextStep()

        val expected = WelcomeStep.entries[
            (initialStep.ordinal + 1).coerceAtMost(
                WelcomeStep.entries.size - 1
            )
        ]

        assertEquals(expected, viewModel.uiState.value.step)
    }

    @Test
    fun `nextStep on last step should not go beyond last`() = runTest {
        val viewModel = buildViewModel()

        repeat(WelcomeStep.entries.size + 5) { viewModel.nextStep() }
        assertEquals(WelcomeStep.entries.last(), viewModel.uiState.value.step)
    }

    @Test
    fun `previousStep should go back to previous WelcomeStep`() = runTest {
        val viewModel = buildViewModel()

        viewModel.nextStep()

        val afterNext = viewModel.uiState.value.step

        viewModel.previousStep()

        val expected = WelcomeStep.entries[(afterNext.ordinal - 1).coerceAtLeast(0)]

        assertEquals(expected, viewModel.uiState.value.step)
    }

    @Test
    fun `previousStep on first step should stay at first`() = runTest {
        val viewModel = buildViewModel()

        repeat(5) { viewModel.previousStep() }
        assertEquals(WelcomeStep.entries.first(), viewModel.uiState.value.step)
    }

    @Test
    fun `updateUsername should update uiState username`() = runTest {
        val viewModel = buildViewModel()

        viewModel.updateUsername("NewName")
        assertEquals("NewName", viewModel.uiState.value.username)
    }

    @Test
    fun `saveProfile should call updateUsernameUseCase with current username`() = runTest {
        val viewModel = buildViewModel()

        viewModel.updateUsername("Kipty")
        viewModel.saveProfile()
        coVerify { updateUsernameUseCase("Kipty") }
    }
}
