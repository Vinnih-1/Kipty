package io.github.vinnih.kipty.ui.create

import android.net.Uri
import java.io.File
import kotlinx.coroutines.flow.StateFlow

interface CreateController {

    val uiState: StateFlow<CreateUiState>

    fun nextStep()

    fun previousStep()

    fun selectAudio(file: Uri?)

    fun selectImage(file: File?)

    fun insertTitle(title: String)

    fun insertDescription(description: String)

    fun createAudio()

    fun clearUiState()
}
