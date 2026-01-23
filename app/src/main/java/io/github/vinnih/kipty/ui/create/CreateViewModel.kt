package io.github.vinnih.kipty.ui.create

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.repository.audio.AudioRepository
import io.github.vinnih.kipty.data.workers.AudioWorker
import io.github.vinnih.kipty.utils.createFolder
import io.github.vinnih.kipty.utils.getFileName
import java.io.File
import java.time.LocalDateTime
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

    override fun selectAudio(file: Uri?) {
        _uiState.update { currentState ->
            currentState.copy(data = currentState.data.copy(audioUri = file))
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

    override fun createAudio() {
        viewModelScope.launch(Dispatchers.IO) {
            validateAudioName()

            val baseName = _uiState.value.data.title
            val uniqueName = getUniqueAudioName(baseName)
            val description = _uiState.value.data.description
            val image = if (_uiState.value.data.imageFile != null) {
                _uiState.value.data.imageFile!!.absolutePath
            } else {
                File(context.filesDir, "default-icon.png").absolutePath
            }
            val path = File(
                context.filesDir,
                "transcriptions" + File.separatorChar + uniqueName
            ).createFolder()
            val imageFile = File(path, image.substringAfterLast("/"))

            File(image).copyTo(imageFile, overwrite = true)

            val entity = AudioEntity(
                name = uniqueName,
                description = description.ifEmpty { null },
                audioPath = "",
                imagePath = imageFile.absolutePath,
                isDefault = false,
                createdAt = LocalDateTime.now().toString(),
                duration = 0,
                audioSize = 0
            )
            val uid = audioRepository.save(entity)

            processAudio(uid.toInt(), path)
        }
    }

    private fun processAudio(uid: Int, folder: File) {
        val data = Data.Builder()
            .putInt("uid", uid)
            .putString("folderPath", folder.absolutePath)
            .putString("audioUri", _uiState.value.data.audioUri.toString())
            .build()
        val request = OneTimeWorkRequestBuilder<AudioWorker>()
            .addTag(AudioWorker.TAG)
            .setInputData(data)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "process_new_audio",
                androidx.work.ExistingWorkPolicy.KEEP,
                request
            )
    }

    private fun getUniqueAudioName(baseName: String): String {
        val transcriptionsDir = File(context.filesDir, "transcriptions")

        if (!transcriptionsDir.exists() || !File(transcriptionsDir, baseName).exists()) {
            return baseName
        }

        var counter = 1
        var uniqueName: String

        do {
            uniqueName = "${baseName}_$counter"
            counter++
        } while (File(transcriptionsDir, uniqueName).exists())

        return uniqueName
    }

    private fun validateAudioName() {
        _uiState.value.data.title.ifEmpty {
            _uiState.update { currentState ->
                currentState.copy(
                    data = currentState.data.copy(
                        title = currentState.data.audioUri!!.getFileName(
                            context
                        ).substringBeforeLast(".")
                    )
                )
            }
        }
    }

    override fun clearUiState() {
        _uiState.update {
            CreateUiState()
        }
    }
}
