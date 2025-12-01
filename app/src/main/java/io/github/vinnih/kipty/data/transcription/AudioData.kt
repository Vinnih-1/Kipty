package io.github.vinnih.kipty.data.transcription

import kotlinx.serialization.Serializable

data class AudioData(val details: AudioDetails, val transcription: List<AudioTranscription>?)

@Serializable
data class AudioDetails(
    val name: String,
    val description: String? = null,
    val totalListened: Int,
    val duration: Long,
    val createdAt: String,
    val audioFile: String,
    val imageFile: String? = null
)

data class AudioTranscription(val start: Long, val end: Long, val text: String)
