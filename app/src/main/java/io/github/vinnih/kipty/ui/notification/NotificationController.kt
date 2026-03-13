package io.github.vinnih.kipty.ui.notification

import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.NotificationCategory
import io.github.vinnih.kipty.data.database.entity.NotificationEntity
import kotlinx.coroutines.flow.StateFlow

interface NotificationController {

    val uiState: StateFlow<NotificationUiState>

    fun notify(
        audioEntity: AudioEntity,
        title: String,
        content: String,
        channel: NotificationCategory
    )

    fun read(notificationEntity: NotificationEntity)

    fun delete(notificationEntity: NotificationEntity)
}
