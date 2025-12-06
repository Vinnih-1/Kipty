package io.github.vinnih.kipty.ui.home

import io.github.vinnih.androidtranscoder.extractor.WavReader
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

    override suspend fun createAudio(reader: WavReader): AudioEntity {
        TODO("Not yet implemented")
    }

    override suspend fun updateAudioFiles() {
        TODO("Not yet implemented")
    }

    override suspend fun copyAssets(): List<File> {
        TODO("Not yet implemented")
    }
}
