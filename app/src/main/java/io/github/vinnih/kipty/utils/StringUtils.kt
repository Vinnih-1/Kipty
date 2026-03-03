package io.github.vinnih.kipty.utils

import android.text.format.DateUtils
import io.github.vinnih.kipty.data.database.entity.AudioTranscription
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import org.json.JSONObject

private enum class Timestamp(val multiplier: Long) {
    HOUR(1000 * 60 * 60),
    MINUTE(1000 * 60),
    SECOND(1000),
    MILLISECOND(1)
}

@Suppress("ktlint:standard:max-line-length")
fun String.toRelativeTime(): String {
    val millis = LocalDateTime.parse(this).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val now = System.currentTimeMillis()
    val diffMillis = now - millis

    return when {
        diffMillis < DateUtils.MINUTE_IN_MILLIS -> "just now"

        diffMillis < DateUtils.HOUR_IN_MILLIS -> "${(diffMillis / DateUtils.MINUTE_IN_MILLIS).toInt()} min ago"

        diffMillis < DateUtils.DAY_IN_MILLIS -> "${(diffMillis / DateUtils.HOUR_IN_MILLIS).toInt()} hr ago"

        else -> {
            val days = (diffMillis / DateUtils.DAY_IN_MILLIS).toInt()
            "$days day${if (days > 1) "s" else ""} ago"
        }
    }
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

fun String.convertTranscription(): List<AudioTranscription> {
    val result = mutableListOf<AudioTranscription>()
    val lines = this.trimIndent().split("\n")

    // Vosk emits multi-line JSON — accumulate lines until braces balance
    val jsonBuffer = StringBuilder()
    var braceDepth = 0

    for (line in lines) {
        val trimmed = line.trim()
        if (trimmed.isEmpty()) continue

        if (trimmed.startsWith("[")) {
            // Legacy Whisper SRT format: [00:00:00.000 --> 00:00:02.500] text
            val timestamp = trimmed.take(31).timestamp()
            val text = trimmed.drop(31)
            if (text.isNotBlank()) result.add(AudioTranscription(timestamp.first, timestamp.second, text))
            continue
        }

        for (ch in trimmed) {
            when (ch) {
                '{' -> braceDepth++
                '}' -> braceDepth--
            }
        }
        jsonBuffer.appendLine(trimmed)

        if (braceDepth == 0 && jsonBuffer.isNotBlank()) {
            try {
                val json = JSONObject(jsonBuffer.toString().trim())
                val text = json.optString("text").trim()
                if (text.isNotEmpty()) {
                    val words = json.optJSONArray("result")
                    val startMs = if (words != null && words.length() > 0)
                        (words.getJSONObject(0).getDouble("start") * 1000).toLong()
                    else 0L
                    val endMs = if (words != null && words.length() > 0)
                        (words.getJSONObject(words.length() - 1).getDouble("end") * 1000).toLong()
                    else 0L
                    result.add(AudioTranscription(startMs, endMs, text))
                }
            } catch (_: Exception) {
            } finally {
                jsonBuffer.clear()
            }
        }
    }

    return result
}

fun String.formatDate(): String {
    val inputFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    val outputFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH)

    val dateTime = LocalDateTime.parse(this, inputFormatter)
    return outputFormatter.format(dateTime)
}
