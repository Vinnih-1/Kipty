package io.github.vinnih.kipty.domain.usecase.audio

import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.domain.repository.AudioRepository
import jakarta.inject.Inject

class GetAudioByIdUseCase @Inject constructor(private val repository: AudioRepository) {
    suspend operator fun invoke(id: Int): AudioEntity? = repository.getById(id)
}
