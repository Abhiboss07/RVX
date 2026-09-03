package com.rvx.companion.core

import android.os.SystemClock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Process-wide ring buffer of the most recent [MAX] events, surfaced live in Developer Mode.
 * Cheap and lock-guarded; safe to call from the media service, the test engine, and the UI.
 * Newest entry first.
 */
object EventLog {
    const val MAX = 50

    data class Entry(
        val elapsed: Long,      // SystemClock.elapsedRealtime() when logged
        val wallClock: Long,    // System.currentTimeMillis() for display
        val tag: String,
        val message: String,
    )

    private val _entries = MutableStateFlow<List<Entry>>(emptyList())
    val entries: StateFlow<List<Entry>> = _entries.asStateFlow()

    @Synchronized
    fun log(tag: String, message: String) {
        val e = Entry(SystemClock.elapsedRealtime(), System.currentTimeMillis(), tag, message)
        _entries.value = (listOf(e) + _entries.value).take(MAX)
    }

    fun clear() {
        _entries.value = emptyList()
    }
}
