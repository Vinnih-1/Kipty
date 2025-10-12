package io.github.vinnih.kipty.ui.home

import io.github.vinnih.kipty.data.local.entity.Transcription
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow

interface HomeUiController {
    val uiState: StateFlow<List<Transcription>>

    fun createTranscription(transcription: Transcription): Job

    fun deleteTranscription(transcription: Transcription): Job
}