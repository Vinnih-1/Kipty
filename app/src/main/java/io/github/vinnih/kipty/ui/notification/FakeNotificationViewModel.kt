package io.github.vinnih.kipty.ui.notification

import io.github.vinnih.kipty.data.FakeNotificationData
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.NotificationChannel
import io.github.vinnih.kipty.data.database.entity.NotificationEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeNotificationViewModel : NotificationController {
    override val uiState: StateFlow<NotificationUiState>
        get() = MutableStateFlow(NotificationUiState(FakeNotificationData.notifications))
    override val today: StateFlow<List<NotificationEntity>>
        get() = TODO("Not yet implemented")
    override val yesterday: StateFlow<List<NotificationEntity>>
        get() = TODO("Not yet implemented")
    override val earlier: StateFlow<List<NotificationEntity>>
        get() = TODO("Not yet implemented")
    override val unread: StateFlow<List<NotificationEntity>>
        get() = TODO("Not yet implemented")

    override fun notify(
        audioEntity: AudioEntity,
        title: String,
        content: String,
        channel: NotificationChannel
    ) {
        TODO("Not yet implemented")
    }

    override fun read(notificationEntity: NotificationEntity) {
        TODO("Not yet implemented")
    }

    override fun delete(notificationEntity: NotificationEntity) {
        TODO("Not yet implemented")
    }
}
