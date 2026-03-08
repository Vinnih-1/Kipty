package io.github.vinnih.kipty.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.github.vinnih.kipty.MainViewModel
import io.github.vinnih.kipty.Screen
import io.github.vinnih.kipty.data.settings.AppPreferencesRepository
import io.github.vinnih.kipty.data.settings.AppSettings
import io.github.vinnih.kipty.domain.usecase.settings.GetAppSettingsUseCase
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
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()
    private val getAppSettingsUseCase = mockk<GetAppSettingsUseCase>()
    private val repository = mockk<AppPreferencesRepository>()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildSettings(username: String) = AppSettings(
        showTimestamp = true,
        minimumThreads = 2,
        receiveAlert = true,
        username = username,
        profileIconPath = "",
        profileIconUpdatedAt = 0L
    )

    @Test
    fun `given blank username, initialScreen should be Welcome`() = runTest {
        every { getAppSettingsUseCase() } returns flowOf(buildSettings(""))
        every { repository.hasDatabasePopulatedFlow } returns flowOf(false)

        val viewModel = MainViewModel(getAppSettingsUseCase, repository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(Screen.Welcome, viewModel.initialScreen.value)
    }

    @Test
    fun `given non-blank username, initialScreen should be Home`() = runTest {
        every { getAppSettingsUseCase() } returns flowOf(buildSettings("User"))
        every { repository.hasDatabasePopulatedFlow } returns flowOf(true)

        val viewModel = MainViewModel(getAppSettingsUseCase, repository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(Screen.Home, viewModel.initialScreen.value)
    }

    @Test
    fun `given blank username, keepSplash should become false immediately`() = runTest {
        every { getAppSettingsUseCase() } returns flowOf(buildSettings(""))
        every { repository.hasDatabasePopulatedFlow } returns flowOf(false)

        val viewModel = MainViewModel(getAppSettingsUseCase, repository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.keepSplash.value)
    }

    @Test
    fun `given username set and database populated, keepSplash should become false`() = runTest {
        every { getAppSettingsUseCase() } returns flowOf(buildSettings("User"))
        every { repository.hasDatabasePopulatedFlow } returns flowOf(true)

        val viewModel = MainViewModel(getAppSettingsUseCase, repository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.keepSplash.value)
    }
}
