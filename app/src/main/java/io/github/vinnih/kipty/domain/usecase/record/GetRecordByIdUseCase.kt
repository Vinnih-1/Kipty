package io.github.vinnih.kipty.domain.usecase.record

import io.github.vinnih.kipty.data.database.entity.SpeechEntity
import io.github.vinnih.kipty.domain.repository.SpeechRepository
import javax.inject.Inject

class GetRecordByIdUseCase @Inject constructor(private val speechRepository: SpeechRepository) {

    suspend operator fun invoke(id: Int): SpeechEntity? = speechRepository.getById(id)
}
