package io.github.vinnih.kipty.ui.configuration

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.vinnih.kipty.data.settings.AppPreferencesRepository
import io.github.vinnih.kipty.data.settings.AppSettings
import io.github.vinnih.kipty.data.workers.AudioWorker
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ConfigurationsUiState(val canCreate: Boolean, val appSettings: AppSettings)

@HiltViewModel
class ConfigurationViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appPreferencesRepository: AppPreferencesRepository
) : ViewModel(),
    ConfigurationController {

    private val canCreate = WorkManager.getInstance(
        context
    ).getWorkInfosByTagFlow(AudioWorker.TAG)

    private val appSettings = appPreferencesRepository.appSettingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = AppSettings(true, 2, true)
    )

    override val uiState: StateFlow<ConfigurationsUiState> = combine(canCreate, appSettings) {
            canCreate,
            appSettings
        ->
        val canCreate = canCreate.isNotEmpty() && canCreate.all { it.state.isFinished }
        ConfigurationsUiState(canCreate, appSettings)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ConfigurationsUiState(false, AppSettings(true, 2, true))
    )

    override fun updateShowTimestamp(showTimestamp: Boolean) {
        viewModelScope.launch {
            appPreferencesRepository.updateShowTimestamp(showTimestamp)
        }
    }

    override fun updateMinimumThreads(minimumThreads: Int) {
        viewModelScope.launch {
            appPreferencesRepository.updateMinimumThreads(minimumThreads)
        }
    }

    override fun updateReceiveAlert(receiveAlert: Boolean) {
        viewModelScope.launch {
            appPreferencesRepository.updateReceiveAlert(receiveAlert)
        }
    }
}
