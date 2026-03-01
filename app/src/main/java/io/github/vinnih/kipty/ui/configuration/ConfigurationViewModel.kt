package io.github.vinnih.kipty.ui.configuration

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.settings.AppSettings
import io.github.vinnih.kipty.data.workers.AudioWorker
import io.github.vinnih.kipty.domain.usecase.audio.GetAudiosUseCase
import io.github.vinnih.kipty.domain.usecase.settings.GetAppSettingsUseCase
import io.github.vinnih.kipty.domain.usecase.settings.UpdateMinimumThreadsUseCase
import io.github.vinnih.kipty.domain.usecase.settings.UpdateProfileIconUseCase
import io.github.vinnih.kipty.domain.usecase.settings.UpdateReceiveAlertUseCase
import io.github.vinnih.kipty.domain.usecase.settings.UpdateShowTimestampUseCase
import io.github.vinnih.kipty.domain.usecase.settings.UpdateUsernameUseCase
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
    getAppSettingsUseCase: GetAppSettingsUseCase,
    getAudiosUseCase: GetAudiosUseCase,
    private val updateShowTimestampUseCase: UpdateShowTimestampUseCase,
    private val updateMinimumThreadsUseCase: UpdateMinimumThreadsUseCase,
    private val updateReceiveAlertUseCase: UpdateReceiveAlertUseCase,
    private val updateProfileIconUseCase: UpdateProfileIconUseCase,
    private val updateUsernameUseCase: UpdateUsernameUseCase
) : ViewModel(),
    ConfigurationController {

    private val canCreate =
        WorkManager.getInstance(context).getWorkInfosByTagFlow(AudioWorker.TAG)

    private val appSettings = getAppSettingsUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    private val audioList = getAudiosUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    override val uiState: StateFlow<ConfigurationsUiState> = combine(
        canCreate,
        appSettings,
        audioList
    ) { canCreate, appSettings, audioList ->
        ConfigurationsUiState(
            canCreate = canCreate.isEmpty() || canCreate.all { it.state.isFinished },
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
        viewModelScope.launch { updateShowTimestampUseCase(showTimestamp) }
    }

    override fun updateMinimumThreads(minimumThreads: Int) {
        viewModelScope.launch { updateMinimumThreadsUseCase(minimumThreads) }
    }

    override fun updateReceiveAlert(receiveAlert: Boolean) {
        viewModelScope.launch { updateReceiveAlertUseCase(receiveAlert) }
    }

    override fun updateProfileIcon(file: File) {
        viewModelScope.launch { updateProfileIconUseCase(file) }
    }

    override fun updateUsername(username: String) {
        viewModelScope.launch { updateUsernameUseCase(username) }
    }
}
