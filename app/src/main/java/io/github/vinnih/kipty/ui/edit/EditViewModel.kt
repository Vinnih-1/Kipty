package io.github.vinnih.kipty.ui.edit

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.repository.audio.AudioRepository
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
    @ApplicationContext private val context: Context,
    private val audioRepository: AudioRepository
) : ViewModel(),
    EditController {

    private val _uiState = MutableStateFlow(EditUiState())

    override val uiState: StateFlow<EditUiState> = _uiState.asStateFlow()

    override fun retrieveData(id: Int) {
        viewModelScope.launch {
            val audioEntity = audioRepository.getById(id).firstOrNull() ?: return@launch

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
        _uiState.update { currentState ->
            currentState.copy(title = title)
        }
    }

    override fun editDescription(description: String) {
        _uiState.update { currentState ->
            currentState.copy(description = description)
        }
    }

    override fun editImage(image: File?) {
        _uiState.update { currentState ->
            println(image?.absolutePath)
            currentState.copy(imageFile = image)
        }
    }

    override fun completeEdit(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val audioEntity = _uiState.value.audioEntity ?: return@launch
            val imageFile = _uiState.value.imageFile ?: File(context.filesDir, "default-icon.png")
            val transcriptionFolder = File(audioEntity.audioPath).parentFile!!
            val destinationImage = File(transcriptionFolder, imageFile.name)

            val newImagePath = if (imageFile.absolutePath != destinationImage.absolutePath) {
                imageFile.copyTo(destinationImage, overwrite = true)
                destinationImage.absolutePath
            } else {
                imageFile.absolutePath
            }

            audioRepository.save(
                _uiState.value.audioEntity!!.copy(
                    name = _uiState.value.title,
                    description = _uiState.value.description,
                    imagePath = newImagePath
                )
            )
            onSuccess.invoke()
        }
    }

    override fun clearUiState() {
        _uiState.update {
            EditUiState()
        }
    }
}
