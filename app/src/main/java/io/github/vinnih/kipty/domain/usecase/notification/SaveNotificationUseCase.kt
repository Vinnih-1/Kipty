package io.github.vinnih.kipty.domain.usecase.notification

import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.NotificationCategory
import io.github.vinnih.kipty.data.database.entity.NotificationEntity
import io.github.vinnih.kipty.domain.repository.NotificationRepository
import jakarta.inject.Inject
import java.time.LocalDateTime

class SaveNotificationUseCase @Inject constructor(private val repository: NotificationRepository) {
    suspend operator fun invoke(
        audioEntity: AudioEntity,
        title: String,
        content: String,
        channel: NotificationCategory
    ) {
        val entity = NotificationEntity(
            title = title,
            content = content,
            audioId = audioEntity.uid,
            audioName = audioEntity.name,
            channel = channel,
            createdAt = LocalDateTime.now().toString()
        )
        repository.save(entity)
    }
}
