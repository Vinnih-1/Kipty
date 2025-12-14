package io.github.vinnih.kipty.ui.audio

import androidx.work.WorkInfo
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.AudioTranscription
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class FakeAudioViewModel : AudioController {
    override val audioUiState: StateFlow<AudioEntity?>
        get() = TODO("Not yet implemented")

    override fun transcribeAudio(
        audioEntity: AudioEntity,
        onSuccess: (List<AudioTranscription>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun saveTranscription(audioEntity: AudioEntity) {
        TODO("Not yet implemented")
    }

    override fun observeTranscriptionWork(): Flow<List<WorkInfo>> {
        TODO("Not yet implemented")
    }

    override fun cancelTranscriptionWork(audioEntity: AudioEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun getById(id: Int): AudioEntity {
        TODO("Not yet implemented")
    }

    override suspend fun getCurrent(id: Int) {
        TODO("Not yet implemented")
    }
}
