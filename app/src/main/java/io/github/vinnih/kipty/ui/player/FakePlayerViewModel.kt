package io.github.vinnih.kipty.ui.player

import androidx.media3.exoplayer.ExoPlayer
import io.github.vinnih.kipty.data.database.entity.AudioEntity

class FakePlayerViewModel : PlayerController {
    override val player: ExoPlayer
        get() = TODO("Not yet implemented")

    override fun playAudio(audioEntity: AudioEntity) {
        TODO("Not yet implemented")
    }

    override fun pauseAudio() {
        TODO("Not yet implemented")
    }

    override fun seekTo(position: Float) {
        TODO("Not yet implemented")
    }
}
