package io.github.vinnih.kipty.ui.home

import io.github.vinnih.androidtranscoder.extractor.WavReader
import io.github.vinnih.kipty.data.transcription.AudioData
import java.io.File
import kotlinx.coroutines.flow.StateFlow

interface HomeController {
    val value: StateFlow<List<AudioData>>

    suspend fun createAudio(audioData: AudioData, reader: WavReader)

    suspend fun updateAudioFiles()

    suspend fun copyAssets(): List<File>
}
