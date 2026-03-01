package io.github.vinnih.kipty.domain.usecase.audio

import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.domain.repository.AudioRepository
import jakarta.inject.Inject

class SaveAudioUseCase @Inject constructor(private val repository: AudioRepository) {
    suspend operator fun invoke(audioEntity: AudioEntity): Long = repository.save(audioEntity)
}
