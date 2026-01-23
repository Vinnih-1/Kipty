package io.github.vinnih.kipty.data.workers

import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.vinnih.kipty.data.database.repository.audio.AudioRepository
import io.github.vinnih.kipty.data.service.notification.NotificationChannels
import io.github.vinnih.kipty.data.service.notification.NotificationService
import io.github.vinnih.kipty.utils.AudioResampler
import io.github.vinnih.kipty.utils.AudioResampler.getAudioDuration
import io.github.vinnih.kipty.utils.AudioResampler.resample
import io.github.vinnih.kipty.utils.processUriToFile
import java.io.File

@HiltWorker
class AudioWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val audioRepository: AudioRepository,
    private val notificationService: NotificationService
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val TAG = "audio_worker"
    }

    override suspend fun doWork(): Result {
        val uri = inputData.getString("audioUri") ?: return Result.failure()
        val uid = inputData.getInt("uid", -1)
        val folderPath = inputData.getString("folderPath") ?: return Result.failure()
        val audioEntity = audioRepository.getById(uid) ?: return Result.failure()

        val notificationProgress = NotificationService.NotificationObject(
            channel = NotificationChannels.AUDIO_RUNNING,
            title = "Creating Transcription",
            content = "We are processing your new audio, please wait."
        )

        setForegroundAsync(createForegroundInfo(notificationProgress))

        val tempFile = uri.toUri().processUriToFile(appContext)!!
        val audio = convertAudioFile(tempFile).also { tempFile.delete() }
        val destination = File(folderPath, audio.name)

        audio.copyTo(destination, overwrite = true)
        audio.delete()

        val duration = getAudioDuration(destination.absolutePath) ?: return Result.failure()

        audioRepository.save(
            audioEntity.copy(
                uid = uid,
                audioPath = destination.absolutePath,
                duration = duration,
                audioSize = destination.length()
            )
        )
        notificationService.notify(
            notificationObject = NotificationService.NotificationObject(
                channel = NotificationChannels.AUDIO_RUNNING,
                title = "Your new audio is now available.",
                content = "Let's check it out"
            ),
            audioEntity = audioEntity
        )

        return Result.success()
    }

    fun convertAudioFile(file: File): File = file.resample(
        format = AudioResampler.OutputFormat.OPUS,
        context = appContext
    )

    private fun createForegroundInfo(
        notificationObject: NotificationService.NotificationObject
    ): ForegroundInfo {
        val notification = notificationService.progressNotification(notificationObject, true)

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
