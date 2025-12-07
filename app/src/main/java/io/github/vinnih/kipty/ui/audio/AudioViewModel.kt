package io.github.vinnih.kipty.ui.audio

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whispercpp.whisper.WhisperContext
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.AudioTranscription
import io.github.vinnih.kipty.data.database.repository.AudioRepository
import io.github.vinnih.kipty.utils.timestamp
import io.github.vinnih.kipty.utils.toFloatArray
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class AudioViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: AudioRepository
) : ViewModel(),
    AudioController {

    private val _isTranscribing = MutableStateFlow(false)
    override val isTranscribing: StateFlow<Boolean> = _isTranscribing.asStateFlow()

    private val modelsPath = File(context.filesDir, "models")

    private val whisperContext: WhisperContext by lazy {
        val model = modelsPath.listFiles()!!.first()
        WhisperContext.createContextFromFile(model.absolutePath)
    }

    override fun convertTranscription(transcribedData: String): List<AudioTranscription> =
        transcribedData.trimIndent().split("\n").map { line ->
            val timestamp = line.take(31).timestamp()
            val text = line.drop(31)

            AudioTranscription(timestamp.first, timestamp.second, text)
        }.toList()

    override fun transcribeAudio(audioEntity: AudioEntity, onSuccess: (AudioEntity) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _isTranscribing.value = true
            val file = File(audioEntity.path).resolve("audio.wav")
            val transcribedData = whisperContext.transcribeData(file.toFloatArray(context))
            val transcription = convertTranscription(transcribedData)
            val audioEntityCopy = audioEntity.copy(transcription = transcription)

            onSuccess(audioEntityCopy)
            saveTranscription(audioEntityCopy)
            _isTranscribing.value = false
        }
    }

    override fun saveTranscription(audioEntity: AudioEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.save(audioEntity)
        }
    }

    override suspend fun getById(id: Int): AudioEntity = withContext(Dispatchers.IO) {
        return@withContext repository.getById(id)!!
    }
}
