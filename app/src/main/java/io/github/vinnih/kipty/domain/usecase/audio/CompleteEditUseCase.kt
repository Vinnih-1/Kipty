package io.github.vinnih.kipty.domain.usecase.audio

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.domain.repository.AudioRepository
import jakarta.inject.Inject
import java.io.File

class CompleteEditUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: AudioRepository
) {
    suspend operator fun invoke(
        audioEntity: AudioEntity,
        title: String,
        description: String,
        imageFile: File?
    ) {
        val image = imageFile ?: File(context.filesDir, "default-icon.png")
        val transcriptionFolder = File(audioEntity.audioPath).parentFile!!
        val destinationImage = File(transcriptionFolder, image.name)

        val newImagePath = if (image.absolutePath != destinationImage.absolutePath) {
            image.copyTo(destinationImage, overwrite = true)
            destinationImage.absolutePath
        } else {
            image.absolutePath
        }

        repository.save(
            audioEntity.copy(
                name = title,
                description = description,
                imagePath = newImagePath
            )
        )
    }
}
