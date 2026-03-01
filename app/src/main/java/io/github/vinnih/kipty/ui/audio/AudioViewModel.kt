package io.github.vinnih.kipty.ui.audio

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.TranscriptionState
import io.github.vinnih.kipty.data.workers.TranscriptionWorker
import io.github.vinnih.kipty.domain.usecase.audio.CancelTranscriptionUseCase
import io.github.vinnih.kipty.domain.usecase.audio.DeleteAudioUseCase
import io.github.vinnih.kipty.domain.usecase.audio.GetAudioByIdUseCase
import io.github.vinnih.kipty.domain.usecase.audio.GetAudioFlowByIdUseCase
import io.github.vinnih.kipty.domain.usecase.audio.SaveAudioUseCase
import io.github.vinnih.kipty.domain.usecase.audio.TranscribeAudioUseCase
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class AudioUiState(val canTranscribe: Boolean = false, val currentUid: Int? = null)

@HiltViewModel
class AudioViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val transcribeAudioUseCase: TranscribeAudioUseCase,
    private val cancelTranscriptionUseCase: CancelTranscriptionUseCase,
    private val getAudioFlowByIdUseCase: GetAudioFlowByIdUseCase,
    private val getAudioByIdUseCase: GetAudioByIdUseCase,
    private val saveAudioUseCase: SaveAudioUseCase,
    private val deleteAudioUseCase: DeleteAudioUseCase
) : ViewModel(),
    AudioController {

    private val canTranscribe =
        WorkManager.getInstance(context).getWorkInfosByTagFlow(TranscriptionWorker.TAG)

    override val uiState: StateFlow<AudioUiState> = combine(canTranscribe) { workInfoArray ->
        val workInfoList = workInfoArray[0]
        val uid = workInfoList.firstOrNull()?.progress?.getInt("AUDIO_ID", -1)
        val canTranscribe = workInfoList.isEmpty() || workInfoList.all { it.state.isFinished }
        AudioUiState(canTranscribe, uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AudioUiState())

    override fun transcribeAudio(audioEntity: AudioEntity) {
        viewModelScope.launch {
            transcribeAudioUseCase(audioEntity).collect { state ->
                when (state) {
                    WorkInfo.State.CANCELLED, WorkInfo.State.FAILED -> {
                        saveAudioUseCase(audioEntity.copy(state = TranscriptionState.NONE))
                    }

                    else -> {}
                }
            }
        }
    }

    override fun getFlowById(id: Int): Flow<AudioEntity?> = getAudioFlowByIdUseCase(id)

    override suspend fun saveAudio(audioEntity: AudioEntity): Long =
        withContext(Dispatchers.IO) { saveAudioUseCase(audioEntity) }

    override suspend fun getById(id: Int): AudioEntity? =
        withContext(Dispatchers.IO) { getAudioByIdUseCase(id) }

    override fun deleteAudio(audioEntity: AudioEntity) {
        viewModelScope.launch(Dispatchers.IO) { deleteAudioUseCase(audioEntity) }
    }

    override fun cancelTranscriptionWork(audioEntity: AudioEntity) {
        cancelTranscriptionUseCase()
    }
}
