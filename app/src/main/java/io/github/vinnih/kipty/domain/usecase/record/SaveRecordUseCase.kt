package io.github.vinnih.kipty.domain.usecase.record

import io.github.vinnih.kipty.data.database.entity.AudioTranscription
import io.github.vinnih.kipty.data.database.entity.SpeechEntity
import io.github.vinnih.kipty.domain.repository.SpeechRepository
import java.time.LocalDateTime
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SaveRecordUseCase @Inject constructor(private val speechRepository: SpeechRepository) {

    suspend operator fun invoke(
        audioPath: String,
        recordPath: String,
        result: String,
        phrase: AudioTranscription
    ): Long = withContext(Dispatchers.IO) {
        val speechEntity = SpeechEntity(
            audioPath = audioPath,
            speechPath = recordPath,
            phrase = phrase,
            result = result,
            createdAt = LocalDateTime.now().toString()
        )

        return@withContext speechRepository.save(speechEntity)
    }
}
