package io.github.vinnih.kipty.ui.audio

import android.content.Context
import androidx.lifecycle.ViewModel
import com.whispercpp.whisper.WhisperContext
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.vinnih.kipty.data.transcription.AudioTranscription
import io.github.vinnih.kipty.utils.timestamp
import io.github.vinnih.kipty.utils.toFloatArray
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltViewModel
class AudioViewModel @Inject constructor(@ApplicationContext private val context: Context) :
    ViewModel(),
    AudioController {

    private val modelsPath = File(context.filesDir, "models")

    private val whisperContext: WhisperContext by lazy {
        val model = modelsPath.listFiles()!!.first()
        WhisperContext.createContextFromFile(model.absolutePath)
    }

    override suspend fun convertTranscription(transcribedData: String): List<AudioTranscription> =
        withContext(Dispatchers.IO) {
            transcribedData.trimIndent().split("\n").map { line ->
                val timestamp = line.take(31).timestamp()
                val text = line.drop(31)

                AudioTranscription(timestamp.first, timestamp.second, text)
            }.toList()
        }

    override suspend fun transcribeAudio(audio: File): String = withContext(Dispatchers.IO) {
        return@withContext whisperContext.transcribeData(audio.toFloatArray(context))
    }
}
