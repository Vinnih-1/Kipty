package io.github.vinnih.kipty.ui.player

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.AudioTranscription
import io.github.vinnih.kipty.data.database.repository.audio.AudioRepository
import io.github.vinnih.kipty.data.service.recording.AudioRecorder
import io.github.vinnih.kipty.data.service.recording.SpeechResult
import io.github.vinnih.kipty.data.workers.PopulateWorker
import io.github.vinnih.kipty.utils.AudioResampler
import io.github.vinnih.kipty.utils.AudioResampler.resample
import io.github.vinnih.kipty.utils.normalizeAudio
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class PlaybackSpeed(val value: Float, val text: String) {
    HALF(0.5f, "0.5x"),
    THREE_QUARTERS(0.75f, "0.75x"),
    NORMAL(1.0f, "1x"),
    ONE_AND_A_QUARTER(1.25f, "1.25x"),
    ONE_AND_A_HALF(1.5f, "1.5x"),
    DOUBLE(2.0f, "2x")
}

data class RecordingState(
    val isRecording: Boolean = false,
    val amplitudes: List<Float> = emptyList(),
    val recordingTime: Long = 0L,
    val result: Pair<String, Int> = Pair("", 0)
)

data class PlayerUiState(
    val currentAudio: AudioEntity? = null,
    val progress: Float = 0f,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val playbackSpeed: PlaybackSpeed = PlaybackSpeed.NORMAL,
    val recordingState: RecordingState = RecordingState()
)

@OptIn(UnstableApi::class)
@HiltViewModel
class PlayerViewModel
@Inject constructor(
    @ApplicationContext private val context: Context,
    override val player: Player,
    private val audioRepository: AudioRepository,
    private val audioRecorder: AudioRecorder,
    private val speechResult: SpeechResult
) : ViewModel(),
    PlayerController {

    private val currentAudio = MutableStateFlow<AudioEntity?>(null)

    private val section = MutableStateFlow<AudioTranscription?>(null)

    private val progress: StateFlow<Pair<Float, Long>> = createProgressFlow()

    private val playbackSpeed = MutableStateFlow(PlaybackSpeed.NORMAL)

    private val isRecording = MutableStateFlow(false)

    private val amplitudes = MutableStateFlow<List<Float>>(emptyList())

    private val recordingTime = MutableStateFlow(0L)

    private val audioPath = MutableStateFlow<String?>(null)

    private val result = MutableStateFlow(Pair("", 0))

    private var timerJob: Job? = null

    private val recordingState = combine(
        isRecording,
        amplitudes,
        recordingTime,
        result
    ) { recording, amps, time, result ->
        RecordingState(
            isRecording = recording,
            amplitudes = amps,
            recordingTime = time,
            result = result
        )
    }

    override val uiState: StateFlow<PlayerUiState> = combine(
        currentAudio,
        progress,
        playbackSpeed,
        recordingState
    ) { audio, progress, speed, recording ->
        PlayerUiState(
            currentAudio = audio,
            progress = progress.first,
            currentPosition = progress.second,
            duration = player.duration,
            playbackSpeed = speed,
            recordingState = recording
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PlayerUiState())

    override suspend fun calculatePronunciationScore(expected: String) =
        withContext(Dispatchers.IO) {
            val resampledFile = File(audioPath.value!!).resample(
                format = AudioResampler.OutputFormat.WAV,
                context = context
            )
            val audioBytes = resampledFile.readBytes()

            result.value = speechResult.calculatePronunciationScore(
                expected = expected,
                floatArray = normalizeAudio(audioBytes)
            )
        }

    init {
        listener()

        viewModelScope.launch {
            audioRecorder.amplitudeFlow.collect { amps ->
                amplitudes.value = amps
            }
        }

        viewModelScope.launch {
            while (isActive) {
                if (player.isPlaying) {
                    viewModelScope.launch(Dispatchers.IO) {
                        audioRepository.incrementPlayTime(currentAudio.value!!.uid)
                    }
                }
                delay(1000)
            }
        }

        viewModelScope.launch {
            val workManager = WorkManager.getInstance(context)

            workManager.getWorkInfosByTagFlow(PopulateWorker.TAG)
                .first { workInfos ->
                    workInfos.isNotEmpty() && workInfos.all { it.state.isFinished }
                }
            player.clearMediaItems()
            audioRepository.getAllFlow()
                .dropWhile { it.isEmpty() }
                .first()
                .filter { !it.transcription.isNullOrEmpty() }
                .forEachIndexed { index, it ->
                    if (index == 0) currentAudio.value = it
                    preparePlayer(it)
                }
        }
    }

    override fun toggleRecording() {
        if (isRecording.value) {
            stopRecording()
        } else {
            startRecording()
        }
    }

    private fun startRecording() {
        val outputFile = File(
            context.cacheDir,
            "recording_${System.currentTimeMillis()}.m4a"
        )

        audioPath.value = outputFile.absolutePath
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
        isRecording.value = false
        timerJob?.cancel()
    }

    private fun listener() {
        player.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                if (mediaItem == null) return

                viewModelScope.launch(Dispatchers.IO) {
                    val audioEntity =
                        audioRepository.getById(mediaItem.mediaId.toInt()) ?: return@launch
                    currentAudio.value = audioEntity
                }
            }
        })
    }

    override fun stopAudio() {
        if (player.hasNextMediaItem()) {
            player.seekToNextMediaItem()
        } else {
            currentAudio.value = null
            player.stop()
            player.clearMediaItems()
        }
    }

    override fun seekTo(audioEntity: AudioEntity, start: Long, end: Long) {
        val index = findMediaItemIndexById(audioEntity.uid)

        if (index == -1) {
            preparePlayer(audioEntity)
        }
        section.value = AudioTranscription(start, end, "")
        player.seekTo(index, start)
        player.play()
    }

    override fun seekTo(audioEntity: AudioEntity) {
        currentAudio.value = audioEntity

        val index = findMediaItemIndexById(audioEntity.uid)

        if (index == -1) {
            preparePlayer(audioEntity)
            player.seekToDefaultPosition(player.mediaItemCount - 1)
        } else {
            player.seekToDefaultPosition(index)
        }
        player.play()
    }

    override fun seekTo(position: Long) {
        player.seekTo(position)
    }

    override fun changePlaybackSpeed() {
        val allSpeeds = PlaybackSpeed.entries
        val currentIndex = allSpeeds.indexOf(playbackSpeed.value)
        val nextSpeed = allSpeeds[(currentIndex + 1) % allSpeeds.size]
        playbackSpeed.value = nextSpeed
        player.setPlaybackSpeed(nextSpeed.value)
    }

    private fun preparePlayer(audioEntity: AudioEntity) {
        val metadata = MediaMetadata.Builder().apply {
            setTitle(audioEntity.name)
            setDescription(audioEntity.description)
        }.build()
        val mediaItem = MediaItem.Builder().apply {
            setMediaMetadata(metadata)
            setMediaId("${audioEntity.uid}")
            setUri(
                Uri.Builder()
                    .scheme(if (audioEntity.isDefault) "asset" else "file")
                    .path(audioEntity.audioPath)
                    .build()
            )
        }.build()

        player.addMediaItem(mediaItem)
        player.prepare()
    }

    private fun createProgressFlow(): StateFlow<Pair<Float, Long>> = flow {
        while (currentCoroutineContext().isActive) {
            if (player.isPlaying) {
                if (section.value != null) {
                    val end = section.value!!.end
                    if (end != 0L && player.currentPosition >= end) {
                        player.pause()
                        section.value = null
                    }
                }
                emit(
                    Pair(
                        player.currentPosition.toFloat() / player.duration.toFloat(),
                        player.currentPosition
                    )
                )
            }
            delay(10)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Pair(0f, 0L))

    private fun findMediaItemIndexById(mediaId: Int): Int {
        for (i in 0 until player.mediaItemCount) {
            if (player.getMediaItemAt(i).mediaId == mediaId.toString()) {
                return i
            }
        }
        return -1
    }
}
