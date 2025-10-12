package io.github.vinnih.kipty.ui.home

import io.github.vinnih.kipty.data.local.entity.Transcription
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeHomeViewModel : HomeUiController {

    override val uiState: StateFlow<List<Transcription>> = MutableStateFlow(emptyList())

    override fun createTranscription(transcription: Transcription): Job {
        TODO("Not yet implemented")
    }

    override fun deleteTranscription(transcription: Transcription): Job {
        TODO("Not yet implemented")
    }
}