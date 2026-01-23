package io.github.vinnih.kipty.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
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
        file
    }
}

fun Uri.getFileName(context: Context): String = context.contentResolver.query(
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

fun Uri.getFileSize(context: Context): Long {
    var fileSize: Long = -1

    context.contentResolver.query(this, null, null, null, null)?.use { cursor ->
        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
        if (cursor.moveToFirst() && sizeIndex != -1) {
            fileSize = cursor.getLong(sizeIndex)
        }
    }

    return fileSize
}
