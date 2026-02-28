package io.github.vinnih.kipty.data.service.record

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Singleton
class RecorderService @Inject constructor(@ApplicationContext private val context: Context) {
    private var timerJob: Job? = null
    private var amplitudesJob: Job? = null
    private var mediaRecorder: MediaRecorder? = null

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _recordTime = MutableStateFlow(0L)
    val recordTime: StateFlow<Long> = _recordTime.asStateFlow()

    private val _amplitudes = MutableStateFlow<List<Float>>(emptyList())
    val amplitudes: StateFlow<List<Float>> = _amplitudes.asStateFlow()

    fun startRecord(outputFile: File) {
        mediaRecorder = generateMediaRecorder(outputFile)

        mediaRecorder?.start()

        _isRecording.value = true
        timerJob = recordTime { _recordTime.value = it }
        amplitudesJob = listenAmplitudes {
            _amplitudes.value = (_amplitudes.value + it).takeLast(100)
        }
    }

    fun stopRecord() {
        timerJob?.cancel()
        amplitudesJob?.cancel()
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        _isRecording.value = false
        _recordTime.value = 0L
        _amplitudes.value = emptyList()
    }

    private fun generateMediaRecorder(outputFile: File): MediaRecorder {
        val mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            (MediaRecorder())
        }.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile.absolutePath)
            prepare()
        }

        return mediaRecorder
    }

    private fun listenAmplitudes(amplitudes: (Float) -> Unit): Job =
        CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                mediaRecorder?.let { recorder ->
                    amplitudes(recorder.maxAmplitude / 32767f)
                }
                delay(50)
            }
        }

    private fun recordTime(time: (Long) -> Unit): Job = CoroutineScope(Dispatchers.Default).launch {
        while (isActive) {
            time(_recordTime.value + 1L)
            delay(1000)
        }
    }
}
