package io.github.vinnih.kipty.ui.home

import io.github.vinnih.kipty.data.database.entity.AudioEntity
import java.io.File

interface HomeController {
    suspend fun createAudio(
        file: File,
        name: String? = null,
        description: String? = null
    ): AudioEntity

    suspend fun saveAudio(audioEntity: AudioEntity): Long

    suspend fun copySamples(): List<Pair<File, File>>
}
