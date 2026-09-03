package com.rvx.companion.diagnostics

import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import com.rvx.companion.BuildConfig
import com.rvx.companion.core.formatClock
import com.rvx.companion.core.trimSpeed
import com.rvx.companion.glyph.GlyphStatus
import com.rvx.companion.media.PlaybackPhase
import com.rvx.companion.media.PlaybackSnapshot

/** Traffic-light health for a single diagnostic row. */
enum class Health { PASS, WARNING, FAILED, INFO }

data class DiagnosticItem(
    val label: String,
    val value: String,
    val health: Health,
    val explanation: String,
    val group: String = "Detection",
)

/**
 * Single source of truth for the app's live health. [build] is a pure function of the current
 * [PlaybackSnapshot], the [GlyphStatus], a couple of edge-lighting facts and a few permission
 * probes, so the Diagnostics screen can recompute it cheaply whenever anything changes.
 */
object Diagnostics {

    fun isNotificationAccessGranted(ctx: Context): Boolean {
        val flat = Settings.Secure.getString(ctx.contentResolver, "enabled_notification_listeners")
            ?: return false
        return flat.split(":").any {
            ComponentName.unflattenFromString(it)?.packageName == ctx.packageName
        }
    }

    fun isBatteryOptimized(ctx: Context): Boolean {
        val pm = ctx.getSystemService(PowerManager::class.java) ?: return true
        return !pm.isIgnoringBatteryOptimizations(ctx.packageName)
    }

    fun canDrawOverlay(ctx: Context): Boolean = Settings.canDrawOverlays(ctx)

    fun isNothingDevice(): Boolean =
        (Build.MANUFACTURER + " " + Build.BRAND).lowercase().contains("nothing")

    fun build(
        ctx: Context,
        s: PlaybackSnapshot,
        glyph: GlyphStatus,
        edgeRunning: Boolean,
        edgeEnabled: Boolean,
        edgeColor: Int,
    ): List<DiagnosticItem> {
        val items = mutableListOf<DiagnosticItem>()
        val listener = isNotificationAccessGranted(ctx)
        val hasSession = s.packageName.isNotBlank()

        // ---------------- Detection ----------------
        items += DiagnosticItem(
            "Notification listener",
            if (listener) "Granted" else "Not granted",
            if (listener) Health.PASS else Health.FAILED,
            if (listener) "RVX Companion can read the active media session."
            else "Grant Notification access in Settings, or nothing can be detected.",
        )
        items += DiagnosticItem(
            "Media session detected", if (hasSession) "Yes" else "None",
            when {
                !listener -> Health.FAILED
                hasSession -> Health.PASS
                else -> Health.WARNING
            },
            if (hasSession) "A media app is publishing a session."
            else "No app is currently playing. Start a video in RVX to detect one.",
        )
        items += DiagnosticItem(
            "Active media package", s.packageName.ifBlank { "—" },
            if (hasSession) Health.PASS else Health.WARNING,
            "The package that owns the current media session.",
        )
        items += DiagnosticItem(
            "Playback state", s.phase.name,
            if (s.phase != PlaybackPhase.STOPPED) Health.PASS else Health.INFO,
            "Mapped from the session's PlaybackState.",
        )
        items += DiagnosticItem(
            "Position",
            if (s.durationMs > 0) "${formatClock(s.positionMs)} / ${formatClock(s.durationMs)}" else "—",
            if (s.durationMs > 0) Health.PASS else Health.WARNING,
            "Current position reported by the session.",
        )
        items += DiagnosticItem(
            "Duration", if (s.durationMs > 0) formatClock(s.durationMs) else "—",
            if (s.durationMs > 0) Health.PASS else Health.WARNING,
            "Some sessions (e.g. live streams) do not report a duration.",
        )
        items += DiagnosticItem(
            "Album art", if (s.hasAlbumArt) "Loaded" else "None",
            if (s.hasAlbumArt) Health.PASS else Health.WARNING,
            "Provided by the media app's metadata; not all sessions include it.",
        )
        items += DiagnosticItem(
            "Title", s.title.ifBlank { "—" },
            if (s.title.isNotBlank()) Health.PASS else Health.WARNING,
            "Current media title from metadata.",
        )
        items += DiagnosticItem(
            "Channel / artist", s.artist.ifBlank { "—" },
            if (s.artist.isNotBlank()) Health.PASS else Health.WARNING,
            "Channel or artist from metadata.",
        )
        items += DiagnosticItem(
            "Video quality", "Not available", Health.WARNING,
            "Resolution is NOT exposed through the MediaSession API — no third-party app can read it.",
        )
        items += DiagnosticItem(
            "Playback speed", "${trimSpeed(s.speed)}×", Health.PASS,
            "From PlaybackState.getPlaybackSpeed().",
        )
        items += DiagnosticItem(
            "MediaSession latency",
            if (s.latencyMs >= 0) "${s.latencyMs} ms" else "—",
            when {
                s.latencyMs < 0 -> Health.INFO
                s.latencyMs <= 1500 -> Health.PASS
                else -> Health.WARNING
            },
            "Delay between the session publishing a state change and us observing it.",
        )

        // ---------------- Glyph (Phase 6) ----------------
        val g = "Glyph"
        items += DiagnosticItem(
            "Glyph supported", if (glyph.supported) "Yes" else "No",
            if (glyph.supported) Health.PASS else Health.INFO,
            if (glyph.supported) "A Nothing device with Glyph hardware was detected."
            else "No Glyph hardware — the feature is safely inactive on this device.", g,
        )
        items += DiagnosticItem(
            "Glyph permission",
            if (!glyph.supported) "N/A" else if (glyph.permissionGranted) "Granted" else "Not granted",
            if (!glyph.supported) Health.INFO else if (glyph.permissionGranted) Health.PASS else Health.WARNING,
            "com.nothing.ketchum.permission.ENABLE, required for real Glyph output.", g,
        )
        items += DiagnosticItem(
            "Glyph Matrix connected",
            if (!glyph.supported) "N/A" else if (glyph.initialized) "Yes" else "No (service not bound)",
            if (!glyph.supported) Health.INFO else if (glyph.initialized) Health.PASS else Health.WARNING,
            "Live connection to the Nothing Glyph Matrix service (registered as Phone 3 / DEVICE_23112).", g,
        )
        items += DiagnosticItem(
            "Glyph animation",
            if (!glyph.supported) "N/A" else if (glyph.animationActive) "Active (${glyph.lastAnimation})" else "Idle (${glyph.lastAnimation})",
            if (!glyph.supported) Health.INFO else Health.PASS,
            "Current mapped animation. PLAYING→breathing, BUFFERING→sweep, PAUSED→static.", g,
        )
        items += DiagnosticItem(
            "Glyph playback synced", if (glyph.playbackSynced) "Yes" else "No",
            if (!glyph.supported) Health.INFO else if (glyph.playbackSynced) Health.PASS else Health.INFO,
            "Whether playback phase is currently driving real Glyph output.", g,
        )
        items += DiagnosticItem(
            "Glyph brightness", if (glyph.brightness >= 0) "${glyph.brightness}%" else "—",
            Health.INFO, "Configured Glyph brightness (only meaningful once initialized).", g,
        )
        items += DiagnosticItem(
            "Glyph animation FPS", if (glyph.fps >= 0) "${glyph.fps}" else "—",
            Health.INFO, "Glyph animation update rate (only meaningful once initialized).", g,
        )

        // ---------------- Edge lighting (Phase 7) ----------------
        val e = "Edge lighting"
        val overlay = canDrawOverlay(ctx)
        items += DiagnosticItem(
            "Overlay permission", if (overlay) "Granted" else "Not granted",
            if (overlay) Health.PASS else if (edgeEnabled) Health.FAILED else Health.WARNING,
            "SYSTEM_ALERT_WINDOW, required to draw the edge-lighting overlay.", e,
        )
        items += DiagnosticItem(
            "Edge lighting enabled", if (edgeEnabled) "On" else "Off",
            Health.INFO, "The user-facing toggle in Settings.", e,
        )
        items += DiagnosticItem(
            "Edge lighting service", if (edgeRunning) "Running" else "Stopped",
            if (edgeEnabled && overlay && !edgeRunning) Health.WARNING else Health.INFO,
            "Foreground overlay service state. Runs only while enabled.", e,
        )
        items += DiagnosticItem(
            "Edge playback synced",
            if (edgeRunning && s.phase != PlaybackPhase.STOPPED) "Yes" else "No",
            Health.INFO, "Whether the overlay is currently animating to live playback.", e,
        )
        items += DiagnosticItem(
            "Edge render", if (edgeRunning) "vsync-driven (~display refresh)" else "—",
            Health.INFO, "The overlay repaints via postInvalidateOnAnimation and idles when stopped.", e,
        )
        items += DiagnosticItem(
            "Edge color", "#%08X".format(edgeColor), Health.INFO,
            "Configured glow color (ARGB).", e,
        )

        // ---------------- System ----------------
        val sys = "System"
        val batteryOpt = isBatteryOptimized(ctx)
        items += DiagnosticItem(
            "Battery optimization",
            if (batteryOpt) "Optimized (default)" else "Exempt",
            if (batteryOpt) Health.WARNING else Health.PASS,
            if (batteryOpt) "The listener may be delayed in deep Doze. Exemption is optional."
            else "The app is exempt from battery optimization.", sys,
        )
        items += DiagnosticItem(
            "Accessibility service", "Not used", Health.INFO,
            "RVX Companion does not use accessibility — MediaSession makes it unnecessary.", sys,
        )
        items += DiagnosticItem(
            "Nothing device", if (isNothingDevice()) "Yes" else "No", Health.INFO,
            "Detected from Build.MANUFACTURER/BRAND.", sys,
        )
        items += DiagnosticItem(
            "Android version",
            "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})", Health.INFO,
            "Host operating system.", sys,
        )
        items += DiagnosticItem(
            "Companion version",
            "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})", Health.INFO,
            "This build of RVX Companion.", sys,
        )
        return items
    }
}
