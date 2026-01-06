package io.github.vinnih.kipty.data.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import io.github.vinnih.kipty.R

const val NOTIFICATION_ID = 2122
const val CHANNEL_ID = "kipty_transcription_running"

fun createNotificationChannel(context: Context) {
    val name = "Initializing transcription"
    val description = "Your transcription is running, please wait."

    val channel = NotificationChannel(
        CHANNEL_ID,
        name,
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        this.description = description
    }
    val notificationManager = context.getSystemService(
        Context.NOTIFICATION_SERVICE
    ) as NotificationManager

    notificationManager.createNotificationChannel(channel)
}

fun createNotification(
    context: Context,
    ongoing: Boolean,
    text: String,
    progress: Int
): Notification {
    createNotificationChannel(context)

    return NotificationCompat.Builder(context, CHANNEL_ID)
        .setContentTitle("Transcription")
        .setContentText(text)
        .setSmallIcon(R.mipmap.ic_launcher_round)
        .setOngoing(ongoing)
        .setProgress(100, progress, false)
        .build()
}
