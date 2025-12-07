package io.github.vinnih.kipty.ui.player

import androidx.media3.exoplayer.ExoPlayer
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import kotlinx.coroutines.flow.StateFlow

interface PlayerController {

    val player: ExoPlayer

    val currentAudio: StateFlow<AudioEntity?>

    fun playAudio(audioEntity: AudioEntity)

    fun pauseAudio()

    fun seekTo(position: Long)
}
