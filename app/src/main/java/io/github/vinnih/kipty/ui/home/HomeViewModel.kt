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

    override fun openNotificationSettings() {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    override suspend fun createAudio(
        audio: String,
        image: String,
        name: String,
        description: String?,
        isDefault: Boolean
    ): AudioEntity = withContext(Dispatchers.IO) {
        val entity = AudioEntity(
            name = name,
            description = description?.ifEmpty { null },
            audioPath = audio,
            imagePath = image,
            isDefault = isDefault,
            createdAt = LocalDateTime.now().toString()
        )

        return@withContext entity.copy(uid = saveAudio(entity).toInt())
    }

    override suspend fun saveAudio(audioEntity: AudioEntity): Long = withContext(Dispatchers.IO) {
        return@withContext repository.save(audioEntity)
    }

    override suspend fun createDefault(data: suspend (String, String, String, String) -> Unit) {
        if (AppConfig(context).read().defaultSamplesLoaded) return

        withContext(Dispatchers.IO) {
            context.assets.list("samples/")!!.map { folder ->
                val sample = context.assets.list("samples/$folder")!!
                    .filter { it.endsWith(".mp3") }
                    .map { File(it) }
                    .first()
                val transcription = context.assets.open("samples/$folder/raw_transcription.txt")
                val description = context.assets.open("samples/$folder/description.txt")

                data.invoke(
                    "/samples/$folder/${sample.name}",
                    transcription.bufferedReader().readText(),
                    "samples/$folder/image.jpg",
                    description.bufferedReader().readText()
                )
            }
            AppConfig(context).write(ApplicationData("", true))
        }
    }
}
