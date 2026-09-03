package com.rvx.companion.core

/** Clock-style duration: "1:07", "1:02:14". Negative inputs clamp to 0. */
fun formatClock(ms: Long): String {
    val totalSec = (ms / 1000).coerceAtLeast(0)
    val h = totalSec / 3600
    val m = (totalSec % 3600) / 60
    val s = totalSec % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%d:%02d".format(m, s)
}

/** Trim a playback speed to a compact label: 1.0 -> "1", 1.5 -> "1.5", 1.25 -> "1.25". */
fun trimSpeed(speed: Float): String {
    if (speed == speed.toInt().toFloat()) return speed.toInt().toString()
    return (Math.round(speed * 100) / 100.0).toString()
}
