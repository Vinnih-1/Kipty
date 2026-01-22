package io.github.vinnih.kipty.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import io.github.vinnih.kipty.utils.AudioResampler.resample
import java.io.File

fun Uri.processUriToFile(context: Context): File? {
    val name =
        context.contentResolver.query(
            this,
            arrayOf(OpenableColumns.DISPLAY_NAME),
            null,
            null,
            null
        )?.use { cursor ->
            if (!cursor.moveToFirst()) return@use null
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (index != -1) cursor.getString(index) else null
        } ?: "temp_audio"

    return context.contentResolver.openInputStream(this)?.use { input ->
        val file = File(context.cacheDir, name)
        file.outputStream().use { output -> input.copyTo(output) }
        file.resample(format = AudioResampler.OutputFormat.OPUS, context = context)
    }
}
