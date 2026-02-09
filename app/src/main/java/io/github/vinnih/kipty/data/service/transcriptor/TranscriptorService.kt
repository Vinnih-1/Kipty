package io.github.vinnih.kipty.data.service.transcriptor

import android.content.Context
import com.whispercpp.whisper.WhisperContext
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import io.github.vinnih.kipty.data.database.entity.AudioTranscription
import io.github.vinnih.kipty.data.service.AudioResampler
import io.github.vinnih.kipty.data.service.AudioResampler.resample
import io.github.vinnih.kipty.utils.convertTranscription
import io.github.vinnih.kipty.utils.moveTo
import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
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

    suspend fun File.processAudioSegments(
        context: Context,
        segmentDurationSeconds: Int = 30,
        overlapSeconds: Int = 2,
        onSegmentProcessed: suspend (
            floatArray: FloatArray,
            progress: Int,
            startTimeSeconds: Long
        ) -> Unit
    ) {
        val wavFile = this.resample(format = AudioResampler.OutputFormat.WAV, context = context)
        val bytesPerSecond = 16000 * 2
        val inputStream = wavFile.inputStream()

        inputStream.skip(44)
        processAudioSegments(
            bytesPerSecond * segmentDurationSeconds,
            bytesPerSecond * overlapSeconds,
            wavFile.length() - 44,
            inputStream,
            onProcess = { totalSegments, segmentNumber, startTime, floatArray ->
                val progress = (segmentNumber.toFloat() / totalSegments * 100).toInt()
                val startTimeSeconds = ((startTime.toFloat() / bytesPerSecond) * 1000).toLong()

                onSegmentProcessed(floatArray, progress, startTimeSeconds)
            }
        )
        this.resample(format = AudioResampler.OutputFormat.MP3, context = context)
            .moveTo(this.absoluteFile)

        wavFile.delete()
        inputStream.close()
    }

    suspend fun processAudioSegments(
        segmentSize: Int,
        overlapSize: Int,
        audioSize: Long,
        inputStream: FileInputStream,
        onProcess: suspend (
            totalSegments: Int,
            segmentNumber: Int,
            startTime: Long,
            segmentProcessed: FloatArray
        ) -> Unit
    ) {
        var segmentNumber = 0
        var currentPositionBytes = 0L
        var previousOverlap = ByteArray(0)
        val totalSegments =
            ((audioSize - overlapSize) / (segmentSize - overlapSize).toFloat()).toInt() + 1

        while (currentPositionBytes < audioSize) {
            segmentNumber++

            val bytesToRead = minOf(
                segmentSize - previousOverlap.size,
                (audioSize - currentPositionBytes).toInt()
            )
            val buffer = ByteArray(bytesToRead)
            val bytesRead = inputStream.read(buffer)

            if (bytesRead == -1) break

            val fullSegment = if (previousOverlap.isNotEmpty()) {
                previousOverlap + buffer.copyOf(bytesRead)
            } else {
                buffer.copyOf(bytesRead)
            }
            val floatArray = normalizeAudio(fullSegment)
            val startTime = if (segmentNumber == 1) {
                0L
            } else {
                currentPositionBytes - previousOverlap.size
            }

            onProcess(totalSegments, segmentNumber, startTime, floatArray)

            val overlapStartByte = maxOf(0, fullSegment.size - overlapSize)

            previousOverlap = fullSegment.copyOfRange(overlapStartByte, fullSegment.size)
            currentPositionBytes += bytesRead
        }
    }

    fun normalizeAudio(byteArray: ByteArray): FloatArray {
        val floatArray = FloatArray(byteArray.size / 2)
        val shortBuffer = ByteBuffer.wrap(byteArray)
            .order(ByteOrder.LITTLE_ENDIAN)
            .asShortBuffer()

        for (i in 0 until shortBuffer.limit()) {
            floatArray[i] = (shortBuffer.get(i) / 32767.0f).coerceIn(-1f..1f)
        }

        return floatArray
    }
}
