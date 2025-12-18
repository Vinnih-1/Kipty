package io.github.vinnih.kipty.ui.player

import androidx.media3.exoplayer.ExoPlayer
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import kotlinx.coroutines.flow.StateFlow

class FakePlayerViewModel : PlayerController {
    override val player: ExoPlayer
        get() = TODO("Not yet implemented")
    override val uiState: StateFlow<PlayerUiState>
        get() = TODO("Not yet implemented")
    override val currentAudio: StateFlow<AudioEntity?>
        get() = TODO("Not yet implemented")
    override val progress: StateFlow<Float>
        get() = TODO("Not yet implemented")

    override fun playAudio(audioEntity: AudioEntity) {
        TODO("Not yet implemented")
    }
    override fun pauseAudio() {
        TODO("Not yet implemented")
    }

    override fun seekTo(audioEntity: AudioEntity, start: Long, end: Long) {
        TODO("Not yet implemented")
    }
}
