package com.rvx.companion.ui

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rvx.companion.RvxApp
import com.rvx.companion.data.PerformanceProfile
import com.rvx.companion.data.SettingsState
import com.rvx.companion.edge.EdgeLightingService
import com.rvx.companion.glyph.GlyphEffect
import kotlinx.coroutines.launch

/** Every feature is a toggle here (project rule). Toggles map 1:1 to [SettingsState]. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    val app = RvxApp.instance
    val ctx = LocalContext.current
    val s: SettingsState by app.settings.state.collectAsStateWithLifecycle(initialValue = SettingsState())
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)

        SectionCard("Permissions") {
            ActionRow(
                "Notification access",
                "Required to read the active media session for analytics + integrations.",
            ) { ctx.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)) }
            HorizontalDivider()
            ActionRow(
                "Display over other apps",
                "Required for the universal edge-lighting overlay.",
            ) {
                ctx.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))
            }
        }

        SectionCard("Appearance") {
            ToggleRow("Material You (dynamic color)", "Use wallpaper colors on Android 12+.",
                s.dynamicColor) { scope.launch { app.settings.setDynamicColor(it) } }
            ToggleRow("AMOLED black theme", "True-black surfaces to save OLED power.",
                s.amoledTheme) { scope.launch { app.settings.setAmoled(it) } }
        }

        SectionCard("Device integrations") {
            ToggleRow(
                "Universal edge lighting",
                "Playback-synced glow around the screen. Works on any device.",
                s.edgeLightingEnabled,
            ) {
                scope.launch { app.settings.setEdgeLighting(it) }
                if (it) EdgeLightingService.start(ctx) else EdgeLightingService.stop(ctx)
            }
            ToggleRow(
                "Edge color follows album art",
                "Tint the glow to the current cover art (any app). Falls back to your color when no art.",
                s.edgeFollowsArt,
            ) { scope.launch { app.settings.setEdgeFollowsArt(it) } }
            ToggleRow(
                "Nothing Glyph",
                if (app.glyph.isSupported) "Sync the Glyph Matrix to playback."
                else "No Glyph hardware detected — safely inactive on this device.",
                s.glyphEnabled && app.glyph.isSupported,
                enabled = app.glyph.isSupported,
            ) { scope.launch { app.settings.setGlyph(it); app.glyph.setEnabled(it) } }
            if (app.glyph.isSupported && s.glyphEnabled) {
                Text(
                    "Glyph effect",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 4.dp),
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    GlyphEffect.entries.forEach { e ->
                        FilterChip(
                            selected = s.glyphEffect == e,
                            onClick = { scope.launch { app.settings.setGlyphEffect(e) } },
                            label = { Text(e.label) },
                        )
                    }
                }
            }
            ToggleRow(
                "Camera-flash notifications",
                "Blink the flash on key events. Optional.",
                s.flashNotificationsEnabled,
            ) { scope.launch { app.settings.setFlashNotifications(it) } }
        }

        SectionCard("Privacy") {
            ToggleRow(
                "Local watch analytics",
                "Record watch time on-device. Turn off to stop recording.",
                s.analyticsEnabled,
            ) { scope.launch { app.settings.setAnalytics(it) } }
            ActionRow("Clear analytics data", "Delete all recorded watch sessions.") {
                app.watch.clear()
            }
        }

        SectionCard("Performance mode") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PerformanceProfile.entries.forEach { p ->
                    FilterChip(
                        selected = s.performanceProfile == p,
                        onClick = { scope.launch { app.settings.setPerformanceProfile(p) } },
                        label = { Text(p.label) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            content()
        }
    }
}

@Composable
private fun ToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(Modifier.weight(1f).padding(end = 12.dp)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(subtitle, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange, enabled = enabled)
    }
}

@Composable
private fun ActionRow(title: String, subtitle: String, onClick: () -> Unit) {
    Column(
        Modifier.fillMaxWidth().clickable(onClick = onClick),
    ) {
        Text(title, style = MaterialTheme.typography.bodyLarge)
        Text(subtitle, style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
