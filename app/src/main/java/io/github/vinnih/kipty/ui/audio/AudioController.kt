package io.github.vinnih.kipty.ui.audio

import androidx.work.WorkInfo
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.AudioTranscription
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AudioController {

    val allAudios: StateFlow<List<AudioEntity>>

    fun transcribeAudio(audioEntity: AudioEntity, onSuccess: (List<AudioTranscription>) -> Unit)

    fun saveTranscription(audioEntity: AudioEntity)

    fun observeTranscriptionWork(): Flow<List<WorkInfo>>

    fun cancelTranscriptionWork(audioEntity: AudioEntity)

    fun getById(id: Int): Flow<AudioEntity?>
}
