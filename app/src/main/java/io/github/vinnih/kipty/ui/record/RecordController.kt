package io.github.vinnih.kipty.ui.record

import io.github.vinnih.kipty.data.database.entity.AudioTranscription
import kotlinx.coroutines.flow.StateFlow

interface RecordController {

    val uiState: StateFlow<RecordUiState>

    fun toggleRecording(audioPath: String)

    fun abortRecording()

    suspend fun calculatePronunciationScore(phrase: AudioTranscription): Unit
}
