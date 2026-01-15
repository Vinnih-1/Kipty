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
import io.github.vinnih.kipty.data.service.NOTIFICATION_ID
import io.github.vinnih.kipty.data.service.createNotification
import io.github.vinnih.kipty.data.settings.AppPreferencesRepository
import io.github.vinnih.kipty.data.transcriptor.Transcriptor
import io.github.vinnih.kipty.json
import io.github.vinnih.kipty.utils.createFile
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

@HiltWorker
class TranscriptionWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val appPreferencesRepository: AppPreferencesRepository,
    private val audioRepository: AudioRepository,
    private val transcriptor: Transcriptor
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val audioId = inputData.getInt("AUDIO_ID", -1)

        if (audioId == -1) return@withContext Result.failure()

        setForegroundAsync(createForegroundInfo(0))

        val audioEntity = transcriptor.transcribe(
            audioEntity = audioRepository.getById(audioId).first()!!,
            numThreads = appPreferencesRepository.appSettingsFlow.first().minimumThreads,
            onProgress = {
                setForegroundAsync(createForegroundInfo(it))
            }
        )
        val transcription = File(applicationContext.cacheDir, "${this@TranscriptionWorker.id}.json")
            .createFile()
            .also {
                it.writeText(json.encodeToString(audioEntity.transcription))
            }
        val data = workDataOf(
            "transcription" to transcription.absolutePath
        )
        audioRepository.save(audioEntity.copy(state = TranscriptionState.TRANSCRIBED))

        return@withContext Result.success(data)
    }

    private fun createForegroundInfo(progress: Int): ForegroundInfo {
        val notification = createNotification(
            applicationContext,
            true,
            "Your transcription is $progress%, please wait.",
            progress
        )

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(NOTIFICATION_ID, notification)
        }
    }
}
