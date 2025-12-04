package io.github.vinnih.kipty.utils

private enum class Timestamp(val multiplier: Long) {
    HOUR(1000 * 60 * 60),
    MINUTE(1000 * 60),
    SECOND(1000),
    MILLISECOND(1)
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

    return pair.copy(map.get(0)!!, map.get(1)!!)
}

fun Long.timestamp(): String {
    var milliseconds = this

    val hours = milliseconds / Timestamp.HOUR.multiplier
    milliseconds -= hours * Timestamp.HOUR.multiplier

    val minutes = milliseconds / Timestamp.MINUTE.multiplier
    milliseconds -= minutes * Timestamp.MINUTE.multiplier

    val seconds = milliseconds / Timestamp.SECOND.multiplier
    milliseconds -= seconds / Timestamp.SECOND.multiplier

    val timestamp = String.format(
        "%02d:%02d:%02d%s%03d",
        hours,
        minutes,
        seconds,
        ".",
        milliseconds
    )

    return buildString {
        append("[")
        append(timestamp)
        append("]")
    }
}
