package io.github.vinnih.kipty.ui.home

import io.github.vinnih.kipty.data.database.entity.AudioEntity
import java.io.File

class FakeHomeViewModel : HomeController {
    override fun openNotificationSettings() {
        TODO("Not yet implemented")
    }

    override suspend fun createAudio(
        audio: String,
        image: String,
        name: String,
        description: String?,
        isDefault: Boolean
    ): AudioEntity {
        TODO("Not yet implemented")
    }

    override suspend fun saveAudio(audioEntity: AudioEntity): Long {
        TODO("Not yet implemented")
    }

    override suspend fun createDefault(data: suspend (String, String, String, String) -> Unit) {
        TODO("Not yet implemented")
    }
}
