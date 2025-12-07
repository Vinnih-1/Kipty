package io.github.vinnih.kipty.ui.audio

import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.AudioTranscription
import kotlinx.coroutines.flow.StateFlow

class FakeAudioViewModel : AudioController {
    override val isTranscribing: StateFlow<Boolean>
        get() = TODO("Not yet implemented")

    override fun convertTranscription(transcribedData: String): List<AudioTranscription> {
        TODO("Not yet implemented")
    }

    override fun transcribeAudio(audioEntity: AudioEntity, onSuccess: (AudioEntity) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun saveTranscription(audioEntity: AudioEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun getById(id: Int): AudioEntity {
        TODO("Not yet implemented")
    }
}
