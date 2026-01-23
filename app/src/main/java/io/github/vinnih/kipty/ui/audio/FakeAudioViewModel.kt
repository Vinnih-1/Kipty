package io.github.vinnih.kipty.ui.audio

import io.github.vinnih.kipty.data.database.entity.AudioEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class FakeAudioViewModel : AudioController {

    override val uiState: StateFlow<AudioUiState>
        get() = TODO("Not yet implemented")

    override fun transcribeAudio(audioEntity: AudioEntity) {
        TODO("Not yet implemented")
    }

    override fun getFlowById(id: Int): Flow<AudioEntity?> {
        TODO("Not yet implemented")
    }

    override suspend fun saveAudio(audioEntity: AudioEntity): Long {
        TODO("Not yet implemented")
    }

    override suspend fun getById(id: Int): AudioEntity? {
        TODO("Not yet implemented")
    }

    override fun deleteAudio(audioEntity: AudioEntity) {
        TODO("Not yet implemented")
    }

    override fun cancelTranscriptionWork(audioEntity: AudioEntity) {
        TODO("Not yet implemented")
    }
}
