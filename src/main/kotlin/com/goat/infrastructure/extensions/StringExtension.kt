package com.goat.infrastructure.extensions

import java.time.Duration

fun String?.toDuration(): Duration? {
    if (this == null) return null

    var seconds = 0L

    val pattern = Regex("(\\d+)([dhms])")

    pattern.findAll(this).forEach { match ->
        val value = match.groupValues[1].toLong()
        val unit = match.groupValues[2]

        seconds += when (unit) {
            "d" -> value * 60 * 60 * 24
            "h" -> value * 60 * 60
            "m" -> value * 60
            "s" -> value
            else -> 0
        }
    }

    return Duration.ofSeconds(seconds)
}