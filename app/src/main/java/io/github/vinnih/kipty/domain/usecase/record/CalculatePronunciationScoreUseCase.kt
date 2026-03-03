package io.github.vinnih.kipty.domain.usecase.record

import io.github.vinnih.kipty.data.database.entity.AudioTranscription
import io.github.vinnih.kipty.data.service.record.SpeechResult
import java.io.File
import javax.inject.Inject

class CalculatePronunciationScoreUseCase @Inject constructor(
    private val speechResult: SpeechResult
) {

    suspend operator fun invoke(
        phrase: AudioTranscription,
        audioFile: File,
        onSuccess: (Pair<String, Int>) -> Unit
    ) {
        speechResult.calculatePronunciationScore(
            expected = phrase.text,
            audioFile = audioFile,
            onScore = onSuccess
        )
    }
}
