package io.github.vinnih.kipty.data.transcriptor

import com.whispercpp.whisper.WhisperContext
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import java.io.File

interface Transcriptor {

    val whisperContext: WhisperContext

    fun copyModel(): File

    fun loadModel(): WhisperContext

    suspend fun initialize()

    suspend fun transcribe(
        audioEntity: AudioEntity,
        numThreads: Int,
        onProgress: (Int) -> Unit
    ): AudioEntity

    suspend fun transcribe(floatArray: FloatArray): String
}
