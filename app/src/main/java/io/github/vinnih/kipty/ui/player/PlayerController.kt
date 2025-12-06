package io.github.vinnih.kipty.ui.player

import io.github.vinnih.kipty.data.database.entity.AudioEntity

interface PlayerController {

    fun playAudio(audioEntity: AudioEntity)

    fun pauseAudio()

    fun seekTo(position: Float)
}
