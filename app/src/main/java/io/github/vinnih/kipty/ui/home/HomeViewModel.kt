package io.github.vinnih.kipty.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.vinnih.kipty.data.local.entity.Transcription
import io.github.vinnih.kipty.data.local.repository.TranscriptionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val transcriptionRepository: TranscriptionRepository,
    @ApplicationContext val context: Context
) : ViewModel(), HomeUiController {

    override val uiState = transcriptionRepository.getAllTranscriptions()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    override fun createTranscription(transcription: Transcription) = viewModelScope.launch {
        transcriptionRepository.insertTranscription(transcription)
    }

    override fun deleteTranscription(transcription: Transcription) = viewModelScope.launch {
        transcriptionRepository.deleteTranscription(transcription)
    }
}