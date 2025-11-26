package io.github.vinnih.kipty.utils

import android.content.Context
import android.net.Uri
import java.io.File

fun copyFile(context: Context, uri: Uri): File {
    val inputStream = context.contentResolver.openInputStream(uri)!!
    val file = File(context.filesDir, "")
    inputStream.use { input ->
        file.outputStream().use { output -> input.copyTo(output) }
    }
    return file
}
