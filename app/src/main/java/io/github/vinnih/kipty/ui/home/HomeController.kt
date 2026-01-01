package io.github.vinnih.kipty.ui.home

import io.github.vinnih.kipty.data.database.entity.AudioEntity
import java.io.File

interface HomeController {

    fun openNotificationSettings()

    suspend fun createAudio(
        audio: String,
        image: String,
        name: String,
        description: String? = null,
        isDefault: Boolean = false
    ): AudioEntity

    suspend fun saveAudio(audioEntity: AudioEntity): Long

    suspend fun createDefault(data: suspend (String, String, String, String) -> Unit)
}
