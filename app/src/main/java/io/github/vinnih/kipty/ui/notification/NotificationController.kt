package io.github.vinnih.kipty.ui.notification

import io.github.vinnih.kipty.data.database.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface NotificationController {

    val allNotifications: StateFlow<List<NotificationEntity>>

    val unreadNotifications: StateFlow<List<NotificationEntity>>

    suspend fun createNotification(title: String, content: String): NotificationEntity

    suspend fun submitNotification(notificationEntity: NotificationEntity): Long

    suspend fun getAllNotifications(): Flow<List<NotificationEntity>>

    suspend fun getAllUnreadNotifications(): Flow<List<NotificationEntity>>

    suspend fun getNotificationById(id: Int): Flow<NotificationEntity?>

    suspend fun deleteNotification(notificationEntity: NotificationEntity)

    suspend fun readAllNotifications()
}
