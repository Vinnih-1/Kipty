package io.github.vinnih.kipty.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.service.player.PlayerService
import io.github.vinnih.kipty.domain.repository.AudioRepository
import io.github.vinnih.kipty.domain.usecase.player.ChangePlaybackSpeedUseCase
import io.github.vinnih.kipty.domain.usecase.player.LoadAudiosUseCase
import io.github.vinnih.kipty.domain.usecase.player.SeekToUseCase
import io.github.vinnih.kipty.domain.usecase.player.StopAudioUseCase
import io.github.vinnih.kipty.domain.usecase.player.TrackPlayTimeUseCase
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class PlaybackSpeed(val value: Float, val text: String) {
    HALF(0.5f, "0.5x"),
    THREE_QUARTERS(0.75f, "0.75x"),
    NORMAL(1.0f, "1x"),
    ONE_AND_A_QUARTER(1.25f, "1.25x"),
    ONE_AND_A_HALF(1.5f, "1.5x"),
    DOUBLE(2.0f, "2x")
}

data class PlayerUiState(
    val currentAudio: AudioEntity? = null,
    val progress: Float = 0f,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val playbackSpeed: PlaybackSpeed = PlaybackSpeed.NORMAL
)

@HiltViewModel
class PlayerViewModel
@Inject constructor(
    override val playerService: PlayerService,
    private val audioRepository: AudioRepository,
    private val loadAudiosUseCase: LoadAudiosUseCase,
    private val trackPlayTimeUseCase: TrackPlayTimeUseCase,
    private val stopAudioUseCase: StopAudioUseCase,
    private val seekToUseCase: SeekToUseCase,
    private val changePlaybackSpeedUseCase: ChangePlaybackSpeedUseCase
) : ViewModel(),
    PlayerController {

    private val currentAudio = MutableStateFlow<AudioEntity?>(null)

    private val progress: StateFlow<Pair<Float, Long>> = createProgressFlow()

    private val playbackSpeed = MutableStateFlow(PlaybackSpeed.NORMAL)

    override val uiState: StateFlow<PlayerUiState> = combine(
        currentAudio,
        progress,
        playbackSpeed
    ) { audio, progress, speed ->
        PlayerUiState(
            currentAudio = audio,
            progress = progress.first,
            currentPosition = progress.second,
            duration = playerService.duration,
            playbackSpeed = speed
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PlayerUiState())

    init {
        observeSectionBoundary()

        playerService.onMediaItemTransition { mediaId ->
            viewModelScope.launch(Dispatchers.IO) {
                val audio = audioRepository.getById(mediaId.toInt()) ?: return@launch
                currentAudio.value = audio
            }
        }

        viewModelScope.launch {
            trackPlayTimeUseCase { currentAudio.value }
        }

        viewModelScope.launch {
            loadAudiosUseCase { audio -> currentAudio.value = audio }
        }
    }

    override fun stopAudio(): Unit = stopAudioUseCase { currentAudio.value = null }

    override fun seekTo(audioEntity: AudioEntity, start: Long, end: Long) {
        viewModelScope.launch {
            seekToUseCase(
                audioEntity,
                start,
                end
            ) { playerService.section = it }
        }
    }

    override fun seekTo(audioEntity: AudioEntity): Unit = seekToUseCase(audioEntity) {
        currentAudio.value = it
    }

    override fun seekTo(position: Long): Unit = seekToUseCase(position)

    override fun changePlaybackSpeed() {
        playbackSpeed.value = changePlaybackSpeedUseCase(playbackSpeed.value)
    }

    private fun observeSectionBoundary() {
        viewModelScope.launch {
            while (isActive) {
                playerService.checkSectionBoundary()
                delay(10)
            }
        }
    }

    private fun createProgressFlow(): StateFlow<Pair<Float, Long>> = flow {
        while (currentCoroutineContext().isActive) {
            if (playerService.isPlaying) {
                emit(
                    Pair(
                        playerService.currentPosition.toFloat() / playerService.duration.toFloat(),
                        playerService.currentPosition
                    )
                )
            }
            delay(10)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Pair(0f, 0L))
}
