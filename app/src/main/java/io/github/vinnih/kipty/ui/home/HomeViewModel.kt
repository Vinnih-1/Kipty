package io.github.vinnih.kipty.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.settings.AppSettings
import io.github.vinnih.kipty.domain.usecase.audio.GetAudiosUseCase
import io.github.vinnih.kipty.domain.usecase.audio.GetPlayTimeUseCase
import io.github.vinnih.kipty.domain.usecase.settings.GetAppSettingsUseCase
import io.github.vinnih.kipty.domain.usecase.settings.OpenNotificationSettingsUseCase
import io.github.vinnih.kipty.domain.usecase.worker.PopulateDatabaseUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val audioList: List<AudioEntity> = listOf(),
    val appSettings: AppSettings? = null,
    val isAudioLoading: Boolean = true,
    val isSettingsLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    getAudiosUseCase: GetAudiosUseCase,
    getAppSettingsUseCase: GetAppSettingsUseCase,
    private val getPlayTimeUseCase: GetPlayTimeUseCase,
    private val populateDatabaseUseCase: PopulateDatabaseUseCase,
    private val openNotificationSettingsUseCase: OpenNotificationSettingsUseCase
) : ViewModel(),
    HomeController {

    private val audioFlow = getAudiosUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = listOf()
    )

    private val appSettingsFlow = getAppSettingsUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    override val homeUiState: StateFlow<HomeUiState> = combine(audioFlow, appSettingsFlow) {
            audioList,
            appSettings
        ->
        HomeUiState(
            audioList = audioList,
            appSettings = appSettings,
            isAudioLoading = audioList.isEmpty(),
            isSettingsLoading = appSettings == null
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = HomeUiState()
    )

    override fun getPlayTimeById(id: Int): Flow<Long> = getPlayTimeUseCase(id)

    override fun openNotificationSettings(): Unit = openNotificationSettingsUseCase()

    override suspend fun populateDatabase(onSuccess: () -> Unit) {
        populateDatabaseUseCase(onSuccess)
    }
}
