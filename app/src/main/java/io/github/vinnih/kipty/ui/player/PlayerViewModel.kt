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
import java.io.File
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

data class PlayerUiState(val audioEntity: AudioEntity? = null, val progress: Float = 0f)

@HiltViewModel
class PlayerViewModel @Inject constructor(override val player: ExoPlayer) :
    ViewModel(),
    PlayerController {

    private val _currentAudio = MutableStateFlow<AudioEntity?>(null)
    override val currentAudio: StateFlow<AudioEntity?> = _currentAudio.asStateFlow()

    override val progress: StateFlow<Float> = createProgressFlow()

    override val uiState: StateFlow<PlayerUiState> = combine(currentAudio, progress) {
            audio,
            progress
        ->
        PlayerUiState(
            audioEntity = audio,
            progress = progress
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PlayerUiState())

    override fun playAudio(audioEntity: AudioEntity) {
        _currentAudio.value = audioEntity
        preparePlayer(audioEntity)
        player.play()
    }

    override fun pauseAudio() {
        player.pause()
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
            setUri(Uri.fromFile(File(audioEntity.path, "audio.mp3")))
        }

        player.setMediaItem(mediaItem.build())
        player.prepare()
    }

    private fun createProgressFlow(): StateFlow<Float> = flow {
        while (currentCoroutineContext().isActive) {
            if (player.isPlaying) {
                checkSectionEnd()
                emit(player.currentPosition.toFloat() / player.duration.toFloat())
            }
            delay(100)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    private fun checkSectionEnd() {
        val end = player.currentMediaItem?.mediaMetadata?.extras?.getLong("end") ?: 0L
        if (end != 0L && player.currentPosition >= end) {
            player.stop()
        }
    }
}
