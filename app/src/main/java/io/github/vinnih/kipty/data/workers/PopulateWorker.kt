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
import io.github.vinnih.kipty.utils.AudioResampler.getAudioDuration
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
    private val appPreferencesRepository: AppPreferencesRepository
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val TAG = "populate_worker"
    }

    override suspend fun doWork(): Result {
        appPreferencesRepository.runOnlyOnFirstSync {
            createDefault { audio, transcription, image, description, tempFile ->
                val duration = getAudioDuration(tempFile.absolutePath) ?: return@createDefault
                val transcriptionData = transcription.convertTranscription()
                val audioEntity = AudioEntity(
                    name = audio.substringAfterLast("/"),
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

        return Result.success()
    }

    suspend fun createDefault(data: suspend (String, String, String, String, File) -> Unit) {
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
                        val tempFile = File(appContext.filesDir, "temp_${sample.name}").createFile()

                        appContext.assets.open("samples/$folder/$sampleFolder/${sample.name}")
                            .copyTo(tempFile)

                        val transcription = appContext.assets.open(
                            "samples/$folder/$sampleFolder/raw_transcription.txt"
                        )
                        val description = appContext.assets.open(
                            "samples/$folder/$sampleFolder/description.txt"
                        )
                        println(sample.length())

                        data.invoke(
                            "samples/$folder/$sampleFolder/${sample.name}",
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
