package io.github.vinnih.kipty.domain.usecase.audio

import android.content.Context
import android.net.Uri
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.vinnih.kipty.data.workers.AudioWorker
import io.github.vinnih.kipty.utils.getFileName
import jakarta.inject.Inject
import java.io.File

class CreateAudioUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke(audioUri: Uri, title: String, description: String, imageFile: File?) {
        val validatedTitle = title.ifEmpty {
            audioUri.getFileName(context).substringBeforeLast(".")
        }
        val uniqueName = getUniqueAudioName(validatedTitle)
        val image = imageFile?.absolutePath ?: File(context.filesDir, "default-icon.png").absolutePath

        val data = Data.Builder()
            .putString("name", uniqueName)
            .putString("description", description)
            .putString("imagePath", image)
            .putString("audioUri", audioUri.toString())
            .build()

        val request = OneTimeWorkRequestBuilder<AudioWorker>()
            .addTag(AudioWorker.TAG)
            .setInputData(data)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "process_new_audio",
            ExistingWorkPolicy.KEEP,
            request
        )
    }

    private fun getUniqueAudioName(baseName: String): String {
        val transcriptionsDir = File(context.filesDir, "transcriptions")

        if (!transcriptionsDir.exists() || !File(transcriptionsDir, baseName).exists()) {
            return baseName
        }

        var counter = 1
        var uniqueName: String

        do {
            uniqueName = "${baseName}_$counter"
            counter++
        } while (File(transcriptionsDir, uniqueName).exists())

        return uniqueName
    }
}
