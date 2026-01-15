package io.github.vinnih.kipty.ui.configuration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.vinnih.kipty.data.settings.AppPreferencesRepository
import io.github.vinnih.kipty.data.settings.AppSettings
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ConfigurationViewModel @Inject constructor(
    private val appPreferencesRepository: AppPreferencesRepository
) : ViewModel(),
    ConfigurationController {

    override val uiState: StateFlow<AppSettings> = appPreferencesRepository.appSettingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppSettings(true, 2, true)
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
