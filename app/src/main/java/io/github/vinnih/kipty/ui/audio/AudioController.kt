package io.github.vinnih.kipty.ui.audio

import io.github.vinnih.kipty.data.database.entity.AudioEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AudioController {

    val uiState: StateFlow<AudioUiState>

    fun transcribeAudio(audioEntity: AudioEntity)

    fun getFlowById(id: Int): Flow<AudioEntity?>

    suspend fun saveAudio(audioEntity: AudioEntity): Long

    suspend fun getById(id: Int): AudioEntity?

    fun deleteAudio(audioEntity: AudioEntity)

    fun cancelTranscriptionWork(audioEntity: AudioEntity)
}
