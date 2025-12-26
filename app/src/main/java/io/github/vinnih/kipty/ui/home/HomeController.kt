package io.github.vinnih.kipty.ui.home

import io.github.vinnih.kipty.data.database.entity.AudioEntity
import java.io.File

interface HomeController {

    fun openNotificationSettings()

    suspend fun createAudio(
        audio: File,
        image: File? = null,
        name: String? = null,
        description: String? = null
    ): AudioEntity

    suspend fun saveAudio(audioEntity: AudioEntity): Long

    suspend fun createDefault(data: suspend (File, File, File, File) -> Unit)
}
