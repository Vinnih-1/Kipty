package io.github.vinnih.kipty.ui.player

import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.service.player.PlayerService
import kotlinx.coroutines.flow.StateFlow

interface PlayerController {

    val playerService: PlayerService

    val uiState: StateFlow<PlayerUiState>

    fun stopAudio()

    fun seekTo(audioEntity: AudioEntity)

    fun seekTo(audioEntity: AudioEntity, start: Long, end: Long)

    fun seekTo(position: Long)

    fun changePlaybackSpeed()
}
