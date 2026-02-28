package io.github.vinnih.kipty.domain.usecase.record

import io.github.vinnih.kipty.data.database.entity.AudioTranscription
import io.github.vinnih.kipty.data.service.record.SpeechResult
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CalculatePronunciationScoreUseCase @Inject constructor(
    private val speechResult: SpeechResult
) {

    suspend operator fun invoke(
        phrase: AudioTranscription,
        byteArray: ByteArray,
        onSuccess: (Pair<String, Int>) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            val result = speechResult.calculatePronunciationScore(
                expected = phrase.text,
                byteArray = byteArray
            )
            onSuccess(result)
        }
    }
}
