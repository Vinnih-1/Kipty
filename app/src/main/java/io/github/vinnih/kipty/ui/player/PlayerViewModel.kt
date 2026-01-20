package io.github.vinnih.kipty.ui.player

import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.AudioTranscription
import io.github.vinnih.kipty.data.database.repository.audio.AudioRepository
import javax.inject.Inject
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json

data class PlayerUiState(
    val audioEntity: AudioEntity? = null,
    val progress: Float = 0f,
    val currentPosition: Long = 0L,
    val duration: Long = 0L
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    override val player: ExoPlayer,
    private val audioRepository: AudioRepository
) : ViewModel(),
    PlayerController {

    private val _currentAudio = MutableStateFlow<AudioEntity?>(null)
    override val currentAudio: StateFlow<AudioEntity?> = _currentAudio.asStateFlow()

    override val progress: StateFlow<Pair<Float, Long>> = createProgressFlow()

    override val uiState: StateFlow<PlayerUiState> = combine(currentAudio, progress) {
            audio,
            progress
        ->
        PlayerUiState(
            audioEntity = audio,
            progress = progress.first,
            currentPosition = progress.second,
            duration = player.duration
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PlayerUiState())

    override fun playPause(audioEntity: AudioEntity) {
        if (currentAudio.value?.uid == audioEntity.uid) {
            if (player.isPlaying) {
                player.pause()
            } else {
                player.play()
            }

            return
        }
        _currentAudio.value = audioEntity
        preparePlayer(audioEntity)
        player.play()
    }

    override fun stopAudio() {
        _currentAudio.value = null
        player.clearMediaItems()
        player.stop()
    }

    override fun seekTo(audioEntity: AudioEntity, start: Long, end: Long) {
        _currentAudio.value = audioEntity
        val extras = Bundle().apply {
            putLong("end", end)
        }
        preparePlayer(audioEntity, extras)
        player.seekTo(start)
        player.play()
    }

    override fun currentSection(): AudioTranscription? {
        val transcription = _currentAudio.value?.transcription ?: return null
        return transcription.find {
            it.start <= player.currentPosition &&
                it.end >= player.currentPosition
        }
    }

    private fun preparePlayer(audioEntity: AudioEntity, extras: Bundle = Bundle()) {
        player.clearMediaItems()

        val extras = extras.apply {
            putString("transcription", Json.encodeToString(audioEntity.transcription ?: listOf()))
        }
        val medatada = MediaMetadata.Builder().apply {
            setTitle(audioEntity.name)
            setDescription(audioEntity.description)
            setExtras(extras)
        }
        val mediaItem = MediaItem.Builder().apply {
            setMediaMetadata(medatada.build())
            setUri(
                Uri.Builder()
                    .scheme(if (audioEntity.isDefault) "asset" else "file")
                    .path(audioEntity.audioPath)
                    .build()
            )
        }

        player.setMediaItem(mediaItem.build())
        player.prepare()
    }

    private fun createProgressFlow(): StateFlow<Pair<Float, Long>> = flow {
        while (currentCoroutineContext().isActive) {
            if (player.isPlaying) {
                checkSectionEnd()
                emit(
                    Pair(
                        player.currentPosition.toFloat() / player.duration.toFloat(),
                        player.currentPosition
                    )
                )
                audioRepository.incrementPlayTime(_currentAudio.value!!.uid)
            }
            delay(1000)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Pair(0f, 0L))

    private fun checkSectionEnd() {
        val end = player.currentMediaItem?.mediaMetadata?.extras?.getLong("end") ?: 0L
        if (end != 0L && player.currentPosition >= end) {
            player.stop()
        }
    }
}
