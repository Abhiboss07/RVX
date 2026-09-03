package com.rvx.companion.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * A completed watch/listen interval observed from a media session. Persisted on-device only
 * (JSON in filesDir) — nothing is uploaded. org.json is used deliberately to avoid pulling a
 * serialization dependency, keeping the build lean and offline-friendly.
 */
data class WatchSession(
    val packageName: String,
    val appLabel: String,
    val title: String,
    val artist: String,
    val startedAt: Long,
    val endedAt: Long,
) {
    val durationMs: Long get() = (endedAt - startedAt).coerceAtLeast(0)

    fun toJson(): JSONObject = JSONObject().apply {
        put("pkg", packageName)
        put("app", appLabel)
        put("title", title)
        put("artist", artist)
        put("start", startedAt)
        put("end", endedAt)
    }

    companion object {
        fun fromJson(o: JSONObject) = WatchSession(
            packageName = o.optString("pkg"),
            appLabel = o.optString("app"),
            title = o.optString("title"),
            artist = o.optString("artist"),
            startedAt = o.optLong("start"),
            endedAt = o.optLong("end"),
        )
    }
}

/** Aggregated numbers the dashboard renders. All computed locally. */
data class WatchStats(
    val todayMs: Long = 0,
    val weekMs: Long = 0,
    val monthMs: Long = 0,
    val totalSessions: Int = 0,
    val topApps: List<Pair<String, Long>> = emptyList(),
    val streakDays: Int = 0,
    /** Last 7 days (oldest→newest) of daily watch millis, for the heatmap/bar row. */
    val last7Days: List<Long> = List(7) { 0L },
    /** Most-watched channels/artists (by observed listen time). */
    val topChannels: List<Pair<String, Long>> = emptyList(),
    /** Total for the previous 7-day window, for a week-over-week trend. */
    val prevWeekMs: Long = 0,
    /** Average daily watch time across the last 7 days. */
    val dailyAvgMs: Long = 0,
) {
    /** Week-over-week change as a fraction (e.g. +0.25). Null when there's no prior baseline. */
    val weekTrend: Float?
        get() = if (prevWeekMs <= 0) null else (weekMs - prevWeekMs).toFloat() / prevWeekMs
}

class WatchRepository(context: Context) {

    private val file = File(context.filesDir, "watch_sessions.json")
    private val sessions = mutableListOf<WatchSession>()

    private val _stats = MutableStateFlow(WatchStats())
    val stats: StateFlow<WatchStats> = _stats.asStateFlow()

    init {
        load()
        recompute()
    }

    @Synchronized
    fun record(session: WatchSession) {
        // Ignore blips shorter than 3s so scrubbing/relaunch noise doesn't skew stats.
        if (session.durationMs < TimeUnit.SECONDS.toMillis(3)) return
        sessions.add(session)
        persist()
        recompute()
    }

    @Synchronized
    fun clear() {
        sessions.clear()
        persist()
        recompute()
    }

    private fun load() {
        if (!file.exists()) return
        runCatching {
            val arr = JSONArray(file.readText())
            for (i in 0 until arr.length()) sessions.add(WatchSession.fromJson(arr.getJSONObject(i)))
        }
    }

    private fun persist() {
        val arr = JSONArray()
        sessions.forEach { arr.put(it.toJson()) }
        runCatching { file.writeText(arr.toString()) }
    }

    private fun recompute() {
        val now = System.currentTimeMillis()
        val startOfToday = startOfDay(now)
        val weekAgo = startOfToday - TimeUnit.DAYS.toMillis(6)
        val prevWeekStart = weekAgo - TimeUnit.DAYS.toMillis(7)
        val monthAgo = now - TimeUnit.DAYS.toMillis(30)

        var today = 0L; var week = 0L; var month = 0L; var prevWeek = 0L
        val perApp = HashMap<String, Long>()
        val perChannel = HashMap<String, Long>()
        val perDay = LongArray(7)

        for (s in sessions) {
            val d = s.durationMs
            if (s.startedAt >= startOfToday) today += d
            if (s.startedAt >= weekAgo) week += d
            if (s.startedAt in prevWeekStart until weekAgo) prevWeek += d
            if (s.startedAt >= monthAgo) month += d
            perApp[s.appLabel] = (perApp[s.appLabel] ?: 0) + d
            if (s.artist.isNotBlank()) perChannel[s.artist] = (perChannel[s.artist] ?: 0) + d
            val dayIdx = ((startOfDay(s.startedAt) - weekAgo) / TimeUnit.DAYS.toMillis(1)).toInt()
            if (dayIdx in 0..6) perDay[dayIdx] += d
        }

        _stats.value = WatchStats(
            todayMs = today,
            weekMs = week,
            monthMs = month,
            totalSessions = sessions.size,
            topApps = perApp.entries.sortedByDescending { it.value }.take(5).map { it.key to it.value },
            streakDays = computeStreak(startOfToday),
            last7Days = perDay.toList(),
            topChannels = perChannel.entries.sortedByDescending { it.value }.take(5).map { it.key to it.value },
            prevWeekMs = prevWeek,
            dailyAvgMs = week / 7,
        )
    }

    private fun computeStreak(startOfToday: Long): Int {
        val daysWithActivity = sessions.map { startOfDay(it.startedAt) }.toHashSet()
        var streak = 0
        var day = startOfToday
        // Allow the streak to count from today or yesterday (today may be empty so far).
        if (day !in daysWithActivity) day -= TimeUnit.DAYS.toMillis(1)
        while (day in daysWithActivity) {
            streak++
            day -= TimeUnit.DAYS.toMillis(1)
        }
        return streak
    }

    private fun startOfDay(ts: Long): Long = Calendar.getInstance().apply {
        timeInMillis = ts
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}
