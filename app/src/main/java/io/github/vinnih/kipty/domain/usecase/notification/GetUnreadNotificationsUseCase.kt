package io.github.vinnih.kipty.domain.usecase.notification

import io.github.vinnih.kipty.data.database.entity.NotificationEntity
import io.github.vinnih.kipty.domain.repository.NotificationRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetUnreadNotificationsUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    operator fun invoke(): Flow<List<NotificationEntity>> = repository.getAllUnread()
}
