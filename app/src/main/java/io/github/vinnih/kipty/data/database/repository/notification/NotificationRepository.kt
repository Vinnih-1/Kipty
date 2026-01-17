package io.github.vinnih.kipty.data.database.repository.notification

import io.github.vinnih.kipty.data.database.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {

    fun getToday(): Flow<List<NotificationEntity>>

    fun getYesterday(): Flow<List<NotificationEntity>>

    fun getEarlier(): Flow<List<NotificationEntity>>

    fun getAllUnread(): Flow<List<NotificationEntity>>

    suspend fun save(notificationEntity: NotificationEntity): Long

    suspend fun delete(notificationEntity: NotificationEntity)

    suspend fun read(notificationEntity: NotificationEntity)
}
