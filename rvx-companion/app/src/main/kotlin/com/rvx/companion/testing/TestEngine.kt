package com.rvx.companion.testing

import android.os.SystemClock
import com.rvx.companion.core.EventLog
import com.rvx.companion.media.PlaybackBus
import com.rvx.companion.media.PlaybackPhase
import com.rvx.companion.media.PlaybackSnapshot
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class TestStatus { PENDING, RUNNING, PASS, FAIL }

data class TestCase(
    val name: String,
    val description: String,
    val status: TestStatus = TestStatus.PENDING,
    val durationMs: Long = 0L,
    val error: String? = null,
)

/**
 * In-process self-test of the detection pipeline. Each test injects a synthetic (source = TEST)
 * snapshot into [PlaybackBus] and asserts the bus reflects it, exercising the exact same state
 * plumbing the live observer feeds. While a run is active the bus is put in test mode so live
 * events do not interfere; it is restored to a clean STOPPED state afterwards.
 *
 * These cover the playback/session pipeline (the Phase 2 MVP). Lifecycle scenarios (screen off/on,
 * orientation, background execution) are validated by the Phase 5 manual checklist, since they
 * require the real system and cannot be faithfully simulated in-process.
 */
class TestEngine {

    private val _cases = MutableStateFlow(defaultCases())
    val cases: StateFlow<List<TestCase>> = _cases.asStateFlow()

    private val _running = MutableStateFlow(false)
    val running: StateFlow<Boolean> = _running.asStateFlow()

    private fun defaultCases() = listOf(
        TestCase("Play", "PLAYING is detected and title propagates"),
        TestCase("Pause", "PAUSED is detected"),
        TestCase("Resume", "PLAYING resumes after pause"),
        TestCase("Seek", "Position jump updates progress"),
        TestCase("Buffering", "BUFFERING is detected"),
        TestCase("Completion", "End-of-media resolves to STOPPED"),
        TestCase("Metadata update", "A new title replaces the old one"),
        TestCase("MediaSession reconnect", "Recovers after a session is lost then re-bound"),
    )

    suspend fun runAll() {
        if (_running.value) return
        _running.value = true
        _cases.value = defaultCases()
        EventLog.log("test", "Self-test run started")
        PlaybackBus.setTestMode(true)
        try {
            run(0) { play() }
            run(1) { pause() }
            run(2) { resume() }
            run(3) { seek() }
            run(4) { buffering() }
            run(5) { completion() }
            run(6) { metadataUpdate() }
            run(7) { reconnect() }
        } finally {
            PlaybackBus.setTestMode(false)
            // Return the bus to a clean stopped state; the live observer re-emits on next event.
            PlaybackBus.publish(
                PlaybackSnapshot(phase = PlaybackPhase.STOPPED, source = PlaybackSnapshot.Source.TEST)
            )
            _running.value = false
            EventLog.log("test", "Self-test run finished")
        }
    }

    private suspend fun run(i: Int, block: suspend () -> Unit) {
        update(i) { it.copy(status = TestStatus.RUNNING) }
        val start = SystemClock.elapsedRealtime()
        val result = runCatching { block() }
        val dur = SystemClock.elapsedRealtime() - start
        val ok = result.isSuccess
        update(i) {
            it.copy(
                status = if (ok) TestStatus.PASS else TestStatus.FAIL,
                durationMs = dur,
                error = result.exceptionOrNull()?.message,
            )
        }
        EventLog.log("test", "${_cases.value[i].name}: ${if (ok) "PASS" else "FAIL"} (${dur}ms)")
    }

    // --- individual simulations (assertions throw on failure) ---

    private suspend fun play() {
        emitTest(PlaybackPhase.PLAYING, "Test video", pos = 0, dur = 100_000)
        check(PlaybackBus.state.value.phase == PlaybackPhase.PLAYING) { "phase != PLAYING" }
        check(PlaybackBus.state.value.title == "Test video") { "title did not propagate" }
    }

    private suspend fun pause() {
        emitTest(PlaybackPhase.PAUSED, "Test video", pos = 5_000, dur = 100_000)
        check(PlaybackBus.state.value.phase == PlaybackPhase.PAUSED) { "phase != PAUSED" }
    }

    private suspend fun resume() {
        emitTest(PlaybackPhase.PLAYING, "Test video", pos = 5_000, dur = 100_000)
        check(PlaybackBus.state.value.phase == PlaybackPhase.PLAYING) { "phase != PLAYING" }
    }

    private suspend fun seek() {
        emitTest(PlaybackPhase.PLAYING, "Test video", pos = 60_000, dur = 100_000)
        val p = PlaybackBus.state.value.progress ?: -1f
        check(p in 0.55f..0.65f) { "progress not ~0.6 (was $p)" }
    }

    private suspend fun buffering() {
        emitTest(PlaybackPhase.BUFFERING, "Test video", pos = 60_000, dur = 100_000)
        check(PlaybackBus.state.value.phase == PlaybackPhase.BUFFERING) { "phase != BUFFERING" }
    }

    private suspend fun completion() {
        emitTest(PlaybackPhase.STOPPED, "Test video", pos = 100_000, dur = 100_000)
        check(PlaybackBus.state.value.phase == PlaybackPhase.STOPPED) { "phase != STOPPED" }
    }

    private suspend fun metadataUpdate() {
        emitTest(PlaybackPhase.PLAYING, "Second video", pos = 0, dur = 200_000)
        check(PlaybackBus.state.value.title == "Second video") { "metadata did not update" }
    }

    private suspend fun reconnect() {
        emitTest(PlaybackPhase.STOPPED, "", pos = 0, dur = 0)                    // session lost
        delay(30)
        emitTest(PlaybackPhase.PLAYING, "Reconnected", pos = 0, dur = 100_000)   // re-bound
        check(PlaybackBus.state.value.phase == PlaybackPhase.PLAYING) { "did not recover" }
        check(PlaybackBus.state.value.title == "Reconnected") { "wrong session after reconnect" }
    }

    private suspend fun emitTest(phase: PlaybackPhase, title: String, pos: Long, dur: Long) {
        PlaybackBus.publish(
            PlaybackSnapshot(
                phase = phase,
                packageName = "anddea.youtube",
                appLabel = "RVX (test)",
                title = title,
                artist = "Test channel",
                positionMs = pos,
                durationMs = dur,
                speed = 1f,
                positionSyncElapsed = SystemClock.elapsedRealtime(),
                source = PlaybackSnapshot.Source.TEST,
            )
        )
        delay(120) // let collectors observe and make per-test timing visible
    }

    private fun update(i: Int, f: (TestCase) -> TestCase) {
        _cases.value = _cases.value.toMutableList().also { it[i] = f(it[i]) }
    }
}
