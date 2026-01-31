package io.github.vinnih.kipty.data.database.entity

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Immutable
@Serializable
@Entity(tableName = "speeches")
data class SpeechEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    val audioPath: String,
    val speechPath: String,
    val phrase: AudioTranscription,
    val result: String,
    val createdAt: String
)
