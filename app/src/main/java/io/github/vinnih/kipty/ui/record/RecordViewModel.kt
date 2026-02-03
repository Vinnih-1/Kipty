package io.github.vinnih.kipty.ui.record

import android.content.Context
import android.os.Looper
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
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
    private val speechRepository: SpeechRepository,
    private val player: Player
) : ViewModel(),
    RecordController {

    @delegate:UnstableApi
    private val tempPlayer: ExoPlayer by lazy {
        ExoPlayer.Builder(context)
            .setLooper(Looper.getMainLooper())
            .build()
    }

    private val isTempAudioPlaying = MutableStateFlow(false)

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

    override suspend fun getById(id: Int): SpeechEntity? = speechRepository.getById(id)

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
        clearAll()
    }

    override suspend fun calculatePronunciationScore(phrase: AudioTranscription): Long =
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

            return@withContext saveSpeech(phrase)
        }

    private fun startRecording(audioPath: String) {
        if (player.isPlaying) player.pause()

        val outputFile = File(
            context.cacheDir,
            "recording_${System.currentTimeMillis()}.m4a"
        )

        filesPath.value = Pair(outputFile.absolutePath, audioPath)
        audioRecorder.startRecording(outputFile)
        isRecording.value = true
        recordingTime.value = 0

        timerJob = viewModelScope.launch {
            while (isRecording.value) {
                delay(1000)
                recordingTime.value += 1
            }
        }
    }

    private fun stopRecording() {
        audioRecorder.stopRecording()
        timerJob?.cancel()
    }

    private fun transferTo() {
        val recordPath = filesPath.value.first
        val audioPath = filesPath.value.second

        val audioFile = File(audioPath)
        val parentFolderName = audioFile.parentFile?.name ?: "unknown"

        val path = File(
            context.filesDir,
            "speeches${File.separator}$parentFolderName"
        ).createFolder()
        val speechFile = File(recordPath)
        val resampledFile = speechFile.resample(
            bitrate = 192,
            context = context,
            format = AudioResampler.OutputFormat.OPUS
        )
        val destination = File(path, resampledFile.name)

        filesPath.value = Pair(destination.absolutePath, audioPath)
        resampledFile.copyTo(destination, true)
        speechFile.delete()
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

    override fun clearAll() {
        isRecording.value = false
        amplitudes.value = emptyList()
        recordingTime.value = 0
        filesPath.value = Pair("", "")
        result.value = Pair("", 0)
        timerJob?.cancel()
        tempPlayer.stop()
    }

    override fun playTempAudio(audioFilePath: String) {
        viewModelScope.launch(Dispatchers.Main) {
            if (player.isPlaying) player.pause()

            val mediaItem = MediaItem.fromUri(audioFilePath.toUri())

            tempPlayer.setMediaItem(mediaItem)
            tempPlayer.prepare()
            tempPlayer.play()
            isTempAudioPlaying.value = true

            tempPlayer.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED) {
                        isTempAudioPlaying.value = false
                        tempPlayer.removeListener(this)
                    }
                }
            })
        }
    }

    override fun stopTempAudio() {
        tempPlayer.stop()
        isTempAudioPlaying.value = false
    }

    override fun onCleared() {
        super.onCleared()
        tempPlayer.release()
    }
}
