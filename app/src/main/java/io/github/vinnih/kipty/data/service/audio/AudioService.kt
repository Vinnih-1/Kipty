package io.github.vinnih.kipty.data.service.audio

import android.content.Context
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFprobeKit
import com.arthenica.ffmpegkit.ReturnCode
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.io.nameWithoutExtension
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
class AudioService @Inject constructor() {

    fun resample(
        file: File,
        sampleRate: Int = 16000,
        channels: Int = 1,
        bitrate: Int = 16,
        format: OutputFormat = OutputFormat.MP3,
        context: Context
    ): File {
        val outputFile = File(
            context.cacheDir,
            "${file.nameWithoutExtension}.${format.extension}"
        )

        val command = buildString {
            append("-i \"${file.absolutePath}\" ")
            append("-ar $sampleRate ")
            append("-ac $channels ")

            when (format) {
                OutputFormat.MP3 -> {
                    append("-b:a ${bitrate}k ")
                }

                OutputFormat.WAV -> {
                    append("-c:a pcm_s16le ")
                }

                OutputFormat.OPUS -> {
                    append("-c:a libopus ")
                    append("-b:a ${bitrate}k ")
                }
            }

            append("-y ")
            append("\"${outputFile.absolutePath}\"")
        }

        val session = FFmpegKit.execute(command)

        if (!ReturnCode.isSuccess(session.returnCode)) {
            throw Exception("Resample failure: ${session.failStackTrace}")
        }

        return outputFile
    }

    suspend fun getAudioDuration(filePath: String): Long? = withContext(Dispatchers.IO) {
        try {
            val session = FFprobeKit.execute(
                "-v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 \"$filePath\""
            )

            if (session.returnCode.isValueSuccess) {
                session.output?.trim()?.toDoubleOrNull()?.times(1000)?.toLong()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
