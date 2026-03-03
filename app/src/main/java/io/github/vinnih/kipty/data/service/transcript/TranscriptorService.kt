package io.github.vinnih.kipty.data.service.transcript

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.vinnih.kipty.data.service.audio.AudioService
import io.github.vinnih.kipty.data.service.audio.OutputFormat
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechStreamService
import org.vosk.android.StorageService

@Singleton
class TranscriptorService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val audioService: AudioService
) {

    private val modelDeferred = CompletableDeferred<Model>()
    private lateinit var speechStreamService: SpeechStreamService

    init {
        StorageService.unpack(
            context,
            "model-en-us",
            "model",
            { model ->
                println("loaded model: $model")
                modelDeferred.complete(model)
            },
            { error ->
                modelDeferred.completeExceptionally(error)
            }
        )
    }

    suspend fun recognizeFile(
        audioFile: File,
        onRawJson: (String) -> Unit = {},
        onFailure: (Exception) -> Unit
    ): String = withContext(Dispatchers.IO) {
        val wavFile = audioService.resample(
            file = audioFile,
            format = OutputFormat.WAV,
            context = context
        )
        val inputStream = wavFile.inputStream()
        inputStream.skip(44)

        val model = modelDeferred.await()
        val jsonLines = StringBuilder()

        try {
            suspendCancellableCoroutine { continuation ->
                val recognizer = Recognizer(model, 16000f).apply {
                    setWords(true)
                }

                speechStreamService = SpeechStreamService(recognizer, inputStream, 16000f)

                continuation.invokeOnCancellation {
                    speechStreamService.stop()
                }

                speechStreamService.start(object : RecognitionListener {
                    override fun onPartialResult(hypothesis: String?) {}

                    override fun onResult(hypothesis: String?) {
                        if (!hypothesis.isNullOrBlank()) {
                            jsonLines.appendLine(hypothesis.trim())
                            onRawJson(hypothesis.trim())
                        }
                    }

                    override fun onFinalResult(hypothesis: String?) {
                        if (hypothesis == null) {
                            continuation.resumeWithException(Exception("No hypothesis"))
                        } else {
                            if (hypothesis.isNotBlank()) {
                                jsonLines.appendLine(hypothesis.trim())
                                onRawJson(hypothesis.trim())
                            }
                            continuation.resume(Unit)
                        }
                    }

                    override fun onError(exception: Exception?) {
                        continuation.resumeWithException(
                            exception ?: Exception("Unknown recognition error")
                        )
                    }

                    override fun onTimeout() {
                        continuation.resumeWithException(Exception("Recognition timed out"))
                    }
                })
            }
        } catch (e: Exception) {
            onFailure(e)
        } finally {
            inputStream.close()
            wavFile.delete()
        }

        jsonLines.toString()
    }
}
