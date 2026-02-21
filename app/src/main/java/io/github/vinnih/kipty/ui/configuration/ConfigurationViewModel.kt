package io.github.vinnih.kipty.ui.configuration

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.vinnih.kipty.data.database.repository.audio.AudioRepository
import io.github.vinnih.kipty.data.settings.AppPreferencesRepository
import io.github.vinnih.kipty.data.settings.AppSettings
import io.github.vinnih.kipty.data.workers.AudioWorker
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ConfigurationsUiState(
    val canCreate: Boolean,
    val appSettings: AppSettings?,
    val isLoading: Boolean = appSettings == null
)

@HiltViewModel
class ConfigurationViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appPreferencesRepository: AppPreferencesRepository,
    private val audioRepository: AudioRepository
) : ViewModel(),
    ConfigurationController {

    private val canCreate = WorkManager.getInstance(
        context
    ).getWorkInfosByTagFlow(AudioWorker.TAG)

    private val appSettings = appPreferencesRepository.appSettingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    override val uiState: StateFlow<ConfigurationsUiState> = combine(canCreate, appSettings) {
            canCreate,
            appSettings
        ->
        val canCreate = canCreate.isEmpty() || canCreate.all { it.state.isFinished }
        ConfigurationsUiState(canCreate, appSettings)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ConfigurationsUiState(false, null)
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

    override fun updateProfileIcon(file: File) {
        viewModelScope.launch {
            val destination = File(context.filesDir, "profile_icon.png")
            file.copyTo(destination, overwrite = true)

            appPreferencesRepository.updateProfileIconPath(destination.absolutePath)
        }
    }

    override fun updateUsername(username: String) {
        viewModelScope.launch {
            appPreferencesRepository.updateUsername(username)
        }
    }
}
