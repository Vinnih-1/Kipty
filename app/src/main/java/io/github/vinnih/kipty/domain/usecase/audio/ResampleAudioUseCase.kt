package io.github.vinnih.kipty.domain.usecase.audio

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.vinnih.kipty.data.service.audio.AudioService
import io.github.vinnih.kipty.data.service.audio.OutputFormat
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ResampleAudioUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val audioService: AudioService
) {

    suspend operator fun invoke(
        file: File,
        sampleRate: Int = 16000,
        channels: Int = 1,
        bitrate: Int = 16,
        format: OutputFormat = OutputFormat.MP3
    ): File = withContext(Dispatchers.IO) {
        audioService.resample(
            file = file,
            sampleRate = sampleRate,
            channels = channels,
            bitrate = bitrate,
            format = format,
            context = context
        )
    }
}
