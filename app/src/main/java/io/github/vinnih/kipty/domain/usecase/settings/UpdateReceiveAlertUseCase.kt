package io.github.vinnih.kipty.domain.usecase.settings

import io.github.vinnih.kipty.data.settings.AppPreferencesRepository
import jakarta.inject.Inject

class UpdateReceiveAlertUseCase @Inject constructor(
    private val repository: AppPreferencesRepository
) {
    suspend operator fun invoke(receiveAlert: Boolean) =
        repository.updateReceiveAlert(receiveAlert)
}
