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
import io.github.vinnih.kipty.data.database.repository.audio.AudioRepository
import io.github.vinnih.kipty.data.workers.TranscriptionWorker
import io.github.vinnih.kipty.json
import io.github.vinnih.kipty.utils.createFile
import io.github.vinnih.kipty.utils.createFolder
import io.github.vinnih.kipty.utils.moveTo
import java.io.File
import java.time.LocalDateTime
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
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
    override val allAudios: StateFlow<List<AudioEntity>> = repository.getAll().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    override suspend fun createAudio(
        audio: String,
        image: String,
        name: String,
        description: String?,
        isDefault: Boolean
    ): AudioEntity = withContext(Dispatchers.IO) {
        val path = File(
            context.filesDir,
            "transcriptions" + File.separatorChar + name
        ).createFolder()
        val audioFile = File(path, audio.substringAfterLast("/"))
        val imageFile = File(path, image.substringAfterLast("/"))

        File(audio).moveTo(audioFile)
        File(image).moveTo(imageFile)

        val entity = AudioEntity(
            name = name,
            description = description?.ifEmpty { null },
            audioPath = audioFile.absolutePath,
            imagePath = imageFile.absolutePath,
            isDefault = isDefault,
            createdAt = LocalDateTime.now().toString()
        )

        return@withContext entity.copy(uid = saveAudio(entity).toInt())
    }

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
        viewModelScope.launch { saveAudio(entityCopy) }
    }

    override suspend fun saveAudio(audioEntity: AudioEntity): Long = withContext(Dispatchers.IO) {
        return@withContext repository.save(audioEntity)
    }

    override fun deleteAudio(audioEntity: AudioEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(audioEntity)
            if (!audioEntity.isDefault) {
                File(audioEntity.audioPath).deleteRecursively()
            }
        }
    }

    override fun observeTranscriptionWork(): Flow<List<WorkInfo>> =
        workManager.getWorkInfosForUniqueWorkFlow(TRANSCRIPTION_QUEUE)

    override fun cancelTranscriptionWork(audioEntity: AudioEntity) {
        workManager.cancelAllWorkByTag("${audioEntity.uid}")
    }

    override fun getById(id: Int): Flow<AudioEntity?> = repository.getById(id)
}
