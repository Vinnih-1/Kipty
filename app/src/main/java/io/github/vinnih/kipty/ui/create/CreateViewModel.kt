package io.github.vinnih.kipty.ui.create

import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.vinnih.kipty.domain.usecase.audio.CreateAudioUseCase
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CreateUiState(val step: Step = Step.FILE, val data: Data = Data()) {
    data class Data(
        val audioUri: Uri? = null,
        val title: String = "",
        val description: String = "",
        val imageFile: File? = null
    )
}

enum class Step {
    FILE,
    DETAILS,
    IMAGE,
    REVIEW
}

@HiltViewModel
class CreateViewModel @Inject constructor(
    private val createAudioUseCase: CreateAudioUseCase
) : ViewModel(),
    CreateController {

    private val _uiState = MutableStateFlow(CreateUiState())

    override val uiState: StateFlow<CreateUiState> = _uiState.asStateFlow()

    override fun nextStep() {
        _uiState.update { currentState ->
            val nextStep = (currentState.step.ordinal + 1).coerceAtMost(Step.entries.size - 1)
            currentState.copy(step = Step.entries[nextStep])
        }
    }

    override fun previousStep() {
        _uiState.update { currentState ->
            val previousStep = (currentState.step.ordinal - 1).coerceAtLeast(0)
            currentState.copy(step = Step.entries[previousStep])
        }
    }

    override fun selectAudio(file: Uri?) {
        _uiState.update { it.copy(data = it.data.copy(audioUri = file)) }
    }

    override fun selectImage(file: File?) {
        _uiState.update { it.copy(data = it.data.copy(imageFile = file)) }
    }

    override fun insertTitle(title: String) {
        _uiState.update { it.copy(data = it.data.copy(title = title)) }
    }

    override fun insertDescription(description: String) {
        _uiState.update { it.copy(data = it.data.copy(description = description)) }
    }

    override fun createAudio() {
        val data = _uiState.value.data
        val audioUri = data.audioUri ?: return
        createAudioUseCase(audioUri, data.title, data.description, data.imageFile)
    }

    override fun clearUiState() {
        _uiState.update { CreateUiState() }
    }
}
