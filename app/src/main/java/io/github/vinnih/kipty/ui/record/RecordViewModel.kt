package io.github.vinnih.kipty.ui.record

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.vinnih.kipty.data.database.entity.AudioTranscription
import io.github.vinnih.kipty.data.database.entity.SpeechEntity
import io.github.vinnih.kipty.data.database.repository.speech.SpeechRepository
import io.github.vinnih.kipty.data.service.recording.AudioRecorder
import io.github.vinnih.kipty.data.service.recording.SpeechResult
import io.github.vinnih.kipty.utils.AudioResampler
import io.github.vinnih.kipty.utils.AudioResampler.resample
import io.github.vinnih.kipty.utils.createFolder
import io.github.vinnih.kipty.utils.normalizeAudio
import java.io.File
import java.time.LocalDateTime
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class RecordUiState(
    val isRecording: Boolean = false,
    val amplitudes: List<Float> = emptyList(),
    val recordingTime: Long = 0L,
    val result: Pair<String, Int> = Pair("", 0)
)

@HiltViewModel
class RecordViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val audioRecorder: AudioRecorder,
    private val speechResult: SpeechResult,
    private val speechRepository: SpeechRepository
) : ViewModel(),
    RecordController {

    private val isRecording = MutableStateFlow(false)

    private val amplitudes = MutableStateFlow<List<Float>>(emptyList())

    private val recordingTime = MutableStateFlow(0L)

    private val filesPath = MutableStateFlow(Pair("", ""))

    private val result = MutableStateFlow(Pair("", 0))

    private var timerJob: Job? = null

    override val uiState: StateFlow<RecordUiState> = combine(
        isRecording,
        amplitudes,
        recordingTime,
        result
    ) { recording, amps, time, result ->
        RecordUiState(
            isRecording = recording,
            amplitudes = amps,
            recordingTime = time,
            result = result
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RecordUiState())

    init {
        viewModelScope.launch {
            audioRecorder.amplitudeFlow.collect { amps ->
                amplitudes.value = amps
            }
        }
    }

    override fun toggleRecording(audioPath: String) {
        if (isRecording.value) {
            stopRecording()
        } else {
            startRecording(audioPath)
        }
    }

    override fun abortRecording() {
        val recordPath = filesPath.value.first

        if (recordPath.isNotEmpty()) {
            File(recordPath).delete()
        }

        stopRecording()
    }

    override suspend fun calculatePronunciationScore(phrase: AudioTranscription): Unit =
        withContext(Dispatchers.IO) {
            val recordPath = filesPath.value.first
            val resampledFile = File(recordPath).resample(
                format = AudioResampler.OutputFormat.WAV,
                context = context
            )
            val audioBytes = resampledFile.readBytes()

            result.value = speechResult.calculatePronunciationScore(
                expected = phrase.text,
                floatArray = normalizeAudio(audioBytes)
            )
            resampledFile.delete()
            transferTo()
            saveSpeech(phrase)
            clearAll()
        }

    private fun startRecording(audioPath: String) {
        val outputFile = File(
            context.cacheDir,
            "recording_${System.currentTimeMillis()}.m4a"
        )

        filesPath.value = Pair(outputFile.absolutePath, audioPath)
        audioRecorder.startRecording(outputFile)
        isRecording.value = true
        recordingTime.value = 0

        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                recordingTime.value += 1
            }
        }
    }

    private fun stopRecording() {
        audioRecorder.stopRecording()
    }

    private fun transferTo() {
        val recordPath = filesPath.value.first
        val audioPath = filesPath.value.second
        val path = File(
            context.filesDir,
            "speeches" + File.separatorChar + audioPath
                .substringAfterLast("/")
                .substringBeforeLast(".")
        ).createFolder()
        val audioFile = File(recordPath)
        val resampledFile = audioFile.resample(
            context = context,
            format = AudioResampler.OutputFormat.OPUS
        )
        val destination = File(path, resampledFile.name)

        filesPath.value = Pair(destination.absolutePath, audioPath)
        resampledFile.copyTo(destination, true)
        audioFile.delete()
        resampledFile.delete()
    }

    private suspend fun saveSpeech(phrase: AudioTranscription): Long = withContext(Dispatchers.IO) {
        val speechEntity = SpeechEntity(
            audioPath = filesPath.value.second,
            speechPath = filesPath.value.first,
            phrase = phrase,
            result = result.value.first,
            createdAt = LocalDateTime.now().toString()
        )

        return@withContext speechRepository.save(speechEntity)
    }

    private fun clearAll() {
        isRecording.value = false
        amplitudes.value = emptyList()
        recordingTime.value = 0
        filesPath.value = Pair("", "")
        result.value = Pair("", 0)
        timerJob?.cancel()
    }
}
