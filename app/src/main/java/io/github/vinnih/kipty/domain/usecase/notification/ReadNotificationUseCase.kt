package io.github.vinnih.kipty.domain.usecase.notification

import io.github.vinnih.kipty.data.database.entity.NotificationEntity
import io.github.vinnih.kipty.domain.repository.NotificationRepository
import jakarta.inject.Inject

class ReadNotificationUseCase @Inject constructor(private val repository: NotificationRepository) {
    suspend operator fun invoke(notification: NotificationEntity) = repository.read(notification)
}
