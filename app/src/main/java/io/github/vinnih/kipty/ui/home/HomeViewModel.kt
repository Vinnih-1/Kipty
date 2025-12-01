package io.github.vinnih.kipty.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.vinnih.androidtranscoder.utils.toWavReader
import io.github.vinnih.kipty.data.application.AppConfig
import io.github.vinnih.kipty.data.application.ApplicationData
import io.github.vinnih.kipty.data.transcription.AudioData
import io.github.vinnih.kipty.data.transcription.AudioDetails
import jakarta.inject.Inject
import java.io.File
import java.time.LocalDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    val config: AppConfig
) : ViewModel(),
    HomeController {
    private var _value = MutableStateFlow<List<AudioData>>(emptyList())
    override val value = _value.asStateFlow()

    private val modelsPath = File(context.filesDir, "models")
    private val samplesPath = File(context.filesDir, "samples")

    override suspend fun loadAudioFiles() {
        TODO("Not yet implemented")
    }

    override suspend fun copyAssets() = withContext(Dispatchers.IO) {
        val model = context.assets.list("models/")?.firstOrNull()
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
                val reader = file.toWavReader(context.cacheDir)
                val data =
                    AudioData(
                        AudioDetails(
                            sample,
                            "Default audio samples",
                            0,
                            reader.duration,
                            LocalDateTime.now().toString(),
                            sample
                        ),
                        null
                    )
                reader.dispose()
            }
        }
    }
}
