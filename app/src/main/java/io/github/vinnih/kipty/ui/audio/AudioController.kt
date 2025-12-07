package io.github.vinnih.kipty.ui.audio

import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.AudioTranscription
import kotlinx.coroutines.flow.StateFlow

interface AudioController {

    val isTranscribing: StateFlow<Boolean>

    fun convertTranscription(transcribedData: String): List<AudioTranscription>

    fun transcribeAudio(audioEntity: AudioEntity, onSuccess: (AudioEntity) -> Unit)

    fun saveTranscription(audioEntity: AudioEntity)

    suspend fun getById(id: Int): AudioEntity
}
