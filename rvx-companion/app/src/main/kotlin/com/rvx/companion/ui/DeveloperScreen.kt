package com.rvx.companion.ui

import android.os.Build
import android.os.SystemClock
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rvx.companion.RvxApp
import com.rvx.companion.core.EventLog
import com.rvx.companion.core.formatClock
import com.rvx.companion.data.SettingsState
import com.rvx.companion.edge.EdgeLightingService
import com.rvx.companion.media.PlaybackPhase
import com.rvx.companion.media.PlaybackSnapshot
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Developer Mode: live instrumentation for building and debugging. Not aimed at normal users —
 * it exposes the raw playback snapshot, session latency, memory, device facts and the last
 * [EventLog.MAX] events. Ticks a few times a second for the interpolated position and memory.
 */
@Composable
fun DeveloperScreen(modifier: Modifier = Modifier) {
    val app = RvxApp.instance
    val playback by app.playback.state.collectAsStateWithLifecycle()
    val events by EventLog.entries.collectAsStateWithLifecycle()
    val edgeRunning by EdgeLightingService.running.collectAsStateWithLifecycle()
    val settings: SettingsState by app.settings.state.collectAsStateWithLifecycle(initialValue = SettingsState())

    var tick by remember { mutableLongStateOf(SystemClock.elapsedRealtime()) }
    LaunchedEffect(Unit) {
        while (true) {
            tick = SystemClock.elapsedRealtime()
            delay(300)
        }
    }

    val livePos = playback.livePositionMs(tick)
    val usedMb = remember(tick) {
        val rt = Runtime.getRuntime()
        (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024)
    }
    val timeFmt = remember { SimpleDateFormat("HH:mm:ss", Locale.US) }

    Column(
        modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text("Developer Mode", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Live instrumentation for debugging. ${if (app.playback.isTestMode) "⚠ Test Mode active — showing synthetic events." else ""}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                DevRow("🎵 Active package", playback.packageName.ifBlank { "—" })
                DevRow("▶️ Playback state", playback.phase.name)
                DevRow(
                    "⏱ Position",
                    if (playback.durationMs > 0) "${formatClock(livePos)} / ${formatClock(playback.durationMs)}"
                    else formatClock(livePos),
                )
                DevRow("📺 Title", playback.title.ifBlank { "—" })
                DevRow("👤 Channel", playback.artist.ifBlank { "—" })
                DevRow("⚡ MediaSession latency", if (playback.latencyMs >= 0) "${playback.latencyMs} ms" else "—")
                DevRow("🔋 Battery impact (est.)", batteryEstimate(playback, edgeRunning))
                DevRow("💾 Memory (used heap)", "$usedMb MB")
                DevRow("📱 Device", "${Build.MANUFACTURER} ${Build.MODEL}")
                DevRow("🤖 Android", "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
                DevRow("💡 Glyph", glyphStatus(app.glyph.isSupported, settings.glyphEnabled))
                DevRow("🌈 Edge lighting", if (edgeRunning) "Running" else "Stopped")
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                "📝 Event log (last ${EventLog.MAX})",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            OutlinedButton(onClick = { EventLog.clear() }) { Text("Clear") }
        }

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp)) {
                if (events.isEmpty()) {
                    Text(
                        "No events yet. Interact with a media session or run Test Mode.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    events.forEachIndexed { i, e ->
                        if (i > 0) HorizontalDivider()
                        Row(
                            Modifier.padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                timeFmt.format(Date(e.wallClock)),
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                "[${e.tag}]",
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Text(
                                e.message,
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DevRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun batteryEstimate(s: PlaybackSnapshot, edgeRunning: Boolean): String = when {
    edgeRunning -> "Low — overlay foreground active"
    s.phase == PlaybackPhase.PLAYING -> "Minimal — event-driven observer"
    else -> "Negligible — idle"
}

private fun glyphStatus(supported: Boolean, enabled: Boolean): String = when {
    !supported -> "Disabled (no hardware)"
    enabled -> "Supported · enabled"
    else -> "Supported · off"
}
