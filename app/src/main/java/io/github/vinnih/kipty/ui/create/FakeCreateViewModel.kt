package io.github.vinnih.kipty.ui.create

import android.net.Uri
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeCreateViewModel : CreateController {
    override val uiState: StateFlow<CreateUiState> = MutableStateFlow(CreateUiState())

    override fun nextStep() {
        TODO("Not yet implemented")
    }

    override fun previousStep() {
        TODO("Not yet implemented")
    }

    override fun selectAudio(file: Uri?) {
        TODO("Not yet implemented")
    }

    override fun selectImage(file: File?) {
        TODO("Not yet implemented")
    }

    override fun insertTitle(title: String) {
        TODO("Not yet implemented")
    }

    override fun insertDescription(description: String) {
        TODO("Not yet implemented")
    }

    override fun createAudio() {
        TODO("Not yet implemented")
    }

    override fun clearUiState() {
        TODO("Not yet implemented")
    }
}
