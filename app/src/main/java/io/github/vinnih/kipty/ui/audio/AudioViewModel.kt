package io.github.vinnih.kipty.ui.audio

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.repository.AudioRepository
import io.github.vinnih.kipty.data.workers.TranscriptionWorker
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class AudioViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: AudioRepository
) : ViewModel(),
    AudioController {

    private val _uiState = MutableStateFlow(AudioUiState())
    override val uiState: StateFlow<AudioUiState> = _uiState.asStateFlow()

    override fun transcribeAudio(audioEntity: AudioEntity, onSuccess: (AudioEntity) -> Unit) {
        val request = OneTimeWorkRequestBuilder<TranscriptionWorker>()
            .setInputData(Data.Builder().putInt("AUDIO_ID", audioEntity.uid).build())
            .build()

        WorkManager.getInstance(context).enqueue(request)
    }

    override fun saveTranscription(audioEntity: AudioEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.save(audioEntity)
        }
    }

    override suspend fun getById(id: Int): AudioEntity = withContext(Dispatchers.IO) {
        return@withContext repository.getById(id)!!
    }
}

data class AudioUiState(
    val isTranscribing: Boolean = false,
    val transcribing: AudioEntity? = null,
    val queue: List<AudioEntity> = emptyList()
)
