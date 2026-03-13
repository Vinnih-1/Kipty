package io.github.vinnih.kipty.ui.notification

import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.NotificationCategory
import io.github.vinnih.kipty.data.database.entity.NotificationEntity
import io.github.vinnih.kipty.preview.FakeNotificationData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeNotificationViewModel(
    notificationUiState: NotificationUiState = NotificationUiState(
        FakeNotificationData.notifications
    )
) : NotificationController {

    override val uiState: StateFlow<NotificationUiState> = MutableStateFlow(notificationUiState)

    override fun notify(
        audioEntity: AudioEntity,
        title: String,
        content: String,
        channel: NotificationCategory
    ) {}

    override fun read(notificationEntity: NotificationEntity) {}

    override fun delete(notificationEntity: NotificationEntity) {}
}
