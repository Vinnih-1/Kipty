package io.github.vinnih.kipty.domain.usecase.settings

import io.github.vinnih.kipty.data.settings.AppPreferencesRepository
import jakarta.inject.Inject

class UpdateShowTimestampUseCase @Inject constructor(
    private val repository: AppPreferencesRepository
) {
    suspend operator fun invoke(showTimestamp: Boolean) =
        repository.updateShowTimestamp(showTimestamp)
}
