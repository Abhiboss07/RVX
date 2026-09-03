package com.rvx.companion.media

import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.SystemClock
import android.service.notification.NotificationListenerService
import androidx.palette.graphics.Palette
import com.rvx.companion.RvxApp
import com.rvx.companion.core.EventLog
import com.rvx.companion.data.WatchSession

/**
 * Observes the active media session across the system via [MediaSessionManager]. We extend
 * NotificationListenerService only because that is the sanctioned way to receive
 * `getActiveSessions` results without being a system app — we never read notification
 * content. When the user watches a video / plays audio in *any* app (YouTube, RVX, a music
 * player), we get its MediaController and translate PlaybackState/metadata changes into:
 *   1. [PlaybackBus] snapshots (drive the UI, diagnostics, edge-lighting + Glyph), and
 *   2. completed [WatchSession] records (local analytics).
 * Notable events are mirrored to [EventLog] for Developer Mode.
 */
class MediaObserverService : NotificationListenerService() {

    private lateinit var sessionManager: MediaSessionManager
    private val componentName by lazy { ComponentName(this, MediaObserverService::class.java) }

    // The controller we are currently tracking, plus the open interval for analytics.
    private var tracked: MediaController? = null
    private var trackedCallback: MediaController.Callback? = null
    private var openStartedAt: Long = 0
    private var openWasPlaying = false

    // De-dupe event-log spam / carry last measured latency across refresh emits.
    private var lastLoggedPhase: PlaybackPhase? = null
    private var lastLoggedTitle: String? = null
    private var lastLatencyMs = -1L

    private enum class Reason { STATE, METADATA, REFRESH }

    private companion object {
        // A state callback whose timestamp is within this window is treated as a fresh transition
        // we can measure observation latency from; older than this, latency is reported unknown.
        const val FRESH_TRANSITION_MS = 3_000L
    }

    private val sessionsChanged =
        MediaSessionManager.OnActiveSessionsChangedListener { controllers -> bind(controllers) }

    override fun onListenerConnected() {
        sessionManager = getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
        EventLog.log("listener", "Notification listener connected")
        runCatching {
            sessionManager.addOnActiveSessionsChangedListener(sessionsChanged, componentName)
            bind(sessionManager.getActiveSessions(componentName))
        }.onFailure { EventLog.log("listener", "getActiveSessions failed: ${it.message}") }
    }

    override fun onListenerDisconnected() {
        EventLog.log("listener", "Notification listener disconnected")
        runCatching { sessionManager.removeOnActiveSessionsChangedListener(sessionsChanged) }
        detach()
        // Ask the system to rebind us if it killed the listener.
        runCatching { requestRebind(componentName) }
    }

    /** Pick the most relevant controller (first actively playing, else first present). */
    private fun bind(controllers: List<MediaController>?) {
        val list = controllers.orEmpty()
        val chosen = list.firstOrNull {
            it.playbackState?.state == PlaybackState.STATE_PLAYING
        } ?: list.firstOrNull()

        if (chosen?.packageName == tracked?.packageName && chosen != null) {
            // Same app still in charge; refresh snapshot from its current state.
            emit(chosen, Reason.REFRESH)
            return
        }
        detach()
        if (chosen == null) {
            EventLog.log("session", "No active media session")
            PlaybackBus.publish(PlaybackSnapshot(phase = PlaybackPhase.STOPPED))
            return
        }
        EventLog.log("session", "Bound to ${chosen.packageName}")
        val cb = object : MediaController.Callback() {
            override fun onPlaybackStateChanged(state: PlaybackState?) = emit(chosen, Reason.STATE)
            override fun onMetadataChanged(metadata: MediaMetadata?) = emit(chosen, Reason.METADATA)
            override fun onSessionDestroyed() {
                EventLog.log("session", "Session destroyed: ${chosen.packageName}")
                detach()
            }
        }
        chosen.registerCallback(cb)
        tracked = chosen
        trackedCallback = cb
        emit(chosen, Reason.STATE)
    }

    private fun emit(controller: MediaController, reason: Reason) {
        val state = controller.playbackState
        val meta = controller.metadata
        val phase = when (state?.state) {
            PlaybackState.STATE_PLAYING -> PlaybackPhase.PLAYING
            PlaybackState.STATE_BUFFERING, PlaybackState.STATE_CONNECTING -> PlaybackPhase.BUFFERING
            PlaybackState.STATE_PAUSED -> PlaybackPhase.PAUSED
            else -> PlaybackPhase.STOPPED
        }
        val title = meta?.getString(MediaMetadata.METADATA_KEY_TITLE).orEmpty()
        val artist = meta?.getString(MediaMetadata.METADATA_KEY_ARTIST)
            ?: meta?.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST).orEmpty()
        val durationMs = (meta?.getLong(MediaMetadata.METADATA_KEY_DURATION) ?: 0L).coerceAtLeast(0L)
        val positionMs = (state?.position ?: 0L).coerceAtLeast(0L)
        val speed = state?.playbackSpeed?.takeIf { it > 0f } ?: 1f
        val art: Bitmap? = meta?.let {
            it.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)
                ?: it.getBitmap(MediaMetadata.METADATA_KEY_ART)
                ?: it.getBitmap(MediaMetadata.METADATA_KEY_DISPLAY_ICON)
        }
        val dominant = dominantColorOf(art)

        val now = SystemClock.elapsedRealtime()
        val syncBase = state?.lastPositionUpdateTime?.takeIf { it > 0L } ?: now
        if (reason == Reason.STATE) {
            // On a genuine state change, lastPositionUpdateTime ~= when the app set it, so the gap
            // to now is the session -> observer propagation delay. But if that timestamp is old,
            // this callback is not a fresh transition (e.g. we just bound to an idle session), and
            // the gap is "time since last change", not observation latency — report unknown then.
            val raw = now - (state?.lastPositionUpdateTime ?: now)
            lastLatencyMs = if (raw in 0L..FRESH_TRANSITION_MS) raw else -1L
        }

        if (phase != lastLoggedPhase) {
            EventLog.log("state", "${lastLoggedPhase ?: "—"} -> $phase")
            lastLoggedPhase = phase
        }
        if (title.isNotBlank() && title != lastLoggedTitle) {
            EventLog.log("metadata", "Now: $title")
            lastLoggedTitle = title
        }

        updateAnalytics(controller.packageName, appLabel(controller.packageName), title, artist, phase)

        PlaybackBus.publish(
            PlaybackSnapshot(
                phase = phase,
                packageName = controller.packageName,
                appLabel = appLabel(controller.packageName),
                title = title,
                artist = artist,
                positionMs = positionMs,
                durationMs = durationMs,
                speed = speed,
                albumArt = art,
                dominantColor = dominant,
                positionSyncElapsed = syncBase,
                latencyMs = lastLatencyMs,
                emittedAtElapsed = now,
                source = PlaybackSnapshot.Source.LIVE,
            )
        )
    }

    /**
     * Maintains an open "playing" interval. When playback stops/pauses/changes app, the open
     * interval is closed and recorded (if analytics is enabled).
     */
    private fun updateAnalytics(
        pkg: String, app: String, title: String, artist: String, phase: PlaybackPhase,
    ) {
        val nowPlaying = phase == PlaybackPhase.PLAYING
        if (nowPlaying && !openWasPlaying) {
            openStartedAt = System.currentTimeMillis()
            openWasPlaying = true
            pendingTitle = title; pendingArtist = artist; pendingPkg = pkg; pendingApp = app
        } else if (!nowPlaying && openWasPlaying) {
            closeOpenInterval()
        }
    }

    private var pendingTitle = ""; private var pendingArtist = ""
    private var pendingPkg = ""; private var pendingApp = ""

    private fun closeOpenInterval() {
        if (!openWasPlaying) return
        openWasPlaying = false
        val app = RvxApp.instance
        // Respect the analytics toggle; if off, discard the interval.
        val enabled = runCatching { app.analyticsEnabledBlocking() }.getOrDefault(true)
        if (enabled) {
            app.watch.record(
                WatchSession(
                    packageName = pendingPkg,
                    appLabel = pendingApp,
                    title = pendingTitle,
                    artist = pendingArtist,
                    startedAt = openStartedAt,
                    endedAt = System.currentTimeMillis(),
                )
            )
        }
    }

    private fun detach() {
        closeOpenInterval()
        trackedCallback?.let { cb -> runCatching { tracked?.unregisterCallback(cb) } }
        tracked = null
        trackedCallback = null
        lastLoggedPhase = null
        PlaybackBus.publish(PlaybackSnapshot(phase = PlaybackPhase.STOPPED))
    }

    private fun appLabel(pkg: String): String = runCatching {
        val pm = packageManager
        pm.getApplicationLabel(pm.getApplicationInfo(pkg, 0)).toString()
    }.getOrDefault(pkg)

    // Dominant-color extraction, cached by art identity so it runs once per artwork (not per emit).
    private var lastArt: Bitmap? = null
    private var lastColor: Int? = null

    private fun dominantColorOf(art: Bitmap?): Int? {
        if (art == null) { lastArt = null; lastColor = null; return null }
        if (art === lastArt) return lastColor
        lastArt = art
        lastColor = runCatching {
            val small = Bitmap.createScaledBitmap(art, 64, 64, false)
            val p = Palette.from(small).generate()
            (p.getVibrantColor(0) .takeIf { it != 0 }
                ?: p.getDominantColor(0).takeIf { it != 0 })
        }.getOrNull()
        return lastColor
    }
}
