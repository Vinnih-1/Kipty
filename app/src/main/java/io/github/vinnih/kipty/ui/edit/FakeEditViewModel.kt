package io.github.vinnih.kipty.ui.edit

import java.io.File
import kotlinx.coroutines.flow.StateFlow

class FakeEditViewModel : EditController {
    override val uiState: StateFlow<EditUiState>
        get() = TODO("Not yet implemented")

    override fun retrieveData(id: Int) {
        TODO("Not yet implemented")
    }

    override fun editTitle(title: String) {
        TODO("Not yet implemented")
    }

    override fun editDescription(description: String) {
        TODO("Not yet implemented")
    }

    override fun editImage(image: File?) {
        TODO("Not yet implemented")
    }

    override fun completeEdit(onSuccess: () -> Unit) {
        TODO("Not yet implemented")
    }

    override fun clearUiState() {
        TODO("Not yet implemented")
    }
}
