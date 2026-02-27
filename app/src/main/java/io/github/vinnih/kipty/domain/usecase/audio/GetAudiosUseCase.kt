package io.github.vinnih.kipty.domain.usecase.audio

import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.domain.repository.AudioRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetAudiosUseCase @Inject constructor(private val audioRepository: AudioRepository) {
    operator fun invoke(): Flow<List<AudioEntity>> = audioRepository.getAllFlow()
}
