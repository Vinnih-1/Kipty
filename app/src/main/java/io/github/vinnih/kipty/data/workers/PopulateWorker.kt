package io.github.vinnih.kipty.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.TranscriptionState
import io.github.vinnih.kipty.data.service.audio.AudioService
import io.github.vinnih.kipty.data.settings.AppPreferencesRepository
import io.github.vinnih.kipty.domain.repository.AudioRepository
import io.github.vinnih.kipty.utils.convertTranscription
import io.github.vinnih.kipty.utils.copyTo
import io.github.vinnih.kipty.utils.createFile
import java.io.File
import java.time.LocalDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class PopulateWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val repository: AudioRepository,
    private val appPreferencesRepository: AppPreferencesRepository,
    private val audioService: AudioService
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val TAG = "populate_worker"
    }

    override suspend fun doWork(): Result {
        appPreferencesRepository.runOnlyOnFirstSync {
            createDefault { audio, transcription, image, description, tempFile ->
                val duration =
                    audioService.getAudioDuration(tempFile.absolutePath) ?: return@createDefault
                val transcriptionData = transcription.convertTranscription()
                val audioEntity = AudioEntity(
                    name = audio.substringAfterLast("/").substringBeforeLast("."),
                    description = description,
                    audioPath = audio,
                    imagePath = image,
                    isDefault = true,
                    createdAt = LocalDateTime.now().toString(),
                    state = TranscriptionState.TRANSCRIBED,
                    transcription = transcriptionData,
                    duration = duration,
                    audioSize = tempFile.length()
                )

                repository.save(audioEntity)
                tempFile.delete()
            }
        }

        appPreferencesRepository.markDatabaseAsPopulated()
        return Result.success()
    }

    suspend fun createDefault(data: suspend (String, String, String, String, File) -> Unit) {
        withContext(Dispatchers.IO) {
            appContext.assets.open("icons/default-icon.png")
                .copyTo(File(appContext.filesDir, "default-icon.png"))

            appContext.assets.list("samples/")?.forEach { folder ->
                appContext.assets.list("samples/$folder")?.forEach { sampleFolder ->
                    val sampleName = appContext.assets.list("samples/$folder/$sampleFolder")
                        ?.find { it.endsWith(".opus") }

                    if (sampleName != null) {
                        val tempFile = File(appContext.filesDir, "temp_$sampleName").createFile()

                        appContext.assets.open("samples/$folder/$sampleFolder/$sampleName")
                            .copyTo(tempFile)

                        val transcription = appContext.assets.open(
                            "samples/$folder/$sampleFolder/raw_transcription.txt"
                        )
                        val description = appContext.assets.open(
                            "samples/$folder/$sampleFolder/description.txt"
                        )
                        data.invoke(
                            "samples/$folder/$sampleFolder/$sampleName",
                            transcription.bufferedReader().readText(),
                            "samples/$folder/$sampleFolder/image.jpg",
                            description.bufferedReader().readText(),
                            tempFile
                        )
                    }
                }
            }
        }
    }
}
