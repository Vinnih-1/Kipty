package io.github.vinnih.kipty.data.workers

import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.vinnih.kipty.data.database.entity.TranscriptionState
import io.github.vinnih.kipty.data.database.repository.audio.AudioRepository
import io.github.vinnih.kipty.data.service.notification.NotificationChannels
import io.github.vinnih.kipty.data.service.notification.NotificationService
import io.github.vinnih.kipty.data.settings.AppPreferencesRepository
import io.github.vinnih.kipty.data.transcriptor.Transcriptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

@HiltWorker
class TranscriptionWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val appPreferencesRepository: AppPreferencesRepository,
    private val audioRepository: AudioRepository,
    private val transcriptor: Transcriptor,
    private val notificationService: NotificationService
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val TAG = "TranscriptionWorker"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val audioId = inputData.getInt("AUDIO_ID", -1)

        if (audioId == -1) return@withContext Result.failure()

        this@TranscriptionWorker.setProgress(workDataOf("AUDIO_ID" to audioId))
        audioRepository.updateAudioState(audioId, TranscriptionState.TRANSCRIBING)

        val channel = NotificationChannels.TRANSCRIPTION_RUNNING
        val notificationProgress = NotificationService.NotificationObject(
            channel = channel,
            title = "Creating Transcription",
            content = "Your transcription is 0%, please wait.",
            progress = 0
        )

        setForegroundAsync(createForegroundInfo(notificationProgress))

        val audioEntity = transcriptor.transcribe(
            audioEntity = audioRepository.getFlowById(audioId).first()!!,
            numThreads = appPreferencesRepository.appSettingsFlow.first().minimumThreads,
            onProgress = {
                notificationProgress.progress = it
                notificationProgress.content = "Your transcription is $it%, please wait."
                setForegroundAsync(createForegroundInfo(notificationProgress))
            }
        )
        audioRepository.save(audioEntity.copy(state = TranscriptionState.TRANSCRIBED))
        notificationService.notify(
            notificationObject = NotificationService.NotificationObject(
                channel = NotificationChannels.TRANSCRIPTION_CREATED,
                title = "New Episode Available",
                content = "A new episode has been added to your feed"
            ),
            audioEntity = audioEntity
        )

        return@withContext Result.success()
    }

    private fun createForegroundInfo(
        notificationObject: NotificationService.NotificationObject
    ): ForegroundInfo {
        val notification = notificationService.progressNotification(notificationObject)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                notificationObject.id,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(notificationObject.id, notification)
        }
    }
}
