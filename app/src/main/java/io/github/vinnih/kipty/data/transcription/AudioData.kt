package io.github.vinnih.kipty.data.transcription

import java.time.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class AudioData(val details: AudioDetails, val transcription: List<AudioTranscription>? = null)

@Serializable
data class AudioDetails(
    val name: String,
    val description: String? = null,
    val totalListened: Int = 0,
    val duration: Long,
    val createdAt: String = LocalDateTime.now().toString(),
    val imageFile: String? = null
)

@Serializable
data class AudioTranscription(val start: Long, val end: Long, val text: String)
