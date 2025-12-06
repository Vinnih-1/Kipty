package io.github.vinnih.kipty.ui.home

import io.github.vinnih.androidtranscoder.extractor.WavReader
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import java.io.File
import kotlinx.coroutines.flow.StateFlow

interface HomeController {
    val value: StateFlow<List<AudioEntity>>

    suspend fun createAudio(reader: WavReader): AudioEntity

    suspend fun updateAudioFiles()

    suspend fun copyAssets(): List<File>
}
