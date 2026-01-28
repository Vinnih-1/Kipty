package io.github.vinnih.kipty.ui.audio

import io.github.vinnih.kipty.data.FakeAudioData
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow

class FakeAudioViewModel : AudioController {

    override val uiState: StateFlow<AudioUiState>
        get() = MutableStateFlow(AudioUiState(canTranscribe = true))

    override fun transcribeAudio(audioEntity: AudioEntity) {}

    override fun getFlowById(id: Int): Flow<AudioEntity?> {
        val audioEntity = json.decodeFromString<AudioEntity>(FakeAudioData.audio_1888_11_13)
        return flow { emit(audioEntity) }
    }

    override suspend fun saveAudio(audioEntity: AudioEntity): Long {
        return 1
    }

    override suspend fun getById(id: Int): AudioEntity? {
        return null
    }

    override fun deleteAudio(audioEntity: AudioEntity) {}

    override fun cancelTranscriptionWork(audioEntity: AudioEntity) {}
}