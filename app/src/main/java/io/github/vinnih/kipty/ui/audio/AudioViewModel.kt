package io.github.vinnih.kipty.ui.audio

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.TranscriptionState
import io.github.vinnih.kipty.data.database.repository.audio.AudioRepository
import io.github.vinnih.kipty.data.workers.TranscriptionWorker
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class AudioUiState(val canTranscribe: Boolean = false, val currentUid: Int = -1)

@HiltViewModel
class AudioViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: AudioRepository
) : ViewModel(),
    AudioController {

    private val workManager = WorkManager.getInstance(context)

    private val canTranscribe = workManager.getWorkInfosByTagFlow(TranscriptionWorker.TAG)

    override val uiState: StateFlow<AudioUiState> = combine(canTranscribe) { workInfoArray ->
        val workInfoList = workInfoArray[0]
        val uid = workInfoList.firstOrNull()?.progress?.getInt("AUDIO_ID", -1)
        val canTranscribe = workInfoList.isEmpty() || workInfoList.all { it.state.isFinished }
        AudioUiState(canTranscribe, uid!!)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AudioUiState())

    override fun transcribeAudio(audioEntity: AudioEntity) {
        val request = OneTimeWorkRequestBuilder<TranscriptionWorker>()
            .setInputData(Data.Builder().putInt("AUDIO_ID", audioEntity.uid).build())
            .addTag(TranscriptionWorker.TAG)
            .build()

        workManager.enqueueUniqueWork(
            "transcript_audio_process",
            ExistingWorkPolicy.KEEP,
            request
        )

        viewModelScope.launch {
            workManager.getWorkInfoByIdFlow(request.id).collect { workInfo ->
                when (workInfo?.state) {
                    WorkInfo.State.CANCELLED -> {
                        repository.updateAudioState(audioEntity.uid, TranscriptionState.NONE)
                    }

                    WorkInfo.State.FAILED -> {
                        repository.updateAudioState(audioEntity.uid, TranscriptionState.NONE)
                    }

                    else -> {}
                }
            }
        }
    }

    override fun getFlowById(id: Int): Flow<AudioEntity?> = repository.getFlowById(id)

    override suspend fun saveAudio(audioEntity: AudioEntity): Long = withContext(Dispatchers.IO) {
        return@withContext repository.save(audioEntity)
    }

    override suspend fun getById(id: Int): AudioEntity? = withContext(Dispatchers.IO) {
        return@withContext repository.getById(id)
    }

    override fun deleteAudio(audioEntity: AudioEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(audioEntity)
            if (!audioEntity.isDefault) {
                File(audioEntity.audioPath).parentFile!!.deleteRecursively()
            }
        }
    }

    override fun cancelTranscriptionWork(audioEntity: AudioEntity) {
        workManager.cancelAllWorkByTag(TranscriptionWorker.TAG)
    }
}
