package io.github.vinnih.kipty.utils

import android.text.format.DateUtils
import io.github.vinnih.kipty.data.database.entity.AudioTranscription
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Locale

private enum class Timestamp(val multiplier: Long) {
    HOUR(1000 * 60 * 60),
    MINUTE(1000 * 60),
    SECOND(1000),
    MILLISECOND(1)
}

fun String.toRelativeTime(): String {
    val millis = LocalDateTime.parse(this).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val now = System.currentTimeMillis()

    return DateUtils.getRelativeTimeSpanString(
        millis,
        now,
        DateUtils.MINUTE_IN_MILLIS,
        DateUtils.FORMAT_ABBREV_RELATIVE
    ).toString()
}

fun Long.formatTime(): String {
    if (this < 0) return "--:--"
    val totalSeconds = this / 1000
    val minutes = totalSeconds / 60
    val remainingSeconds = totalSeconds % 60

    return String.format(Locale.ENGLISH, "%02d:%02d", minutes, remainingSeconds)
}

fun String.timestamp(): Pair<Long, Long> {
    val pair = Pair(0L, 0L)
    val map = HashMap<Int, Long>()
    val times = this.split(" --> ").map { it.replace("[", "").replace("]", "") }

    if (times.size != 2) return pair.copy()

    times.forEachIndexed { index, time ->
        var currentValue = map.getOrDefault(index, 0L)
        time.split(":").forEachIndexed { index, part ->

            when (index) {
                0 -> currentValue += part.toLong() * Timestamp.HOUR.multiplier

                1 -> currentValue += part.toLong() * Timestamp.MINUTE.multiplier

                2 -> {
                    val split = part.split(".")
                    currentValue += split[0].toLong() * Timestamp.SECOND.multiplier
                    currentValue += split[1].toLong() * Timestamp.MILLISECOND.multiplier
                }
            }
        }
        map[index] = currentValue
    }

    if (map.size != 2) return pair.copy()

    return pair.copy(first = map.get(0)!!, second = map.get(1)!!)
}

fun Long.timestamp(): String {
    var milliseconds = this

    val hours = milliseconds / Timestamp.HOUR.multiplier
    milliseconds -= hours * Timestamp.HOUR.multiplier

    val minutes = milliseconds / Timestamp.MINUTE.multiplier
    milliseconds -= minutes * Timestamp.MINUTE.multiplier

    val seconds = milliseconds / Timestamp.SECOND.multiplier

    val timestamp = if (hours > 0) {
        String.format(
            Locale.ENGLISH,
            "%02d:%02d:%02d",
            hours,
            minutes,
            seconds
        )
    } else {
        String.format(
            Locale.ENGLISH,
            "%02d:%02d",
            minutes,
            seconds
        )
    }

    return timestamp
}

fun String.convertTranscription(): List<AudioTranscription> =
    this.trimIndent().split("\n").map { line ->
        val timestamp = line.take(31).timestamp()
        val text = line.drop(31)

        AudioTranscription(timestamp.first, timestamp.second, text)
    }.toList()
