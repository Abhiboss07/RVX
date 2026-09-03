package com.rvx.companion.media

import android.graphics.Bitmap
import android.os.SystemClock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Coarse playback phase, mapped from PlaybackState by [MediaObserverService]. */
enum class PlaybackPhase { STOPPED, PLAYING, PAUSED, BUFFERING }

/**
 * Immutable snapshot of "what is playing right now". Written by [MediaObserverService] from the
 * live media session, or by the in-app test engine (see [source]). Position is stored together
 * with the elapsed-realtime baseline it was sampled at ([positionSyncElapsed]) so consumers can
 * interpolate a smooth live position via [livePositionMs] without any background timer.
 */
data class PlaybackSnapshot(
    val phase: PlaybackPhase = PlaybackPhase.STOPPED,
    val packageName: String = "",
    val appLabel: String = "",
    val title: String = "",
    val artist: String = "",
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val speed: Float = 1f,
    val albumArt: Bitmap? = null,
    /** Dominant/vibrant color extracted from [albumArt] (ARGB), or null when no art. */
    val dominantColor: Int? = null,
    /** elapsedRealtime baseline at which [positionMs] was sampled; 0 if unknown. */
    val positionSyncElapsed: Long = 0L,
    /** Delay (ms) between the session publishing a state change and us observing it; -1 unknown. */
    val latencyMs: Long = -1L,
    /** elapsedRealtime when this snapshot was published. */
    val emittedAtElapsed: Long = 0L,
    val source: Source = Source.LIVE,
) {
    enum class Source { LIVE, TEST }

    val hasAlbumArt: Boolean get() = albumArt != null

    /** 0f..1f playback progress when a duration is known, else null. */
    val progress: Float?
        get() = if (durationMs > 0L) (positionMs.toFloat() / durationMs).coerceIn(0f, 1f) else null

    /** Position interpolated to [nowElapsed]; advances only while PLAYING. */
    fun livePositionMs(nowElapsed: Long = SystemClock.elapsedRealtime()): Long {
        if (positionSyncElapsed <= 0L) return positionMs
        val extra = if (phase == PlaybackPhase.PLAYING) {
            ((nowElapsed - positionSyncElapsed) * speed).toLong()
        } else 0L
        val cap = if (durationMs > 0L) durationMs else Long.MAX_VALUE
        return (positionMs + extra).coerceIn(0L, cap)
    }
}

/**
 * Process-wide hot state of the current playback. The media observer writes it; the dashboard,
 * diagnostics, developer view, edge-lighting service and Glyph controller read it. Decoupling via
 * this bus keeps the NotificationListenerService free of direct references to every consumer.
 *
 * When [setTestMode] is enabled, LIVE publishes are dropped so the test engine's synthetic events
 * drive the pipeline without interference from real playback.
 */
object PlaybackBus {
    private val _state = MutableStateFlow(PlaybackSnapshot())
    val state: StateFlow<PlaybackSnapshot> = _state.asStateFlow()

    @Volatile
    private var testMode = false
    val isTestMode: Boolean get() = testMode

    fun setTestMode(on: Boolean) {
        testMode = on
    }

    fun publish(snapshot: PlaybackSnapshot) {
        if (testMode && snapshot.source == PlaybackSnapshot.Source.LIVE) return
        _state.value = snapshot
    }
}
