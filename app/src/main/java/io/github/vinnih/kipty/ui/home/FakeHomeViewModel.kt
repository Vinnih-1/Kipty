package io.github.vinnih.kipty.ui.home

import io.github.vinnih.kipty.data.FakeAudio
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

class FakeHomeViewModel : HomeController {

    @OptIn(ExperimentalSerializationApi::class)
    val json = Json {
        allowTrailingComma = true
    }

    override val value: StateFlow<List<AudioEntity>>
        get() {
            return MutableStateFlow(
                listOf(json.decodeFromString<AudioEntity>(FakeAudio.audio_1865_02_01))
            ).asStateFlow()
        }

    override suspend fun createAudio(file: File, name: String?, description: String?): AudioEntity {
        TODO("Not yet implemented")
    }

    override suspend fun saveAudio(audioEntity: AudioEntity): Long {
        TODO("Not yet implemented")
    }

    override suspend fun updateAudioFiles() {
        TODO("Not yet implemented")
    }

    override suspend fun copySamples(): List<Pair<File, File>> {
        TODO("Not yet implemented")
    }
}
