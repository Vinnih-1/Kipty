package io.github.vinnih.kipty.utils

fun Long.getFormattedSize(): String = when {
    this >= 1024 * 1024 * 1024 -> "%.1f GB".format(this / (1024.0 * 1024.0 * 1024.0))
    this >= 1024 * 1024 -> "%.1f MB".format(this / (1024.0 * 1024.0))
    this >= 1024 -> "%.1f KB".format(this / 1024.0)
    else -> "$this Bytes"
}
