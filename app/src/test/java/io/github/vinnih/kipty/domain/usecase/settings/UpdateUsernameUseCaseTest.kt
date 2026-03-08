package io.github.vinnih.kipty.domain.usecase.settings

import io.github.vinnih.kipty.data.settings.AppPreferencesRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class UpdateUsernameUseCaseTest {

    private val repository = mockk<AppPreferencesRepository>(relaxed = true)
    private val useCase = UpdateUsernameUseCase(repository)

    @Test
    fun `given valid username, should call repository with same username`() = runTest {
        useCase("User")
        coVerify { repository.updateUsername("User") }
    }

    @Test
    fun `given blank username, should call repository with default fallback`() = runTest {
        useCase("   ")
        coVerify { repository.updateUsername("Account User") }
    }

    @Test
    fun `given empty username, should call repository with default fallback`() = runTest {
        useCase("")
        coVerify { repository.updateUsername("Account User") }
    }
}
