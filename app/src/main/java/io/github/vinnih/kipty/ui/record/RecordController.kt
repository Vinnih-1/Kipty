package io.github.vinnih.kipty.ui.record

import io.github.vinnih.kipty.data.database.entity.AudioTranscription
import io.github.vinnih.kipty.data.database.entity.SpeechEntity
import kotlinx.coroutines.flow.StateFlow

interface RecordController {

    val uiState: StateFlow<RecordUiState>

    suspend fun getById(id: Int): SpeechEntity?

    fun toggleRecording(audioPath: String)

    fun abortRecording()

    suspend fun calculatePronunciationScore(phrase: AudioTranscription): Long
}
