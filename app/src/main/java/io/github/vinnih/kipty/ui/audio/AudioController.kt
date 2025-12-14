package io.github.vinnih.kipty.ui.audio

import androidx.work.WorkInfo
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.AudioTranscription
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AudioController {

    val audioUiState: StateFlow<AudioEntity?>

    fun transcribeAudio(audioEntity: AudioEntity, onSuccess: (List<AudioTranscription>) -> Unit)

    fun saveTranscription(audioEntity: AudioEntity)

    fun observeTranscriptionWork(): Flow<List<WorkInfo>>

    fun cancelTranscriptionWork(audioEntity: AudioEntity)

    suspend fun getById(id: Int): AudioEntity

    suspend fun getCurrent(id: Int)
}
