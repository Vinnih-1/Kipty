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
import io.github.vinnih.kipty.data.workers.PopulateWorker
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
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

data class PlayerUiState(
    val currentAudio: AudioEntity? = null,
    val progress: Float = 0f,
    val currentPosition: Long = 0L,
    val duration: Long = 0L
)

@OptIn(UnstableApi::class)
@HiltViewModel
class PlayerViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    override val player: Player,
    private val audioRepository: AudioRepository
) : ViewModel(),
    PlayerController {

    private val currentAudio = MutableStateFlow<AudioEntity?>(null)

    private val section = MutableStateFlow<AudioTranscription?>(null)

    private val progress: StateFlow<Pair<Float, Long>> = createProgressFlow()

    override val uiState: StateFlow<PlayerUiState> = combine(currentAudio, progress) {
            audio,
            progress
        ->
        PlayerUiState(
            currentAudio = audio,
            progress = progress.first,
            currentPosition = progress.second,
            duration = player.duration
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PlayerUiState())

    init {
        listener()

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
        } else {
            player.seekToDefaultPosition(index)
            player.play()
        }
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
