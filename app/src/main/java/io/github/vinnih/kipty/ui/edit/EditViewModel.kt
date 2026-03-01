package io.github.vinnih.kipty.ui.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.domain.usecase.audio.CompleteEditUseCase
import io.github.vinnih.kipty.domain.usecase.audio.GetAudioFlowByIdUseCase
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditUiState(
    val title: String = "",
    val description: String = "",
    val imageFile: File? = null,
    val audioEntity: AudioEntity? = null
)

@HiltViewModel
class EditViewModel @Inject constructor(
    private val getAudioFlowByIdUseCase: GetAudioFlowByIdUseCase,
    private val completeEditUseCase: CompleteEditUseCase
) : ViewModel(),
    EditController {

    private val _uiState = MutableStateFlow(EditUiState())

    override val uiState: StateFlow<EditUiState> = _uiState.asStateFlow()

    override fun retrieveData(id: Int) {
        viewModelScope.launch {
            val audioEntity = getAudioFlowByIdUseCase(id).firstOrNull() ?: return@launch
            _uiState.update { currentState ->
                currentState.copy(
                    title = audioEntity.name,
                    description = audioEntity.description ?: "",
                    imageFile = File(audioEntity.imagePath),
                    audioEntity = audioEntity
                )
            }
        }
    }

    override fun editTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    override fun editDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    override fun editImage(image: File?) {
        _uiState.update { it.copy(imageFile = image) }
    }

    override fun completeEdit(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val state = _uiState.value
            val audioEntity = state.audioEntity ?: return@launch
            completeEditUseCase(audioEntity, state.title, state.description, state.imageFile)
            onSuccess.invoke()
        }
    }

    override fun clearUiState() {
        _uiState.update { EditUiState() }
    }
}
