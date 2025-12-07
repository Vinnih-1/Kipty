package io.github.vinnih.kipty.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "audios")
data class AudioEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    val name: String,
    val description: String? = null,
    val totalListened: Int = 0,
    val duration: Long,
    val createdAt: String,
    val transcription: List<AudioTranscription>? = null,
    val path: String
)

@Serializable
data class AudioTranscription(val start: Long, val end: Long, val text: String)
