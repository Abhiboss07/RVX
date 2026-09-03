package com.rvx.companion.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rvx.companion.RvxApp
import com.rvx.companion.core.formatClock
import com.rvx.companion.data.WatchStats
import com.rvx.companion.media.PlaybackPhase
import com.rvx.companion.media.PlaybackSnapshot

/** Privacy-first analytics dashboard. Every number is computed on-device from observed sessions. */
@Composable
fun DashboardScreen(modifier: Modifier = Modifier) {
    val app = RvxApp.instance
    val stats by app.watch.stats.collectAsStateWithLifecycle()
    val playback by app.playback.state.collectAsStateWithLifecycle()

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Watch analytics", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Computed locally. Nothing leaves your device.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        // Now-playing card
        NowPlayingCard(playback)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatTile("Today", formatDuration(stats.todayMs), Modifier.weight(1f))
            StatTile("This week", formatDuration(stats.weekMs), Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatTile("This month", formatDuration(stats.monthMs), Modifier.weight(1f))
            StatTile("Streak", "${stats.streakDays}d", Modifier.weight(1f))
        }

        InsightsRow(stats)
        WeeklyBars(stats)
        TopChannels(stats)
        TopApps(stats)

        if (stats.totalSessions == 0) {
            Text(
                "No sessions recorded yet. Grant notification access in Settings, then play " +
                    "something — your watch time appears here.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun NowPlayingCard(s: PlaybackSnapshot) {
    val title = s.title.ifBlank { "Nothing playing" }
    val subtitle = s.appLabel.ifBlank { "Start a video or track in any app" }
    val dotColor = when (s.phase) {
        PlaybackPhase.PLAYING -> MaterialTheme.colorScheme.primary
        PlaybackPhase.BUFFERING -> MaterialTheme.colorScheme.tertiary
        PlaybackPhase.PAUSED -> MaterialTheme.colorScheme.secondary
        PlaybackPhase.STOPPED -> MaterialTheme.colorScheme.outline
    }
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                val art = s.albumArt
                if (art != null) {
                    Image(
                        bitmap = art.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(56.dp).clip(RoundedCornerShape(10.dp)),
                    )
                } else {
                    Box(Modifier.size(12.dp).clip(RoundedCornerShape(6.dp)).background(dotColor))
                }
                Column(Modifier.weight(1f)) {
                    Text(
                        title, style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold, maxLines = 2,
                    )
                    Text(
                        "${s.phase.name.lowercase().replaceFirstChar { it.uppercase() }} · $subtitle" +
                            if (s.artist.isNotBlank()) " · ${s.artist}" else "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            val progress = s.progress
            if (progress != null && s.phase != PlaybackPhase.STOPPED) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(formatClock(s.positionMs), style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(formatClock(s.durationMs), style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun StatTile(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier) {
        Column(Modifier.padding(16.dp)) {
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun InsightsRow(stats: WatchStats) {
    val trend = stats.weekTrend
    Card(Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text("Daily average", style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(formatDuration(stats.dailyAvgMs), style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Week vs last", style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (trend == null) {
                    Text("—", style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    val pct = (trend * 100).toInt()
                    val up = pct >= 0
                    Text(
                        "${if (up) "▲" else "▼"} ${kotlin.math.abs(pct)}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (up) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.tertiary,
                    )
                }
            }
        }
    }
}

@Composable
private fun TopChannels(stats: WatchStats) {
    if (stats.topChannels.isEmpty()) return
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Top channels", style = MaterialTheme.typography.titleSmall)
            stats.topChannels.forEach { (name, ms) ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(name, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.width(8.dp))
                    Text(formatDuration(ms), style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun WeeklyBars(stats: WatchStats) {
    val max = (stats.last7Days.maxOrNull() ?: 1L).coerceAtLeast(1L)
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Last 7 days", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(12.dp))
            Row(
                Modifier.fillMaxWidth().height(80.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                stats.last7Days.forEach { v ->
                    val frac = (v.toFloat() / max).coerceIn(0.02f, 1f)
                    Box(
                        Modifier
                            .weight(1f)
                            .height((80 * frac).dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.primary),
                    )
                }
            }
        }
    }
}

@Composable
private fun TopApps(stats: WatchStats) {
    if (stats.topApps.isEmpty()) return
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Most watched", style = MaterialTheme.typography.titleSmall)
            stats.topApps.forEach { (app, ms) ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(app, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.width(8.dp))
                    Text(formatDuration(ms), style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
