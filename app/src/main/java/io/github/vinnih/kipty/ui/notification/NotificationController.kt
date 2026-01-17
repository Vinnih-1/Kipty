package io.github.vinnih.kipty.ui.notification

import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.NotificationChannel
import io.github.vinnih.kipty.data.database.entity.NotificationEntity
import kotlinx.coroutines.flow.StateFlow

interface NotificationController {

    val uiState: StateFlow<NotificationUiState>
    val today: StateFlow<List<NotificationEntity>>
    val yesterday: StateFlow<List<NotificationEntity>>
    val earlier: StateFlow<List<NotificationEntity>>
    val unread: StateFlow<List<NotificationEntity>>

    fun notify(
        audioEntity: AudioEntity,
        title: String,
        content: String,
        channel: NotificationChannel
    )

    fun read(notificationEntity: NotificationEntity)

    fun delete(notificationEntity: NotificationEntity)
}
