package io.github.vinnih.kipty.data.database.entity

import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.vinnih.kipty.R
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    val title: String,
    val content: String,
    val read: Boolean = false,
    val audioId: Int,
    val audioName: String,
    val channel: NotificationCategory,
    val createdAt: String
)

@Serializable
enum class NotificationCategory(@DrawableRes val iconId: Int) {
    TRANSCRIPTION_INIT(R.drawable.file_text),
    TRANSCRIPTION_DONE(R.drawable.sparkles)
}
