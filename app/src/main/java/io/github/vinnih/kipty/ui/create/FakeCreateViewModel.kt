package io.github.vinnih.kipty.ui.create

import android.net.Uri
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeCreateViewModel(createUiState: CreateUiState = CreateUiState()) : CreateController {
    override val uiState: StateFlow<CreateUiState> = MutableStateFlow(createUiState)

    override fun nextStep() {}

    override fun previousStep() {}

    override fun selectAudio(file: Uri?) {}

    override fun selectImage(file: File?) {}

    override fun insertTitle(title: String) {}

    override fun insertDescription(description: String) {}

    override fun createAudio() {}

    override fun clearUiState() {}
}
