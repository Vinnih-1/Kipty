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
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.repository.audio.AudioRepository
import io.github.vinnih.kipty.data.service.notification.NotificationChannels
import io.github.vinnih.kipty.data.service.notification.NotificationService
import io.github.vinnih.kipty.utils.AudioResampler
import io.github.vinnih.kipty.utils.AudioResampler.getAudioDuration
import io.github.vinnih.kipty.utils.AudioResampler.resample
import io.github.vinnih.kipty.utils.createFolder
import io.github.vinnih.kipty.utils.processUriToFile
import java.io.File
import java.time.LocalDateTime

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
        val name = inputData.getString("name")!!
        val description = inputData.getString("description")!!
        val imagePath = inputData.getString("imagePath")!!
        val path = File(
            appContext.filesDir,
            "transcriptions" + File.separatorChar + name
        ).createFolder()
        val imageFile = File(path, imagePath.substringAfterLast("/"))

        File(imagePath).copyTo(imageFile, overwrite = true)
        val audioEntity = audioRepository.getById(
            createEntity(name, description, imageFile.absolutePath)
        )!!

        val notificationProgress = NotificationService.NotificationObject(
            channel = NotificationChannels.AUDIO_RUNNING,
            title = "Creating Transcription",
            content = "We are processing your new audio, please wait."
        )

        setForegroundAsync(createForegroundInfo(notificationProgress))

        val tempFile = uri.toUri().processUriToFile(appContext)!!
        val audio = convertAudioFile(tempFile).also { tempFile.delete() }
        val destination = File(path, audio.name)

        audio.copyTo(destination, overwrite = true)

        val duration = getAudioDuration(destination.absolutePath) ?: return Result.failure()

        updateEntity(
            audioEntity,
            destination.absolutePath,
            audioSize = audio.length(),
            duration = duration
        )
        audio.delete()

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

    suspend fun createEntity(name: String, description: String, imagePath: String): Int {
        val entity = AudioEntity(
            name = name,
            description = description.ifEmpty { null },
            audioPath = "",
            imagePath = imagePath,
            isDefault = false,
            createdAt = LocalDateTime.now().toString(),
            duration = 0,
            audioSize = 0
        )
        return audioRepository.save(entity).toInt()
    }

    suspend fun updateEntity(
        audioEntity: AudioEntity,
        audioPath: String,
        audioSize: Long,
        duration: Long
    ) {
        audioRepository.save(
            audioEntity.copy(
                audioPath = audioPath,
                audioSize = audioSize,
                duration = duration
            )
        )
    }

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
