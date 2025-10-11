package io.github.vinnih.kipty.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transcriptions")
data class Transcription(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val transcriptionName: String,
    val transcriptionUri: String,
    val transcriptionDescription: String? = null,
    val createdAt: String
)