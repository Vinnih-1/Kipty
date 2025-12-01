package io.github.vinnih.kipty.ui.home

import io.github.vinnih.kipty.data.transcription.AudioData
import kotlinx.coroutines.flow.StateFlow

interface HomeController {
    val value: StateFlow<List<AudioData>>

    suspend fun loadAudioFiles()

    suspend fun copyAssets()
}
