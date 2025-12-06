package io.github.vinnih.kipty.ui.audio

import io.github.vinnih.kipty.data.database.entity.AudioTranscription
import java.io.File

class FakeAudioViewModel : AudioController {
    override suspend fun convertTranscription(transcribedData: String): List<AudioTranscription> {
        TODO("Not yet implemented")
    }

    override suspend fun transcribeAudio(audio: File): String {
        TODO("Not yet implemented")
    }
}
