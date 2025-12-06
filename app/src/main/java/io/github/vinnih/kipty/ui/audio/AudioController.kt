package io.github.vinnih.kipty.ui.audio

import io.github.vinnih.kipty.data.database.entity.AudioTranscription
import java.io.File

interface AudioController {

    suspend fun convertTranscription(transcribedData: String): List<AudioTranscription>

    suspend fun transcribeAudio(audio: File): String
}
