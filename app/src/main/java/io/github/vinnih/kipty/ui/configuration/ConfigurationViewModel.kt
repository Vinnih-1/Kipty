package io.github.vinnih.kipty.ui.configuration

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.vinnih.kipty.data.database.entity.AudioEntity
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
    val canCreate: Boolean = false,
    val appSettings: AppSettings? = null,
    val audioList: List<AudioEntity> = listOf(),
    val isLoadingSettings: Boolean = true,
    val isLoadingAudioList: Boolean = true
)

@HiltViewModel
class ConfigurationViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appPreferencesRepository: AppPreferencesRepository,
    audioRepository: AudioRepository
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

    val audioList = audioRepository.getAllFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    override val uiState: StateFlow<ConfigurationsUiState> = combine(
        canCreate,
        appSettings,
        audioList
    ) {
            canCreate,
            appSettings,
            audioList
        ->
        val canCreate = canCreate.isEmpty() || canCreate.all { it.state.isFinished }
        ConfigurationsUiState(
            canCreate = canCreate,
            appSettings = appSettings,
            audioList = audioList ?: listOf(),
            isLoadingSettings = appSettings == null,
            isLoadingAudioList = audioList == null
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ConfigurationsUiState()
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
