package com.rvx.companion.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rvx.companion.RvxApp
import com.rvx.companion.diagnostics.Health
import com.rvx.companion.testing.TestCase
import com.rvx.companion.testing.TestStatus
import kotlinx.coroutines.launch

/**
 * Phase 4 (MVP scope): a built-in self-test that drives the detection pipeline with synthetic
 * playback events and reports PASS/FAIL + execution time per case. Lifecycle scenarios (screen
 * off/on, orientation, background) are in the Phase 5 manual checklist.
 */
@Composable
fun TestModeScreen(modifier: Modifier = Modifier) {
    val app = RvxApp.instance
    val engine = app.testEngine
    val cases by engine.cases.collectAsStateWithLifecycle()
    val running by engine.running.collectAsStateWithLifecycle()

    val passed = cases.count { it.status == TestStatus.PASS }
    val failed = cases.count { it.status == TestStatus.FAIL }

    Column(
        modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text("Test Mode", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Injects synthetic playback events and verifies the pipeline end-to-end. " +
                "No real playback or permissions required.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { app.appScope.launch { engine.runAll() } },
                enabled = !running,
            ) { Text(if (running) "Running…" else "Run all tests") }
            if (running) CircularProgressIndicator(Modifier.size(20.dp))
            else if (passed + failed > 0) {
                Text(
                    "$passed passed · $failed failed",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (failed == 0) healthColor(Health.PASS) else healthColor(Health.FAILED),
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        cases.forEach { TestRow(it) }

        Text(
            "Lifecycle tests — screen off/on, orientation changes, background execution, and " +
                "MediaSession reconnect on a real device — are covered by the Phase 5 manual checklist.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
private fun TestRow(case: TestCase) {
    Card(Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatusChip(case.status)
            Column(Modifier.weight(1f)) {
                Text(case.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(
                    case.error ?: case.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (case.error != null) healthColor(Health.FAILED)
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (case.status == TestStatus.PASS || case.status == TestStatus.FAIL) {
                Text(
                    "${case.durationMs} ms",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun StatusChip(status: TestStatus) {
    val (label, color) = when (status) {
        TestStatus.PENDING -> "…" to Color(0xFF5B6470)
        TestStatus.RUNNING -> "RUN" to Color(0xFF1565C0)
        TestStatus.PASS -> "PASS" to healthColor(Health.PASS)
        TestStatus.FAIL -> "FAIL" to healthColor(Health.FAILED)
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
