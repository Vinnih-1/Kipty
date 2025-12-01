package io.github.vinnih.kipty.ui.player

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.media3.exoplayer.ExoPlayer
import com.whispercpp.whisper.WhisperContext
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.vinnih.kipty.data.transcription.AudioData
import io.github.vinnih.kipty.data.transcription.AudioTranscription
import io.github.vinnih.kipty.utils.timestamp
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val player: ExoPlayer,
    @ApplicationContext private val context: Context
) : ViewModel(),
    PlayerController {

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

    override suspend fun createTranscription(transcription: AudioData) {
        TODO("Not yet implemented")
    }

    override fun playAudio(audioData: AudioData) {
        TODO("Not yet implemented")
    }

    override fun pauseAudio() {
        TODO("Not yet implemented")
    }

    override fun seekTo(position: Float) {
        TODO("Not yet implemented")
    }
}
