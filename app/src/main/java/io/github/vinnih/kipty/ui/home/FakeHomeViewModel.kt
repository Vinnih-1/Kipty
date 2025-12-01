package io.github.vinnih.kipty.ui.home

import io.github.vinnih.kipty.data.transcription.AudioData
import kotlinx.coroutines.flow.StateFlow

class FakeHomeViewModel : HomeController {
    override val value: StateFlow<List<AudioData>>
        get() {
            TODO()
        }

    override suspend fun loadAudioFiles() {
        TODO("Not yet implemented")
    }

    override suspend fun copyAssets() {
        TODO("Not yet implemented")
    }
}
