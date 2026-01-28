package io.github.vinnih.kipty.ui.player

import androidx.media3.common.Player
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import kotlinx.coroutines.flow.StateFlow

interface PlayerController {
    val player: Player
    val uiState: StateFlow<PlayerUiState>
    fun stopAudio()
    fun seekTo(audioEntity: AudioEntity)
    fun seekTo(
        audioEntity: AudioEntity,
        start: Long,
        end: Long
    )
    fun seekTo(position: Long)
    fun changePlaybackSpeed()
}
