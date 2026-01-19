package io.github.vinnih.kipty.utils

import android.content.Context
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import java.io.File

object AudioResampler {
    enum class OutputFormat(val extension: String) {
        MP3("mp3"),
        WAV("wav")
    }

    fun File.resample(
        sampleRate: Int = 16000,
        channels: Int = 1,
        bitrate: Int = 64,
        format: OutputFormat = OutputFormat.MP3,
        context: Context
    ): File {
        val outputFile = File(
            context.cacheDir,
            "$nameWithoutExtension.${format.extension}"
        )

        val command = buildString {
            append("-i \"$absolutePath\" ")
            append("-ar $sampleRate ")
            append("-ac $channels ")

            when (format) {
                OutputFormat.MP3 -> {
                    append("-b:a ${bitrate}k ")
                }

                OutputFormat.WAV -> {
                    append("-c:a pcm_s16le ")
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
}
