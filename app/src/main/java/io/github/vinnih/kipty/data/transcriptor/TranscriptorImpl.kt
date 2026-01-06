package io.github.vinnih.kipty.data.transcriptor

import android.content.Context
import android.util.Log
import com.whispercpp.whisper.WhisperContext
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.vinnih.androidtranscoder.utils.toWavReader
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.AudioTranscription
import io.github.vinnih.kipty.utils.convertTranscription
import io.github.vinnih.kipty.utils.createFolder
import io.github.vinnih.kipty.utils.processAudioSegments
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TranscriptorImpl @Inject constructor(@ApplicationContext private val context: Context) :
    Transcriptor {

    override var whisperContext: WhisperContext

    init {
        copyModel()
        whisperContext = loadModel()
    }

    override fun copyModel(): File {
        val modelsPath = File(context.filesDir, "models").createFolder()
        val model = context.assets.list("models/")?.firstOrNull()!!

        context.assets.open("models/$model").use { inputStream ->
            File(modelsPath, model).outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        return File(modelsPath, model)
    }

    override fun loadModel(): WhisperContext {
        val modelsPath = File(context.filesDir, "models")
        val model = modelsPath.listFiles()!!.first()

        return WhisperContext.createContextFromFile(model.absolutePath)
    }

    override suspend fun transcribe(
        audioEntity: AudioEntity,
        onProgress: (Int) -> Unit
    ): AudioEntity {
        val audio = File(audioEntity.audioPath)
        val reader = audio.toWavReader(context.cacheDir)
        val transcriptions = mutableListOf<AudioTranscription>()

        reader.data.processAudioSegments(context, onSegmentProcessed = {
                segmentNumber,
                floatArray,
                progress
            ->
            Log.d("TranscriptorImpl", "Transcribing segment $segmentNumber")
            onProgress(progress)

            val transcription = whisperContext.transcribeData(floatArray).convertTranscription()

            if (transcriptions.isEmpty()) {
                transcriptions.addAll(transcription)
            } else {
                transcription.forEach {
                    val lastTranscription = transcriptions.last()
                    val newTranscription = it.copy(
                        start = lastTranscription.end,
                        end = lastTranscription.end + it.end - it.start
                    )
                    transcriptions.add(newTranscription)
                }
            }
        })
        reader.dispose()

        return audioEntity.copy(transcription = transcriptions)
    }
}
