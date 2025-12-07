package io.github.vinnih.kipty.ui.player

import androidx.media3.exoplayer.ExoPlayer
import io.github.vinnih.kipty.data.database.entity.AudioEntity

interface PlayerController {

    val player: ExoPlayer

    fun playAudio(audioEntity: AudioEntity)

    fun pauseAudio()

    fun seekTo(position: Float)
}
