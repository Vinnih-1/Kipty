package io.github.vinnih.kipty.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.TranscriptionState
import io.github.vinnih.kipty.data.database.repository.audio.AudioRepository
import io.github.vinnih.kipty.data.settings.AppPreferencesRepository
import io.github.vinnih.kipty.utils.convertTranscription
import io.github.vinnih.kipty.utils.copyTo
import java.io.File
import java.time.LocalDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class PopulateWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val repository: AudioRepository,
    private val appPreferencesRepository: AppPreferencesRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        appPreferencesRepository.runOnlyOnFirstSync {
            createDefault { audio, transcription, image, description ->
                val transcriptionData = transcription.convertTranscription()
                val audioEntity = AudioEntity(
                    name = audio.substringAfterLast("/"),
                    description = description,
                    audioPath = audio,
                    imagePath = image,
                    isDefault = true,
                    createdAt = LocalDateTime.now().toString(),
                    state = TranscriptionState.TRANSCRIBED,
                    transcription = transcriptionData
                )

                repository.save(audioEntity)
            }
        }

        return Result.success()
    }

    suspend fun createDefault(data: suspend (String, String, String, String) -> Unit) {
        withContext(Dispatchers.IO) {
            appContext.assets.open("icons/default-icon.png")
                .copyTo(File(appContext.filesDir, "default-icon.png"))

            appContext.assets.list("samples/")!!.map { folder ->
                appContext.assets.list("samples/$folder")!!
                    .map { sampleFolder ->
                        val sample = appContext.assets.list("samples/$folder/$sampleFolder")!!
                            .filter { it.endsWith(".opus") }
                            .map { File(it) }
                            .first()
                        val transcription = appContext.assets.open(
                            "samples/$folder/$sampleFolder/raw_transcription.txt"
                        )
                        val description = appContext.assets.open(
                            "samples/$folder/$sampleFolder/description.txt"
                        )

                        data.invoke(
                            "samples/$folder/$sampleFolder/${sample.name}",
                            transcription.bufferedReader().readText(),
                            "samples/$folder/$sampleFolder/image.jpg",
                            description.bufferedReader().readText()
                        )
                    }
            }
        }
    }
}
