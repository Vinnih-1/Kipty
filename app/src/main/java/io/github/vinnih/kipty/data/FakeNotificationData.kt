package io.github.vinnih.kipty.data

import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import io.github.vinnih.kipty.data.database.entity.NotificationChannel
import io.github.vinnih.kipty.data.database.entity.NotificationEntity

object FakeNotificationData {

    val notifications = listOf(
        NotificationEntity(
            uid = 0,
            title = LoremIpsum(5).values.joinToString(),
            content = LoremIpsum(100).values.joinToString(),
            read = false,
            createdAt = "2025-12-14T12:46:48.849",
            audioId = 1,
            audioName = "audio.mp3",
            channel = NotificationChannel.TRANSCRIPTION_INIT
        ),
        NotificationEntity(
            uid = 1,
            title = LoremIpsum(5).values.joinToString(),
            content = LoremIpsum(100).values.joinToString(),
            read = false,
            createdAt = "2025-12-14T12:46:48.849",
            audioId = 1,
            audioName = "audio.mp3",
            channel = NotificationChannel.TRANSCRIPTION_DONE
        ),
        NotificationEntity(
            uid = 2,
            title = LoremIpsum(5).values.joinToString(),
            content = LoremIpsum(100).values.joinToString(),
            read = false,
            createdAt = "2025-12-14T12:46:48.849",
            audioId = 1,
            audioName = "audio.mp3",
            channel = NotificationChannel.TRANSCRIPTION_DONE
        )
    )
}
