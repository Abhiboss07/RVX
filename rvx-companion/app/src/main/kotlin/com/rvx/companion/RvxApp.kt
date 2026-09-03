package com.rvx.companion

import android.app.Application
import com.rvx.companion.data.SettingsRepository
import com.rvx.companion.data.SettingsState
import com.rvx.companion.data.WatchRepository
import com.rvx.companion.glyph.GlyphController
import com.rvx.companion.media.PlaybackBus
import com.rvx.companion.testing.TestEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Tiny hand-rolled service locator. The app is small enough that a DI framework would be
 * more ceremony than value; everything lives for the process lifetime.
 */
class RvxApp : Application() {

    lateinit var settings: SettingsRepository
        private set
    lateinit var watch: WatchRepository
        private set
    lateinit var glyph: GlyphController
        private set

    /** In-app self-test engine (Test Mode). Shared so UI observes one run state. */
    val testEngine = TestEngine()

    val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    /** Latest settings snapshot, kept current so services can read toggles without suspending. */
    @Volatile
    var settingsSnapshot: SettingsState = SettingsState()
        private set

    val playback: PlaybackBus = PlaybackBus

    override fun onCreate() {
        super.onCreate()
        instance = this
        settings = SettingsRepository(this)
        watch = WatchRepository(this)
        glyph = GlyphController.create(this)
        settings.state.onEach { settingsSnapshot = it }.launchIn(appScope)

        // Drive the Glyph Matrix from playback globally (not tied to the edge-lighting service),
        // gated by its toggle. On non-Nothing devices the controller is a no-op.
        settings.state.onEach {
            glyph.setEnabled(it.glyphEnabled)
            glyph.setEffect(it.glyphEffect)
        }.launchIn(appScope)
        PlaybackBus.state.onEach { if (settingsSnapshot.glyphEnabled) glyph.onPlayback(it) }
            .launchIn(appScope)
    }

    fun analyticsEnabledBlocking(): Boolean = settingsSnapshot.analyticsEnabled

    companion object {
        lateinit var instance: RvxApp
            private set
    }
}
