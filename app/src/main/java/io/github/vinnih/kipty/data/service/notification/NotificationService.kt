package io.github.vinnih.kipty.data.service.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.vinnih.kipty.R
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.NotificationCategory
import io.github.vinnih.kipty.data.database.entity.NotificationEntity
import io.github.vinnih.kipty.data.database.repository.notification.NotificationRepository
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class NotificationService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationRepository: NotificationRepository
) {

    data class NotificationObject(
        val id: Int = Random.nextInt(1000, 2000),
        val channel: NotificationChannels,
        val title: String,
        var content: String = "",
        var progress: Int = 0
    )

    init {
        val notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        NotificationChannels.entries.forEach { notificationChannel ->
            val channel = NotificationChannel(
                notificationChannel.channelId,
                notificationChannel.channelName,
                notificationChannel.importance
            )

            notificationManager.createNotificationChannel(channel)
        }
    }

    fun defaultNotification(notificationObject: NotificationObject): Notification =
        NotificationCompat.Builder(context, notificationObject.channel.channelId)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(notificationObject.title)
            .setContentText(notificationObject.content)
            .build()

    fun progressNotification(
        notificationObject: NotificationObject,
        indeterminate: Boolean = false
    ): Notification = NotificationCompat.Builder(context, notificationObject.channel.channelId)
        .setContentTitle(notificationObject.title)
        .setContentText(notificationObject.content)
        .setSmallIcon(R.mipmap.ic_launcher_round)
        .setOngoing(true)
        .setOnlyAlertOnce(true)
        .setProgress(100, notificationObject.progress, indeterminate)
        .build()

    suspend fun notify(notificationObject: NotificationObject, audioEntity: AudioEntity) {
        val notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        val notification = when (notificationObject.progress) {
            0 -> {
                notificationRepository.save(
                    NotificationEntity(
                        title = notificationObject.title,
                        content = notificationObject.content,
                        audioId = audioEntity.uid,
                        audioName = audioEntity.name,
                        channel = NotificationCategory.TRANSCRIPTION_DONE,
                        createdAt = LocalDateTime.now().toString()
                    )
                )
                defaultNotification(notificationObject)
            }

            else -> progressNotification(notificationObject, false)
        }

        notificationManager.notify(notificationObject.id, notification)
    }
}
