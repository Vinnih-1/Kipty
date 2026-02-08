package io.github.vinnih.kipty.data.service.transcriptor

import android.content.Context
import com.whispercpp.whisper.WhisperContext
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.AudioTranscription
import io.github.vinnih.kipty.utils.convertTranscription
import io.github.vinnih.kipty.utils.processAudioSegments
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
class TranscriptorService @Inject constructor(@ApplicationContext private val context: Context) {

    private var _whisperContext: WhisperContext? = null

    val whisperContext: WhisperContext
        get() = _whisperContext
            ?: throw IllegalStateException(
                "WhisperContext not initialized. Call initialize() first."
            )

    suspend fun initialize() {
        if (_whisperContext == null) {
            withContext(Dispatchers.IO) {
                _whisperContext = loadModel()
            }
        }
    }

    fun loadModel(): WhisperContext {
        val model = context.assets.list("models/")?.first()

        return WhisperContext.createContextFromAsset(
            context.assets,
            "models" + File.separator + model
        )
    }

    suspend fun transcribe(
        audioEntity: AudioEntity,
        numThreads: Int,
        onProgress: (Int) -> Unit
    ): AudioEntity {
        val audio = File(audioEntity.audioPath)
        val transcriptions = mutableListOf<AudioTranscription>()

        audio.processAudioSegments(
            context,
            onSegmentProcessed = { floatArray, progress, startTimeSeconds ->
                onProgress(progress)

                val segmentTranscription = whisperContext.transcribeData(
                    data = floatArray,
                    numThreads = numThreads
                ).convertTranscription()

                val adjustedTranscription = segmentTranscription.map { transcript ->
                    transcript.copy(
                        start = transcript.start + startTimeSeconds,
                        end = transcript.end + startTimeSeconds
                    )
                }

                transcriptions.addAll(adjustedTranscription)
            }
        )

        return audioEntity.copy(transcription = transcriptions)
    }

    suspend fun transcribe(floatArray: FloatArray): String = whisperContext.transcribeData(
        data = floatArray,
        numThreads = 8
    ).convertTranscription().joinToString(separator = " ") { it.text }
}
