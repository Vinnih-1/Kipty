package io.github.vinnih.kipty.ui.home

import io.github.vinnih.kipty.data.database.entity.AudioEntity
import java.io.File

class FakeHomeViewModel : HomeController {
    override fun openNotificationSettings() {
        TODO("Not yet implemented")
    }

    override suspend fun createAudio(
        audio: File,
        image: File?,
        name: String?,
        description: String?
    ): AudioEntity {
        TODO("Not yet implemented")
    }

    override suspend fun saveAudio(audioEntity: AudioEntity): Long {
        TODO("Not yet implemented")
    }

    override suspend fun createDefault(data: suspend (File, File, File, File) -> Unit) {
        TODO("Not yet implemented")
    }
}
