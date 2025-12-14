package io.github.vinnih.kipty.ui.notification

import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import io.github.vinnih.kipty.data.database.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeNotificationViewModel : NotificationController {

    override val allNotifications: StateFlow<List<NotificationEntity>>
        get() = MutableStateFlow(
            listOf(
                NotificationEntity(
                    uid = 0,
                    title = LoremIpsum(5).values.joinToString(),
                    content = LoremIpsum(100).values.joinToString(),
                    read = false,
                    createdAt = "2025-12-14T12:46:48.849"
                ),
                NotificationEntity(
                    uid = 1,
                    title = LoremIpsum(5).values.joinToString(),
                    content = LoremIpsum(100).values.joinToString(),
                    read = false,
                    createdAt = "2025-12-14T12:46:48.849"
                ),
                NotificationEntity(
                    uid = 2,
                    title = LoremIpsum(5).values.joinToString(),
                    content = LoremIpsum(100).values.joinToString(),
                    read = false,
                    createdAt = "2025-12-14T12:46:48.849"
                )
            )
        )
    override val unreadNotifications: StateFlow<List<NotificationEntity>>
        get() = TODO("Not yet implemented")

    override suspend fun createNotification(title: String, content: String): NotificationEntity {
        TODO("Not yet implemented")
    }

    override suspend fun submitNotification(notificationEntity: NotificationEntity): Long {
        TODO("Not yet implemented")
    }

    override suspend fun getAllNotifications(): Flow<List<NotificationEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllUnreadNotifications(): Flow<List<NotificationEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun getNotificationById(id: Int): Flow<NotificationEntity?> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteNotification(notificationEntity: NotificationEntity) {
        TODO("Not yet implemented")
    }
}
