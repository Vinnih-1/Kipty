package io.github.vinnih.kipty.utils

import android.content.Context
import io.github.nailik.androidresampler.Resampler
import io.github.nailik.androidresampler.ResamplerConfiguration
import io.github.nailik.androidresampler.data.ResamplerChannel
import io.github.nailik.androidresampler.data.ResamplerQuality
import io.github.vinnih.androidtranscoder.utils.toWavReader
import java.io.ByteArrayOutputStream
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

fun File.toFloatArray(context: Context): FloatArray {
    val reader = this.toWavReader(context.cacheDir)
    val baos = ByteArrayOutputStream()
    reader.data.inputStream().use { it.copyTo(baos) }

    val fullData = baos.toByteArray()
    val buffer = ByteBuffer.wrap(fullData)
    buffer.order(ByteOrder.LITTLE_ENDIAN)
    buffer.position(44)

    val pcmDataLength = buffer.limit() - buffer.position()
    val pcmData = ByteArray(pcmDataLength)
    buffer.get(pcmData, 0, pcmDataLength)

    val resampledPcm = resample(reader.channels, reader.sampleRate, 16000, pcmData)
    val finalFloatArray = FloatArray(resampledPcm.size / 2)
    val shortBuffer = ByteBuffer.wrap(resampledPcm)
        .order(ByteOrder.LITTLE_ENDIAN)
        .asShortBuffer()

    for (i in 0 until shortBuffer.limit()) {
        val shortValue = shortBuffer.get(i).toInt()
        finalFloatArray[i] = (shortValue / 32767.0f).coerceIn(-1f..1f)
    }

    return finalFloatArray
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
