package io.github.vinnih.kipty.domain.usecase.settings

import io.github.vinnih.kipty.data.settings.AppPreferencesRepository
import jakarta.inject.Inject

class UpdateMinimumThreadsUseCase @Inject constructor(
    private val repository: AppPreferencesRepository
) {
    suspend operator fun invoke(minimumThreads: Int) =
        repository.updateMinimumThreads(minimumThreads)
}
