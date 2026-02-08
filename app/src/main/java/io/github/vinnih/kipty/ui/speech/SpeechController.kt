package io.github.vinnih.kipty.ui.speech

import io.github.vinnih.kipty.data.database.entity.AudioTranscription
import io.github.vinnih.kipty.data.database.entity.SpeechEntity
import kotlinx.coroutines.flow.StateFlow

interface SpeechController {

    val uiState: StateFlow<SpeechUiState>

    suspend fun getById(id: Int): SpeechEntity?

    fun toggleRecording(audioPath: String)

    fun abortRecording()

    suspend fun calculatePronunciationScore(phrase: AudioTranscription): Long

    fun clearAll()

    fun playTempAudio(audioFilePath: String)

    fun stopTempAudio()
}
