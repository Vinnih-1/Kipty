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
        val currentValue = map.getOrDefault(index, 0L)
        time.split(":").forEachIndexed { index, part ->
            when (index) {
                0 -> currentValue.plus(part.toLong() * Timestamp.HOUR.multiplier)

                1 -> currentValue.plus(part.toLong() * Timestamp.MINUTE.multiplier)

                2 -> {
                    val split = part.split(".")
                    currentValue.plus(split[0].toLong() * Timestamp.SECOND.multiplier)
                    currentValue.plus(split[1].toLong() * Timestamp.MILLISECOND.multiplier)
                }
            }
        }
        map[index] = currentValue
    }

    if (map.size != 2) return pair.copy()

    return pair.copy(map.get(0)!!, map.get(1)!!)
}
