package io.github.vinnih.kipty.domain.usecase.audio

import io.github.vinnih.kipty.domain.repository.AudioRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetPlayTimeUseCase @Inject constructor(private val audioRepository: AudioRepository) {
    operator fun invoke(id: Int): Flow<Long> = audioRepository.getFlowPlayTimeById(id)
}
