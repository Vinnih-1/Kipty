package io.github.vinnih.kipty.utils

import android.content.Context
import io.github.nailik.androidresampler.Resampler
import io.github.nailik.androidresampler.ResamplerConfiguration
import io.github.nailik.androidresampler.data.ResamplerChannel
import io.github.nailik.androidresampler.data.ResamplerQuality
import io.github.vinnih.androidtranscoder.utils.toWavReader
import java.io.File
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

fun File.createFile(): File {
    if (!this.exists()) {
        this.createNewFile()
    }

    return this
}

fun File.createFolder(): File {
    if (!this.exists()) {
        this.mkdirs()
    }

    return this
}

fun resample(channels: Int, inSampleRate: Int, outSampleRate: Int, pcmData: ByteArray): ByteArray {
    val resamplerConfiguration = ResamplerConfiguration(
        quality = ResamplerQuality.BEST,
        inputChannel = if (channels == 1) ResamplerChannel.MONO else ResamplerChannel.STEREO,
        inputSampleRate = inSampleRate,
        outputChannel = ResamplerChannel.MONO,
        outputSampleRate = outSampleRate
    )
    val resampler = Resampler(resamplerConfiguration)

    return resampler.resample(pcmData)
}

suspend fun File.processAudioSegments(
    context: Context,
    segmentDurationSeconds: Int = 30,
    onSegmentProcessed: suspend (segmentNumber: Int, floatArray: FloatArray, progress: Int) -> Unit
) {
    val reader = this.toWavReader(context.cacheDir)

    val bytesPerSample = 2
    val bytesPerSecond = reader.sampleRate * reader.channels * bytesPerSample
    val segmentSizeBytes = bytesPerSecond * segmentDurationSeconds
    val audioDataSize = reader.data.length() - 44
    val totalSegments = (audioDataSize + segmentSizeBytes - 1) / segmentSizeBytes

    val inputStream = reader.data.inputStream()
    inputStream.skip(44)

    val buffer = ByteArray(segmentSizeBytes)
    var bytesRead: Int
    var segmentNumber = 0

    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
        segmentNumber++

        val actualData = if (bytesRead < segmentSizeBytes) {
            buffer.copyOf(bytesRead)
        } else {
            buffer
        }

        val resampledPcm = resample(reader.channels, reader.sampleRate, 16000, actualData)

        val floatArray = FloatArray(resampledPcm.size / 2)
        val shortBuffer = ByteBuffer.wrap(resampledPcm)
            .order(ByteOrder.LITTLE_ENDIAN)
            .asShortBuffer()

        for (i in 0 until shortBuffer.limit()) {
            floatArray[i] = (shortBuffer.get(i) / 32767.0f).coerceIn(-1f..1f)
        }
        val progress = (segmentNumber.toFloat() / totalSegments.toFloat() * 100).toInt()

        onSegmentProcessed(
            segmentNumber,
            floatArray,
            progress
        )
    }

    inputStream.close()
}

fun InputStream.copyTo(file: File) {
    file.outputStream().use { outputStream ->
        this.copyTo(outputStream)
    }
}

fun File.moveTo(file: File) {
    this.copyTo(file, overwrite = true)
    this.deleteRecursively()
}
