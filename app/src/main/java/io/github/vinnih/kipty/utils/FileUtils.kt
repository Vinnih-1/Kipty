package io.github.vinnih.kipty.utils

import java.io.File
import java.io.InputStream

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

fun InputStream.copyTo(file: File) {
    file.outputStream().use { outputStream ->
        this.copyTo(outputStream)
    }
}

fun File.moveTo(file: File) {
    this.copyTo(file, overwrite = true)
    this.deleteRecursively()
}
