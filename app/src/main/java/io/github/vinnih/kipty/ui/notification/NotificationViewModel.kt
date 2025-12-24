package io.github.vinnih.kipty.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.vinnih.kipty.data.database.entity.NotificationEntity
import io.github.vinnih.kipty.data.database.repository.notification.NotificationRepository
import java.time.LocalDateTime
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

@HiltViewModel
class NotificationViewModel @Inject constructor(private val repository: NotificationRepository) :
    ViewModel(),
    NotificationController {

    override val allNotifications: StateFlow<List<NotificationEntity>> = repository.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    override val unreadNotifications: StateFlow<List<NotificationEntity>> = repository
        .getAllUnread().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    override suspend fun createNotification(title: String, content: String): NotificationEntity =
        withContext(Dispatchers.IO) {
            return@withContext NotificationEntity(
                title = title,
                content = content,
                read = false,
                createdAt = LocalDateTime.now().toString()
            )
        }

    override suspend fun submitNotification(notificationEntity: NotificationEntity): Long =
        withContext(Dispatchers.IO) {
            return@withContext repository.save(notificationEntity)
        }

    override suspend fun getAllNotifications(): Flow<List<NotificationEntity>> = repository.getAll()

    override suspend fun getAllUnreadNotifications(): Flow<List<NotificationEntity>> =
        repository.getAllUnread()

    override suspend fun getNotificationById(id: Int): Flow<NotificationEntity?> =
        repository.getById(id)

    override suspend fun deleteNotification(notificationEntity: NotificationEntity) {
        withContext(Dispatchers.IO) {
            repository.delete(notificationEntity)
        }
    }

    override suspend fun readAllNotifications() {
        withContext(Dispatchers.IO) {
            repository.readAll()
        }
    }
}
