package io.github.vinnih.kipty.ui.player

import io.github.vinnih.kipty.data.transcription.AudioData

interface PlayerController {

    fun playAudio(audioData: AudioData)

    fun pauseAudio()

    fun seekTo(position: Float)
}
