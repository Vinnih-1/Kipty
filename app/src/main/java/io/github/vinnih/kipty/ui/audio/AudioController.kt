package io.github.vinnih.kipty.ui.audio

import androidx.work.WorkInfo
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AudioController {

    val allAudios: StateFlow<List<AudioEntity>>

    fun transcribeAudio(audioEntity: AudioEntity, onSuccess: () -> Unit, onError: (String) -> Unit)

    suspend fun saveAudio(audioEntity: AudioEntity): Long

    suspend fun getAll(): List<AudioEntity>

    suspend fun getById(id: Int): AudioEntity?

    fun deleteAudio(audioEntity: AudioEntity)

    fun observeTranscriptionWork(): Flow<List<WorkInfo>>

    fun cancelTranscriptionWork(audioEntity: AudioEntity)

    fun getFlowById(id: Int): Flow<AudioEntity?>
}
