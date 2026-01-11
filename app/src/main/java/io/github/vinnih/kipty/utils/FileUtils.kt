package io.github.vinnih.kipty.utils

import android.content.Context
import io.github.vinnih.kipty.utils.AudioResampler.resample
import java.io.File
import java.io.FileInputStream
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
        val startTime = if (segmentNumber == 1) 0L else currentPositionBytes - previousOverlap.size

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

fun InputStream.copyTo(file: File) {
    file.outputStream().use { outputStream ->
        this.copyTo(outputStream)
    }
}

fun File.moveTo(file: File) {
    this.copyTo(file, overwrite = true)
    this.deleteRecursively()
}
