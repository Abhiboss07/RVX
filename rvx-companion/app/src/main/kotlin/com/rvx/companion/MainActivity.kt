package com.rvx.companion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rvx.companion.data.SettingsState
import com.rvx.companion.ui.DashboardScreen
import com.rvx.companion.ui.DeveloperScreen
import com.rvx.companion.ui.DiagnosticsScreen
import com.rvx.companion.ui.SettingsScreen
import com.rvx.companion.ui.TestModeScreen
import com.rvx.companion.ui.theme.RvxTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val app = RvxApp.instance
            val settings: SettingsState by app.settings.state
                .collectAsStateWithLifecycle(initialValue = SettingsState())
            RvxTheme(dynamicColor = settings.dynamicColor, amoled = settings.amoledTheme) {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    RootScaffold()
                }
            }
        }
    }
}

private enum class Tab(val label: String, val icon: ImageVector) {
    DASHBOARD("Home", Icons.Filled.Insights),
    DIAGNOSTICS("Checks", Icons.Filled.HealthAndSafety),
    TEST("Test", Icons.Filled.Science),
    DEVELOPER("Dev", Icons.Filled.Terminal),
    SETTINGS("Settings", Icons.Filled.Settings),
}

@Composable
private fun RootScaffold() {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    val dashScroll = rememberScrollState()
    val settingsScroll = rememberScrollState()
    Scaffold(
        bottomBar = {
            NavigationBar {
                Tab.entries.forEachIndexed { i, t ->
                    NavigationBarItem(
                        selected = tab == i,
                        onClick = { tab = i },
                        icon = { Icon(t.icon, contentDescription = t.label) },
                        label = { Text(t.label) },
                    )
                }
            }
        },
    ) { inner ->
        val base = Modifier.padding(inner)
        when (Tab.entries[tab]) {
            // Dashboard/Settings are simple columns and take an outer scroll; the other three
            // manage their own scrolling internally (to avoid nested-scroll conflicts).
            Tab.DASHBOARD -> DashboardScreen(base.verticalScroll(dashScroll))
            Tab.DIAGNOSTICS -> DiagnosticsScreen(base.fillMaxSize())
            Tab.TEST -> TestModeScreen(base.fillMaxSize())
            Tab.DEVELOPER -> DeveloperScreen(base.fillMaxSize())
            Tab.SETTINGS -> SettingsScreen(base.verticalScroll(settingsScroll))
        }
    }
}
