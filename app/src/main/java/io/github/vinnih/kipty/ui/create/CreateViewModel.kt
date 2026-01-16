package io.github.vinnih.kipty.ui.create

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.repository.audio.AudioRepository
import io.github.vinnih.kipty.utils.createFolder
import io.github.vinnih.kipty.utils.moveTo
import java.io.File
import java.time.LocalDateTime
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CreateUiState(val step: Step = Step.FILE, val data: Data = Data()) {
    data class Data(
        val audioFile: File? = null,
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
    @ApplicationContext private val context: Context,
    private val audioRepository: AudioRepository
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

    override fun selectAudio(file: File?) {
        _uiState.update { currentState ->
            currentState.copy(data = currentState.data.copy(audioFile = file))
        }
    }

    override fun selectImage(file: File?) {
        _uiState.update { currentState ->
            currentState.copy(data = currentState.data.copy(imageFile = file))
        }
    }

    override fun insertTitle(title: String) {
        _uiState.update { currentState ->
            currentState.copy(data = currentState.data.copy(title = title))
        }
    }

    override fun insertDescription(description: String) {
        _uiState.update { currentState ->
            currentState.copy(data = currentState.data.copy(description = description))
        }
    }

    override fun createAudio(onSuccess: () -> Unit) {
        val name = _uiState.value.data.title
        val audio = _uiState.value.data.audioFile!!.absolutePath
        val description = _uiState.value.data.description
        val image = if (_uiState.value.data.imageFile != null) {
            _uiState.value.data.imageFile!!.absolutePath
        } else {
            File(context.filesDir, "default-icon.png").absolutePath
        }

        val path = File(
            context.filesDir,
            "transcriptions" + File.separatorChar +
                audio.substringAfterLast("/").substringBeforeLast(".")
        ).createFolder()
        val audioFile = File(path, audio.substringAfterLast("/"))
        val imageFile = File(path, image.substringAfterLast("/"))

        File(audio).moveTo(audioFile)
        File(image).copyTo(imageFile, overwrite = true)

        val entity = AudioEntity(
            name = name.ifEmpty { audioFile.nameWithoutExtension },
            description = description.ifEmpty { null },
            audioPath = audioFile.absolutePath,
            imagePath = imageFile.absolutePath,
            isDefault = false,
            createdAt = LocalDateTime.now().toString()
        )

        viewModelScope.launch {
            audioRepository.save(entity)
            onSuccess.invoke()
        }
    }

    override fun clearUiState() {
        _uiState.update {
            CreateUiState()
        }
    }
}
