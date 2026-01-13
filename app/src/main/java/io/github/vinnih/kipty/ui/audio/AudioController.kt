package io.github.vinnih.kipty.ui.audio

import androidx.work.WorkInfo
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.AudioTranscription
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AudioController {

    val allAudios: StateFlow<List<AudioEntity>>

    suspend fun createAudio(
        audio: String,
        image: String,
        name: String,
        description: String?
    ): AudioEntity

    suspend fun createAudio(
        audio: String,
        image: String,
        name: String,
        description: String?,
        isDefault: Boolean
    ): AudioEntity

    fun transcribeAudio(audioEntity: AudioEntity, onError: (String) -> Unit)

    suspend fun saveAudio(audioEntity: AudioEntity): Long

    fun deleteAudio(audioEntity: AudioEntity)

    fun observeTranscriptionWork(): Flow<List<WorkInfo>>

    fun cancelTranscriptionWork(audioEntity: AudioEntity)

    fun getById(id: Int): Flow<AudioEntity?>
}
