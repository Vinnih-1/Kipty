package io.github.vinnih.kipty.data.database.repository.notification.impl

import io.github.vinnih.kipty.data.database.dao.NotificationDao
import io.github.vinnih.kipty.data.database.entity.NotificationEntity
import io.github.vinnih.kipty.data.database.repository.notification.NotificationRepository
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class NotificationRepositoryImpl @Inject constructor(private val dao: NotificationDao) :
    NotificationRepository {

    override fun getToday(): Flow<List<NotificationEntity>> = dao.getToday()

    override fun getYesterday(): Flow<List<NotificationEntity>> = dao.getYesterday()

    override fun getEarlier(): Flow<List<NotificationEntity>> = dao.getEarlier()

    override fun getAllUnread(): Flow<List<NotificationEntity>> = dao.getAllUnread()

    override suspend fun save(notificationEntity: NotificationEntity): Long =
        withContext(Dispatchers.IO) {
            return@withContext dao.save(notificationEntity)
        }

    override suspend fun delete(notificationEntity: NotificationEntity) {
        withContext(Dispatchers.IO) {
            dao.delete(notificationEntity)
        }
    }

    override suspend fun read(notificationEntity: NotificationEntity) {
        withContext(Dispatchers.IO) {
            dao.read(notificationEntity.uid.toLong())
        }
    }
}
