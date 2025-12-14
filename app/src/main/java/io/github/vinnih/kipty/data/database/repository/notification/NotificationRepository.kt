package io.github.vinnih.kipty.data.database.repository.notification

import io.github.vinnih.kipty.data.database.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getAll(): Flow<List<NotificationEntity>>

    fun getAllUnread(): Flow<List<NotificationEntity>>

    fun getById(id: Int): Flow<NotificationEntity?>

    suspend fun save(notificationEntity: NotificationEntity): Long

    suspend fun delete(notificationEntity: NotificationEntity)
}
