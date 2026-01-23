package io.github.vinnih.kipty.data.service.notification

import android.app.NotificationManager

enum class NotificationChannels(
    val channelId: String,
    val channelName: String,
    val importance: Int = NotificationManager.IMPORTANCE_DEFAULT
) {
    AUDIO_RUNNING(
        channelId = "kipty_creating_running",
        channelName = "Create new audio process"
    ),
    AUDIO_CREATED(
        channelId = "kipty_created_running",
        channelName = "New Audio is created"
    ),
    TRANSCRIPTION_RUNNING(
        channelId = "kipty_transcription_running",
        channelName = "Transcription process notification"
    ),
    TRANSCRIPTION_CREATED(
        channelId = "kipty_transcription_created",
        channelName = "Transcription is created"
    )
}
