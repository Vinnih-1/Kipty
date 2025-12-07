package io.github.vinnih.kipty.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.vinnih.androidtranscoder.extractor.WavReader
import io.github.vinnih.kipty.data.application.AppConfig
import io.github.vinnih.kipty.data.application.ApplicationData
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.repository.AudioRepository
import jakarta.inject.Inject
import java.io.File
import java.time.LocalDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val config: AppConfig,
    private val repository: AudioRepository
) : ViewModel(),
    HomeController {
    private var _value = MutableStateFlow<List<AudioEntity>>(emptyList())
    override val value = _value.asStateFlow()

    private val modelsPath = File(context.filesDir, "models")
    private val samplesPath = File(context.filesDir, "samples")
    private val transcriptionsPath = File(context.filesDir, "transcriptions")

    override suspend fun createAudio(reader: WavReader): AudioEntity = withContext(Dispatchers.IO) {
        if (!transcriptionsPath.exists()) {
            transcriptionsPath.mkdirs()
        }

        val path = File(transcriptionsPath, reader.data.nameWithoutExtension).also {
            it.mkdirs()
        }
        reader.data.inputStream().use { inputStream ->
            File(path, "audio.wav").also {
                it.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }
        val entity = AudioEntity(
            name = reader.data.nameWithoutExtension,
            path = path.canonicalPath,
            createdAt = LocalDateTime.now().toString(),
            duration = reader.duration
        )
        repository.save(entity)

        return@withContext entity
    }

    override suspend fun updateAudioFiles(): Unit = withContext(Dispatchers.IO) {
        _value.update {
            repository.getAll()
        }
    }

    override suspend fun copyAssets(): List<File> = withContext(Dispatchers.IO) {
        val model = context.assets.list("models/")?.firstOrNull()
        val samples = ArrayList<File>()

        if (model != null) {
            modelsPath.mkdirs()
            samplesPath.mkdirs()
            config.write(ApplicationData(model, true))
            context.assets.open("models/$model").use { inputStream ->
                File(modelsPath, model).outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            context.assets.list("samples/")?.forEach { sample ->
                val file = File(context.filesDir, "samples/$sample")
                context.assets.open("samples/$sample").use { inputStream ->
                    file.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                samples.add(file)
            }
        }
        return@withContext samples
    }
}
