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
import io.github.vinnih.kipty.data.database.entity.AudioTranscription
import io.github.vinnih.kipty.data.database.entity.TranscriptionState
import io.github.vinnih.kipty.data.database.repository.AudioRepository
import io.github.vinnih.kipty.data.workers.TranscriptionWorker
import io.github.vinnih.kipty.json
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TRANSCRIPTION_QUEUE = "transcription_queue"

@HiltViewModel
class AudioViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: AudioRepository
) : ViewModel(),
    AudioController {
    private val workManager = WorkManager.getInstance(context)

    private val _audioUiState = MutableStateFlow<AudioEntity?>(null)
    override val audioUiState: StateFlow<AudioEntity?> = _audioUiState.asStateFlow()

    override fun transcribeAudio(
        audioEntity: AudioEntity,
        onSuccess: (List<AudioTranscription>) -> Unit
    ) {
        workManager.pruneWork()

        val request = OneTimeWorkRequestBuilder<TranscriptionWorker>()
            .setInputData(Data.Builder().putInt("AUDIO_ID", audioEntity.uid).build())
            .addTag("${audioEntity.uid}")
            .build()

        workManager.enqueueUniqueWork(
            uniqueWorkName = TRANSCRIPTION_QUEUE,
            existingWorkPolicy = ExistingWorkPolicy.APPEND,
            request = request
        )
        updateAudioState(audioEntity, TranscriptionState.TRANSCRIBING)

        viewModelScope.launch {
            workManager.getWorkInfoByIdFlow(request.id).collect { workInfo ->
                when (workInfo?.state) {
                    WorkInfo.State.SUCCEEDED -> {
                        val transcription = json.decodeFromString<List<AudioTranscription>>(
                            workInfo.outputData.getString("transcription")!!
                        )
                        onSuccess(transcription)
                        updateAudioState(
                            audioEntity.copy(transcription = transcription),
                            TranscriptionState.TRANSCRIBED
                        )
                    }

                    WorkInfo.State.CANCELLED -> {
                        updateAudioState(audioEntity, TranscriptionState.NONE)
                    }

                    WorkInfo.State.FAILED -> {
                        updateAudioState(audioEntity, TranscriptionState.NONE)
                    }

                    else -> {}
                }
            }
        }
    }

    private fun updateAudioState(audioEntity: AudioEntity, state: TranscriptionState) {
        val entityCopy = audioEntity.copy(state = state)

        _audioUiState.value = entityCopy
        saveTranscription(entityCopy)
    }

    override fun saveTranscription(audioEntity: AudioEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.save(audioEntity)
        }
    }

    override fun observeTranscriptionWork(): Flow<List<WorkInfo>> =
        workManager.getWorkInfosForUniqueWorkFlow(TRANSCRIPTION_QUEUE)

    override fun cancelTranscriptionWork(audioEntity: AudioEntity) {
        workManager.cancelAllWorkByTag("${audioEntity.uid}")
        _audioUiState.value = audioEntity.copy(state = TranscriptionState.NONE)
    }

    override suspend fun getById(id: Int): AudioEntity = withContext(Dispatchers.IO) {
        return@withContext repository.getById(id)!!
    }

    override suspend fun getCurrent(id: Int) = withContext(Dispatchers.IO) {
        _audioUiState.value = getById(id)
    }
}
