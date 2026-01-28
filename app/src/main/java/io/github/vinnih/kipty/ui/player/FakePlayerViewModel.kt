package io.github.vinnih.kipty.ui.player

import androidx.media3.common.Player
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakePlayerViewModel : PlayerController {
    override val player: Player = FakeKiptyPlayer()

    override val uiState: StateFlow<PlayerUiState> = MutableStateFlow(PlayerUiState())
    override fun stopAudio() {}

    override fun seekTo(audioEntity: AudioEntity) {}

    override fun seekTo(audioEntity: AudioEntity, start: Long, end: Long) {}
    override fun seekTo(position: Long) {}
    override fun changePlaybackSpeed() {}
}
