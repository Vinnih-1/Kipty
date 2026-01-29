package io.github.vinnih.kipty.data.service.recording

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshotFlow
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Singleton
class AudioRecorder @Inject constructor(@ApplicationContext private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private val amplitudes = mutableStateListOf<Float>()
    private var amplitudeJob: Job? = null

    val amplitudeFlow: StateFlow<List<Float>> =
        snapshotFlow { amplitudes.toList() }
            .stateIn(
                scope = CoroutineScope(Dispatchers.Default),
                started = SharingStarted.WhileSubscribed(),
                initialValue = emptyList()
            )

    fun startRecording(outputFile: File) {
        mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
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
            start()
        }

        // Coletar amplitudes em tempo real
        amplitudeJob = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                mediaRecorder?.let { recorder ->
                    try {
                        // maxAmplitude retorna 0-32767
                        val amplitude = recorder.maxAmplitude / 32767f
                        amplitudes.add(amplitude)

                        // Manter apenas as últimas 100 amplitudes
                        if (amplitudes.size > 100) {
                            amplitudes.removeAt(0)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                delay(50) // Atualizar a cada 50ms
            }
        }
    }

    fun stopRecording() {
        amplitudeJob?.cancel()
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        amplitudes.clear()
    }
}
