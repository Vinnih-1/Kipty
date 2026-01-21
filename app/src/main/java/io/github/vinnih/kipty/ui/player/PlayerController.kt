package io.github.vinnih.kipty.ui.player

import androidx.media3.exoplayer.ExoPlayer
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import kotlinx.coroutines.flow.StateFlow

interface PlayerController {

    val player: ExoPlayer

    val uiState: StateFlow<PlayerUiState>

    fun stopAudio()

    fun seekTo(audioEntity: AudioEntity, start: Long, end: Long)

    fun seekTo(audioEntity: AudioEntity)
}
