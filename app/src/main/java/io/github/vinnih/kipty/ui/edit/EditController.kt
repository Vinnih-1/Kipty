package io.github.vinnih.kipty.ui.edit

import java.io.File
import kotlinx.coroutines.flow.StateFlow

interface EditController {

    val uiState: StateFlow<EditUiState>

    fun retrieveData(id: Int)

    fun editTitle(title: String)

    fun editDescription(description: String)

    fun editImage(image: File?)

    fun completeEdit(onSuccess: () -> Unit)

    fun clearUiState()
}
