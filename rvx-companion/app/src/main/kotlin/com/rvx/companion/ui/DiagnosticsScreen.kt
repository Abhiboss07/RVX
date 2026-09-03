package com.rvx.companion.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rvx.companion.RvxApp
import com.rvx.companion.data.SettingsState
import com.rvx.companion.diagnostics.Diagnostics
import com.rvx.companion.diagnostics.DiagnosticItem
import com.rvx.companion.diagnostics.Health
import com.rvx.companion.edge.EdgeLightingService

/** Phase 3: live health of every detection dependency, each row PASS / WARNING / FAILED / INFO. */
@Composable
fun DiagnosticsScreen(modifier: Modifier = Modifier) {
    val app = RvxApp.instance
    val ctx = LocalContext.current
    val playback by app.playback.state.collectAsStateWithLifecycle()
    val edgeRunning by EdgeLightingService.running.collectAsStateWithLifecycle()
    val glyph by app.glyph.status.collectAsStateWithLifecycle()
    val settings: SettingsState by app.settings.state.collectAsStateWithLifecycle(initialValue = SettingsState())

    // Permission-derived rows can change while we're away; recompute on each resume.
    var refreshKey by remember { mutableIntStateOf(0) }
    val owner = LocalLifecycleOwner.current
    DisposableEffect(owner) {
        val obs = LifecycleEventObserver { _, e ->
            if (e == Lifecycle.Event.ON_RESUME) refreshKey++
        }
        owner.lifecycle.addObserver(obs)
        onDispose { owner.lifecycle.removeObserver(obs) }
    }

    val items = remember(playback, edgeRunning, glyph, settings, refreshKey) {
        Diagnostics.build(
            ctx, playback, glyph, edgeRunning,
            edgeEnabled = settings.edgeLightingEnabled, edgeColor = settings.edgeColor,
        )
    }
    val grouped = remember(items) { items.groupBy { it.group } }

    Column(
        modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text("Diagnostics", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Live health of every detection dependency. Return here anytime to re-check.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        SummaryRow(
            pass = items.count { it.health == Health.PASS },
            warn = items.count { it.health == Health.WARNING },
            fail = items.count { it.health == Health.FAILED },
        )
        grouped.forEach { (group, rows) ->
            Text(
                group,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 6.dp),
            )
            rows.forEach { DiagnosticRow(it) }
        }
    }
}

@Composable
private fun SummaryRow(pass: Int, warn: Int, fail: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Pill("$pass PASS", healthColor(Health.PASS))
        Pill("$warn WARN", healthColor(Health.WARNING))
        Pill("$fail FAIL", healthColor(Health.FAILED))
    }
}

@Composable
private fun Pill(text: String, color: Color) {
    Box(
        Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.16f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge, color = color, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun DiagnosticRow(item: DiagnosticItem) {
    Card(Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatusBadge(item.health)
            Column(Modifier.weight(1f)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(item.label, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                    Text(
                        item.value,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    item.explanation,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(health: Health) {
    val color = healthColor(health)
    val label = when (health) {
        Health.PASS -> "PASS"
        Health.WARNING -> "WARN"
        Health.FAILED -> "FAIL"
        Health.INFO -> "INFO"
    }
    Box(
        Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.18f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Bold)
    }
}

/** Fixed status colors (independent of theme) so PASS/WARN/FAIL read unambiguously. */
internal fun healthColor(health: Health): Color = when (health) {
    Health.PASS -> Color(0xFF2E7D32)
    Health.WARNING -> Color(0xFFB26A00)
    Health.FAILED -> Color(0xFFC62828)
    Health.INFO -> Color(0xFF5B6470)
}
