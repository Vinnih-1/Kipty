package io.github.vinnih.kipty.ui.player

import io.github.vinnih.kipty.data.transcription.AudioData
import io.github.vinnih.kipty.data.transcription.AudioTranscription

class FakePlayerViewModel : PlayerController {
    override suspend fun convertTranscription(transcribedData: String): List<AudioTranscription> {
        TODO("Not yet implemented")
    }

    override suspend fun createTranscription(transcription: AudioData) {
        TODO("Not yet implemented")
    }

    override fun playAudio(audioData: AudioData) {
        TODO("Not yet implemented")
    }

    override fun pauseAudio() {
        TODO("Not yet implemented")
    }

    override fun seekTo(position: Float) {
        TODO("Not yet implemented")
    }
}
