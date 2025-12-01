package io.github.vinnih.kipty.ui.player

import io.github.vinnih.kipty.data.transcription.AudioData
import io.github.vinnih.kipty.data.transcription.AudioTranscription

interface PlayerController {
    suspend fun convertTranscription(transcribedData: String): List<AudioTranscription>

    suspend fun createTranscription(transcription: AudioData)

    fun playAudio(audioData: AudioData)

    fun pauseAudio()

    fun seekTo(position: Float)
}
