package io.github.vinnih.kipty.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.vinnih.kipty.data.database.repository.AudioRepository
import io.github.vinnih.kipty.data.service.NOTIFICATION_ID
import io.github.vinnih.kipty.data.service.createNotification
import io.github.vinnih.kipty.data.transcriptor.Transcriptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class TranscriptionWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val audioRepository: AudioRepository,
    private val transcriptor: Transcriptor
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val audioId = inputData.getInt("AUDIO_ID", -1)

        if (audioId == -1) return@withContext Result.failure()

        setForegroundAsync(
            ForegroundInfo(
                NOTIFICATION_ID,
                createNotification(
                    applicationContext,
                    true,
                    "Your transcription is running, please wait."
                )
            )
        )
        val audioEntity = transcriptor.transcribe(audioRepository.getById(audioId)!!)

        setForegroundAsync(
            ForegroundInfo(
                NOTIFICATION_ID,
                createNotification(applicationContext, false, "Your transcription is finished.")
            )
        )
        audioRepository.save(audioEntity)

        return@withContext Result.success()
    }
}
