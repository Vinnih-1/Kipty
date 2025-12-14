package io.github.vinnih.kipty.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    val title: String,
    val content: String,
    val read: Boolean,
    val createdAt: String
)
