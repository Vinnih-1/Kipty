package io.github.vinnih.kipty.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.repository.audio.AudioRepository
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
    private val samplesPath = File(context.filesDir, "samples").createFolder()
    private val transcriptionsPath = File(context.filesDir, "transcriptions").createFolder()

    override suspend fun createAudio(file: File, name: String?, description: String?): AudioEntity =
        withContext(Dispatchers.IO) {
            val path = File(transcriptionsPath, file.nameWithoutExtension).createFolder()

            file.inputStream().use { inputStream ->
                File(path, "audio.mp3").also {
                    it.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            }

            val entity = AudioEntity(
                name = if (name.isNullOrEmpty()) file.nameWithoutExtension else name,
                description = description,
                path = path.canonicalPath,
                createdAt = LocalDateTime.now().toString(),
                duration = 0L
            )

            return@withContext entity.copy(uid = saveAudio(entity).toInt())
        }

    override suspend fun saveAudio(audioEntity: AudioEntity): Long = withContext(Dispatchers.IO) {
        return@withContext repository.save(audioEntity)
    }

    override suspend fun copySamples(): List<Pair<File, File>> = withContext(Dispatchers.IO) {
        context.assets.list("samples/")!!.map { sample ->
            val audio = File(samplesPath, "$sample.mp3").createFile()
            val transcription = File(samplesPath, "$sample.txt").createFile()

            context.assets.open("samples/$sample/$sample.mp3").use { inputStream ->
                audio.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            context.assets.open("samples/$sample/transcription.txt").use { inputStream ->
                transcription.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            Pair(audio, transcription)
        }
    }
}
