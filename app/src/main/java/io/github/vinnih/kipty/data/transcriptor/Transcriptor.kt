package io.github.vinnih.kipty.data.transcriptor

import android.content.Context
import com.whispercpp.whisper.WhisperContext
import io.github.vinnih.kipty.data.database.entity.AudioEntity
import java.io.File

interface Transcriptor {

    var whisperContext: WhisperContext

    fun copyModel(): File

    fun loadModel(): WhisperContext

    suspend fun transcribe(audioEntity: AudioEntity): AudioEntity
}
