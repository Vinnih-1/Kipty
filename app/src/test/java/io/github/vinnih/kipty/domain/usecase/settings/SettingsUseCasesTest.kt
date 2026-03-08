package io.github.vinnih.kipty.domain.usecase.settings

import io.github.vinnih.kipty.data.settings.AppPreferencesRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SettingsUseCasesTest {

    private val repository = mockk<AppPreferencesRepository>(relaxed = true)

    // --- UpdateShowTimestampUseCase ---

    @Test
    fun `UpdateShowTimestampUseCase should call repository with true`() = runTest {
        val useCase = UpdateShowTimestampUseCase(repository)
        useCase(true)
        coVerify { repository.updateShowTimestamp(true) }
    }

    @Test
    fun `UpdateShowTimestampUseCase should call repository with false`() = runTest {
        val useCase = UpdateShowTimestampUseCase(repository)
        useCase(false)
        coVerify { repository.updateShowTimestamp(false) }
    }

    // --- UpdateReceiveAlertUseCase ---

    @Test
    fun `UpdateReceiveAlertUseCase should call repository with true`() = runTest {
        val useCase = UpdateReceiveAlertUseCase(repository)
        useCase(true)
        coVerify { repository.updateReceiveAlert(true) }
    }

    @Test
    fun `UpdateReceiveAlertUseCase should call repository with false`() = runTest {
        val useCase = UpdateReceiveAlertUseCase(repository)
        useCase(false)
        coVerify { repository.updateReceiveAlert(false) }
    }

    // --- UpdateMinimumThreadsUseCase ---

    @Test
    fun `UpdateMinimumThreadsUseCase should call repository with given value`() = runTest {
        val useCase = UpdateMinimumThreadsUseCase(repository)
        useCase(4)
        coVerify { repository.updateMinimumThreads(4) }
    }

    @Test
    fun `UpdateMinimumThreadsUseCase should call repository with minimum value of 1`() = runTest {
        val useCase = UpdateMinimumThreadsUseCase(repository)
        useCase(1)
        coVerify { repository.updateMinimumThreads(1) }
    }
}
