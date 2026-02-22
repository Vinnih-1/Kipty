package io.github.vinnih.kipty.utils

fun Long.getFormattedSize(): String = when {
    this >= 1024 * 1024 * 1024 -> "%.1f GB".format(this / (1024.0 * 1024.0 * 1024.0))
    this >= 1024 * 1024 -> "%.1f MB".format(this / (1024.0 * 1024.0))
    this >= 1024 -> "%.1f KB".format(this / 1024.0)
    else -> "$this Bytes"
}

fun Long.formatListenedTime(): String {
    val totalMinutes = this / 60
    val totalHours = totalMinutes / 60

    return when {
        totalHours >= 1 -> "${totalHours}h"
        totalMinutes >= 1 -> "${totalMinutes}m"
        else -> "${this}s"
    }
}
