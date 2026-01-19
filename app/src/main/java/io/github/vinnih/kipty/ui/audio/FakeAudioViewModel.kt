package io.github.vinnih.kipty.ui.audio

import androidx.work.WorkInfo
import io.github.vinnih.kipty.data.FakeAudioData
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeAudioViewModel : AudioController {
    override val allAudios: StateFlow<List<AudioEntity>>
        get() = MutableStateFlow(
            listOf(
                json.decodeFromString(FakeAudioData.audio_1865_02_01),
                json.decodeFromString(FakeAudioData.audio_1888_11_13)
            )
        )

    override fun transcribeAudio(
        audioEntity: AudioEntity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun saveAudio(audioEntity: AudioEntity): Long {
        TODO("Not yet implemented")
    }

    override fun deleteAudio(audioEntity: AudioEntity) {
        TODO("Not yet implemented")
    }

    override fun observeTranscriptionWork(): Flow<List<WorkInfo>> {
        TODO("Not yet implemented")
    }

    override fun cancelTranscriptionWork(audioEntity: AudioEntity) {
        TODO("Not yet implemented")
    }

    override fun getById(id: Int): Flow<AudioEntity?> {
        TODO("Not yet implemented")
    }
}
