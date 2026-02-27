package io.github.vinnih.kipty.domain.usecase.settings

import io.github.vinnih.kipty.data.settings.AppPreferencesRepository
import io.github.vinnih.kipty.data.settings.AppSettings
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetAppSettingsUseCase @Inject constructor(
    private val appPreferencesRepository: AppPreferencesRepository
) {
    operator fun invoke(): Flow<AppSettings?> = appPreferencesRepository.appSettingsFlow
}
