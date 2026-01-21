package io.github.vinnih.kipty.ui.home

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.repository.audio.AudioRepository
import io.github.vinnih.kipty.data.workers.PopulateWorker
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(val audioList: List<AudioEntity>, val isLoading: Boolean = true)

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val audioRepository: AudioRepository
) : ViewModel(),
    HomeController {

    private val _homeUiState = MutableStateFlow(HomeUiState(listOf()))

    override val homeUiState: StateFlow<HomeUiState> = _homeUiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(emptyList())
    )

    override fun getPlayTimeById(id: Int): Flow<Long> = audioRepository.getFlowPlayTimeById(id)

    override fun loadAudios() {
        if (_homeUiState.value.audioList.isNotEmpty()) return

        viewModelScope.launch(Dispatchers.IO) {
            audioRepository.getAllFlow().collect {
                _homeUiState.value = HomeUiState(it, false)
            }
        }
    }

    override fun openNotificationSettings() {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    override suspend fun populateDatabase(onSuccess: () -> Unit) {
        val request = OneTimeWorkRequestBuilder<PopulateWorker>().build()
        val workManager = WorkManager.getInstance(context)

        workManager.enqueueUniqueWork(
            "initial_setup_work",
            ExistingWorkPolicy.KEEP,
            request
        )

        viewModelScope.launch {
            workManager.getWorkInfoByIdFlow(request.id).collect {
                when (it?.state) {
                    androidx.work.WorkInfo.State.SUCCEEDED -> {
                        onSuccess.invoke()
                    }

                    else -> {}
                }
            }
        }
    }
}
