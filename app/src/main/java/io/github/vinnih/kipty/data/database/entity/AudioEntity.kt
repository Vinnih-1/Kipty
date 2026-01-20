package io.github.vinnih.kipty.data.database.entity

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Immutable
@Serializable
@Entity(tableName = "audios")
data class AudioEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    val name: String,
    val description: String? = null,
    val createdAt: String,
    val transcription: List<AudioTranscription>? = null,
    val audioPath: String,
    val imagePath: String,
    val isDefault: Boolean,
    val playTime: Long = 0,
    val state: TranscriptionState = TranscriptionState.NONE
)

@Serializable
data class AudioTranscription(val start: Long, val end: Long, val text: String)

@Serializable
enum class TranscriptionState {
    TRANSCRIBED,
    TRANSCRIBING,
    NONE
}
