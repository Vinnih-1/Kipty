package io.github.vinnih.kipty.domain.usecase.audio

import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.domain.repository.AudioRepository
import jakarta.inject.Inject
import java.io.File

class DeleteAudioUseCase @Inject constructor(private val repository: AudioRepository) {
    suspend operator fun invoke(audioEntity: AudioEntity) {
        repository.delete(audioEntity)
        if (!audioEntity.isDefault) {
            File(audioEntity.audioPath).parentFile?.deleteRecursively()
        }
    }
}
