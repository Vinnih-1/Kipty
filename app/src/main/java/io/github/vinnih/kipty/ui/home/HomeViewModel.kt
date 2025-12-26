package io.github.vinnih.kipty.ui.home

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.vinnih.kipty.data.application.AppConfig
import io.github.vinnih.kipty.data.application.ApplicationData
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.repository.audio.AudioRepository
import io.github.vinnih.kipty.utils.copyTo
import io.github.vinnih.kipty.utils.createFile
import io.github.vinnih.kipty.utils.createFolder
import jakarta.inject.Inject
import java.io.File
import java.time.LocalDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val repository: AudioRepository
) : ViewModel(),
    HomeController {

    private val transcriptionsPath = File(context.filesDir, "transcriptions").createFolder()

    override fun openNotificationSettings() {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    override suspend fun createAudio(
        audio: File,
        image: File?,
        name: String?,
        description: String?
    ): AudioEntity = withContext(Dispatchers.IO) {
        val path = File(transcriptionsPath, audio.nameWithoutExtension).createFolder()

        audio.inputStream().copyTo(File(path, "audio.mp3").createFile())
        image?.inputStream()?.copyTo(File(path, "image.jpg").createFile())

        val entity = AudioEntity(
            name = if (name.isNullOrEmpty()) audio.nameWithoutExtension else name,
            description = description?.ifEmpty { null },
            path = path.canonicalPath,
            createdAt = LocalDateTime.now().toString(),
            duration = 0L
        )

        return@withContext entity.copy(uid = saveAudio(entity).toInt())
    }

    override suspend fun saveAudio(audioEntity: AudioEntity): Long = withContext(Dispatchers.IO) {
        return@withContext repository.save(audioEntity)
    }

    override suspend fun createDefault(data: suspend (File, File, File, File) -> Unit) {
        if (AppConfig(context).read().defaultSamplesLoaded) return

        withContext(Dispatchers.IO) {
            val samplesPath = File(context.cacheDir, "samples").createFolder()

            context.assets.list("samples/")!!.map { folder ->
                val sample = context.assets.list("samples/$folder")!!
                    .filter { it.endsWith(".mp3") }
                    .map { File(it) }
                    .first()
                val audio = File(samplesPath, "${sample.name}").createFile()
                val transcription = File(samplesPath, "raw_transcription.txt").createFile()
                val image = File(samplesPath, "image.jpg").createFile()
                val description = File(samplesPath, "description.txt")

                context.assets.open("samples/$folder/${sample.name}").copyTo(audio)
                context.assets.open("samples/$folder/raw_transcription.txt").copyTo(transcription)
                context.assets.open("samples/$folder/image.jpg").copyTo(image)
                context.assets.open("samples/$folder/description.txt").copyTo(description)

                data.invoke(audio, transcription, image, description)
            }
            AppConfig(context).write(ApplicationData("", true))
        }
    }
}
