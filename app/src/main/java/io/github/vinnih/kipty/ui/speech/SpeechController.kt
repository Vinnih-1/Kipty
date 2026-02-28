package io.github.vinnih.kipty.ui.speech

import kotlinx.coroutines.flow.StateFlow

interface SpeechController {

    val uiState: StateFlow<SpeechUiState>
}
