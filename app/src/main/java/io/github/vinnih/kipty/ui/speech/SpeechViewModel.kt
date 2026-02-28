package io.github.vinnih.kipty.ui.speech

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.vinnih.kipty.data.database.entity.AudioTranscription
import io.github.vinnih.kipty.data.database.entity.SpeechEntity
import io.github.vinnih.kipty.data.service.audio.OutputFormat
import io.github.vinnih.kipty.data.service.record.RecorderService
import io.github.vinnih.kipty.domain.usecase.audio.ResampleAudioUseCase
import io.github.vinnih.kipty.domain.usecase.player.PauseAudioUseCase
import io.github.vinnih.kipty.domain.usecase.player.TempPlayerPlayUseCase
import io.github.vinnih.kipty.domain.usecase.player.TempPlayerStopUseCase
import io.github.vinnih.kipty.domain.usecase.record.CalculatePronunciationScoreUseCase
import io.github.vinnih.kipty.domain.usecase.record.GetRecordByIdUseCase
import io.github.vinnih.kipty.domain.usecase.record.SaveRecordUseCase
import io.github.vinnih.kipty.domain.usecase.record.StartRecordUseCase
import io.github.vinnih.kipty.domain.usecase.record.StopRecordUseCase
import io.github.vinnih.kipty.domain.usecase.record.TransferAudioUseCase
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class SpeechUiState(
    val isRecording: Boolean = false,
    val amplitudes: List<Float> = emptyList(),
    val recordingTime: Long = 0L,
    val result: Pair<String, Int> = Pair("", 0)
)

@HiltViewModel
class SpeechViewModel @Inject constructor(
    recorderService: RecorderService,
    private val startRecordUseCase: StartRecordUseCase,
    private val stopRecordUseCase: StopRecordUseCase,
    private val pauseAudioUseCase: PauseAudioUseCase,
    private val saveSpeechUseCase: SaveRecordUseCase,
    private val resampleAudioUseCase: ResampleAudioUseCase,
    private val transferAudioUseCase: TransferAudioUseCase,
    private val getRecordByIdUseCase: GetRecordByIdUseCase,
    private val tempPlayerPlayUseCase: TempPlayerPlayUseCase,
    private val tempPlayerStopUseCase: TempPlayerStopUseCase,
    private val calculateScoreUseCase: CalculatePronunciationScoreUseCase
) : ViewModel(),
    SpeechController {

    private val recordPath = MutableStateFlow("")
    private val audioPath = MutableStateFlow("")
    private val result = MutableStateFlow(Pair("", 0))

    override val uiState: StateFlow<SpeechUiState> = combine(
        recorderService.isRecording,
        recorderService.amplitudes,
        recorderService.recordTime,
        result
    ) { recording, amps, time, result ->
        SpeechUiState(
            isRecording = recording,
            amplitudes = amps,
            recordingTime = time,
            result = result
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SpeechUiState())

    suspend fun getById(id: Int): SpeechEntity? = getRecordByIdUseCase(id)

    fun startRecord(audioPath: String) {
        pauseAudioUseCase()
        startRecordUseCase { output ->
            recordPath.value = output.absolutePath
            this.audioPath.value = audioPath
        }
    }

    fun stopRecord() = stopRecordUseCase()

    fun abortRecording() {
        val recordPath = recordPath.value

        if (recordPath.isNotEmpty()) {
            File(recordPath).delete()
        }
        stopRecordUseCase()

        clearAll()
    }

    suspend fun pronunciationScore(phrase: AudioTranscription): Long {
        val resampledFile = resampleAudioUseCase(
            file = File(recordPath.value),
            format = OutputFormat.WAV
        )

        calculateScoreUseCase(
            phrase = phrase,
            byteArray = resampledFile.readBytes(),
            onSuccess = { result.value = it }
        )
        resampledFile.delete()

        transferAudioUseCase(
            recordPath = recordPath.value,
            path = "speeches${File.separator}${audioPath.value.substringBeforeLast("/")}",
            outputFile = {
                recordPath.value = it.absolutePath
            }
        )

        return saveSpeechUseCase(
            audioPath = audioPath.value,
            recordPath = recordPath.value,
            result = result.value.first,
            phrase = phrase
        )
    }
    fun clearAll() {
        audioPath.value = ""
        recordPath.value = ""
        result.value = Pair("", 0)
    }

    fun playTempAudio(audioFilePath: String) = tempPlayerPlayUseCase(audioFilePath)

    fun stopTempAudio() = tempPlayerStopUseCase()
}
