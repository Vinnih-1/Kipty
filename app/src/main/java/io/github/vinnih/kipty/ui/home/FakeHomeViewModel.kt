package io.github.vinnih.kipty.ui.home

import io.github.vinnih.androidtranscoder.extractor.WavReader
import io.github.vinnih.kipty.data.FakeAudio
import io.github.vinnih.kipty.data.transcription.AudioData
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

    override val value: StateFlow<List<AudioData>>
        get() {
            return MutableStateFlow(
                listOf(json.decodeFromString<AudioData>(FakeAudio.audio_1865_02_01))
            ).asStateFlow()
        }

    override suspend fun createAudio(audioData: AudioData, reader: WavReader) {
        TODO("Not yet implemented")
    }

    override suspend fun updateAudioFiles() {
        TODO("Not yet implemented")
    }

    override suspend fun copyAssets(): List<File> {
        TODO("Not yet implemented")
    }
}
