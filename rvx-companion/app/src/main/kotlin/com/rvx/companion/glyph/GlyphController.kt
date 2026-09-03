package com.rvx.companion.glyph

import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.os.Build
import com.nothing.ketchum.Common
import com.nothing.ketchum.Glyph
import com.nothing.ketchum.GlyphMatrixFrame
import com.nothing.ketchum.GlyphMatrixManager
import com.nothing.ketchum.GlyphMatrixObject
import com.rvx.companion.core.EventLog
import com.rvx.companion.media.PlaybackPhase
import com.rvx.companion.media.PlaybackSnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

/**
 * Selectable Glyph Matrix animation styles (applied while PLAYING). Persisted in settings and
 * chosen from the Settings screen. Buffering/paused/stopped states are handled uniformly.
 */
enum class GlyphEffect(val id: Int, val label: String) {
    NOW_PLAYING(0, "Now Playing"),
    BREATHING(1, "Breathing"),
    PULSE_RING(2, "Pulse ring"),
    PROGRESS_RING(3, "Progress ring"),
    EQUALIZER(4, "Equalizer"),
    ORBIT(5, "Orbit"),
    RIPPLE(6, "Ripple"),
    HEARTBEAT(7, "Heartbeat"),
    SPARKLE(8, "Sparkle");

    companion object {
        fun fromId(id: Int): GlyphEffect = entries.firstOrNull { it.id == id } ?: NOW_PLAYING
    }
}

/**
 * Live, observable status of the Glyph integration — surfaced in Diagnostics (Phase 6) and
 * Developer Mode. Every field is honestly reported: on a non-Nothing device everything is false;
 * on a Nothing Phone (3) it reflects the real Glyph Matrix connection/animation state.
 */
data class GlyphStatus(
    val supported: Boolean = false,
    val permissionGranted: Boolean = false,
    val initialized: Boolean = false,
    val enabled: Boolean = false,
    val animationActive: Boolean = false,
    val lastAnimation: String = "—",
    val brightness: Int = -1,   // 0..255 when animating, else -1 (unknown)
    val fps: Int = -1,          // animation update rate when animating, else -1
    val playbackSynced: Boolean = false,
)

/**
 * Abstraction over Nothing's Glyph hardware.
 *
 *  - [create] uses the SDK's own [Common.is23112] to detect a Nothing **Phone (3)** (the Glyph
 *    Matrix device this build targets). On that device it returns [GlyphMatrixController], which
 *    drives the real 25×25 Glyph Matrix via the official `glyph-matrix-sdk` .aar. On every other
 *    device it returns [NoOpGlyphController] — a safe no-op.
 */
interface GlyphController {
    val isSupported: Boolean
    val status: StateFlow<GlyphStatus>
    fun onPlayback(snapshot: PlaybackSnapshot)
    fun setEffect(effect: GlyphEffect)
    fun signal(event: GlyphEvent)
    fun setEnabled(enabled: Boolean)
    fun release()

    companion object {
        fun create(context: Context): GlyphController {
            val phone3 = runCatching { Common.is23112() }.getOrDefault(false)
            return if (phone3) GlyphMatrixController(context.applicationContext)
            else NoOpGlyphController
        }

        /** Best-effort brand check, used only for UI copy (not for driving hardware). */
        fun isNothingBrand(): Boolean =
            (Build.MANUFACTURER + " " + Build.BRAND).lowercase().contains("nothing")
    }
}

enum class GlyphEvent {
    DOWNLOAD_COMPLETE, PLAYLIST_COMPLETE, NEW_UPLOAD, LIVE_STARTED, LOGIN_OK, UPDATE_AVAILABLE
}

/** Every device without a supported Glyph Matrix uses this. All calls are inert. */
object NoOpGlyphController : GlyphController {
    override val isSupported = false
    private val _status = MutableStateFlow(GlyphStatus())
    override val status: StateFlow<GlyphStatus> = _status.asStateFlow()
    override fun onPlayback(snapshot: PlaybackSnapshot) {}
    override fun setEffect(effect: GlyphEffect) {}
    override fun signal(event: GlyphEvent) {}
    override fun setEnabled(enabled: Boolean) {}
    override fun release() {}
}

/**
 * Real Nothing **Phone (3)** controller driving the 25×25 Glyph Matrix through the official SDK
 * (`com.nothing.ketchum.GlyphMatrixManager`). App-based control (`setAppMatrixFrame`) so the
 * Matrix reacts to playback without the user selecting a Glyph Toy.
 *
 * While PLAYING, the selected [GlyphEffect] is rendered frame-by-frame (20 fps). BUFFERING shows a
 * rotating arc, PAUSED a dim disc, STOPPED clears the Matrix. Every SDK call is guarded.
 */
class GlyphMatrixController(private val context: Context) : GlyphController {

    override val isSupported = true

    private val _status = MutableStateFlow(GlyphStatus(supported = true, permissionGranted = hasPermission()))
    override val status: StateFlow<GlyphStatus> = _status.asStateFlow()

    private var manager: GlyphMatrixManager? = null
    @Volatile private var connected = false
    @Volatile private var enabled = true
    @Volatile private var phase = PlaybackPhase.STOPPED
    @Volatile private var progress = 0f
    @Volatile private var title = ""
    @Volatile private var effect = GlyphEffect.NOW_PLAYING

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var loop: Job? = null

    private val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE; style = Paint.Style.FILL }
    private val stroke = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE; style = Paint.Style.STROKE }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE; textSize = 12f; typeface = Typeface.DEFAULT_BOLD
    }
    private val disc: Bitmap by lazy { discBitmap() }
    private val arc: Bitmap by lazy { arcBitmap() }

    init { initManager() }

    private fun initManager() {
        runCatching {
            val m = GlyphMatrixManager.getInstance(context)
            val cb = object : GlyphMatrixManager.Callback {
                override fun onServiceConnected(name: ComponentName?) {
                    runCatching { m.register(Glyph.DEVICE_23112) }
                    connected = true
                    _status.value = _status.value.copy(initialized = true)
                    EventLog.log("glyph", "Glyph Matrix connected + registered (Phone 3)")
                    onPlayback(PlaybackSnapshot(phase = phase))
                }
                override fun onServiceDisconnected(name: ComponentName?) {
                    connected = false
                    _status.value = _status.value.copy(initialized = false, animationActive = false, playbackSynced = false)
                    EventLog.log("glyph", "Glyph Matrix disconnected")
                }
            }
            m.init(cb)
            manager = m
            EventLog.log("glyph", "Glyph Matrix init requested")
        }.onFailure { EventLog.log("glyph", "Glyph Matrix init failed: ${it.message}") }
    }

    override fun setEffect(effect: GlyphEffect) {
        if (this.effect == effect) return
        this.effect = effect
        EventLog.log("glyph", "Effect → ${effect.label}")
        _status.value = _status.value.copy(lastAnimation = effect.label)
        // Re-apply immediately so a change shows even while paused.
        if (enabled && connected) onPlayback(PlaybackSnapshot(phase = phase))
    }

    override fun onPlayback(snapshot: PlaybackSnapshot) {
        phase = snapshot.phase
        progress = snapshot.progress ?: progress
        if (snapshot.title.isNotBlank()) title = snapshot.title
        val active = enabled && snapshot.phase != PlaybackPhase.STOPPED
        _status.value = _status.value.copy(
            enabled = enabled,
            animationActive = active && connected,
            lastAnimation = if (snapshot.phase == PlaybackPhase.PLAYING) effect.label else phaseName(snapshot.phase),
            playbackSynced = active && connected,
            fps = if (active) FPS else -1,
        )
        if (!enabled || !connected) { stopLoop(); safeClear(); return }
        when (snapshot.phase) {
            PlaybackPhase.PLAYING, PlaybackPhase.BUFFERING -> startLoop()
            PlaybackPhase.PAUSED -> { stopLoop(); pushObject(disc, 45, 0) }   // static dim
            PlaybackPhase.STOPPED -> { stopLoop(); safeClear() }
        }
    }

    override fun signal(event: GlyphEvent) {
        _status.value = _status.value.copy(lastAnimation = "signal:${event.name.lowercase()}")
    }

    override fun setEnabled(enabled: Boolean) {
        this.enabled = enabled
        _status.value = _status.value.copy(enabled = enabled)
        if (!enabled) { stopLoop(); safeClear() }
        else onPlayback(PlaybackSnapshot(phase = phase))
    }

    override fun release() {
        stopLoop(); safeClear()
        runCatching { manager?.unInit() }
        manager = null; connected = false
    }

    // --- driver ---

    private fun startLoop() {
        if (loop?.isActive == true) return
        loop = scope.launch {
            var t = 0f
            while (isActive) {
                if (enabled && connected) pushFrame(t)
                t += 0.09f
                delay(FRAME_MS)
            }
        }
    }

    private fun stopLoop() { loop?.cancel(); loop = null }

    private fun pushFrame(t: Float) {
        when (phase) {
            PlaybackPhase.BUFFERING -> pushObject(arc, 220, ((t * 140f) % 360f).toInt())
            PlaybackPhase.PAUSED -> pushObject(disc, 45, 0)
            PlaybackPhase.STOPPED -> safeClear()
            PlaybackPhase.PLAYING -> {
                val (bmp, brightness) = renderEffect(t)
                pushObject(bmp, brightness, 0)
            }
        }
    }

    private fun pushObject(bmp: Bitmap, brightness: Int, orientation: Int) {
        val m = manager ?: return
        _status.value = _status.value.copy(brightness = brightness)
        val obj = GlyphMatrixObject.Builder()
            .setImageSource(bmp)
            .setBrightness(brightness)
            .setScale(100)
            .setOrientation(orientation)
            .setPosition(0, 0)
            .build()
        val frame: GlyphMatrixFrame = GlyphMatrixFrame.Builder().addTop(obj).build(context)
        val data = frame.render()
        runCatching { m.setAppMatrixFrame(data) }.onFailure { runCatching { m.setMatrixFrame(data) } }
    }

    private fun safeClear() {
        val m = manager ?: return
        runCatching { m.closeAppMatrix() }.onFailure { runCatching { m.turnOff() } }
        _status.value = _status.value.copy(animationActive = false, brightness = -1)
    }

    // --- effect renderers (each returns bitmap + object brightness) ---

    private fun renderEffect(t: Float): Pair<Bitmap, Int> {
        val bmp = Bitmap.createBitmap(SIZE, SIZE, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        val brightness = when (effect) {
            GlyphEffect.NOW_PLAYING -> { drawNowPlaying(c, t); 255 }
            GlyphEffect.BREATHING -> { c.drawCircle(CX, CY, RAD, fill); breathing(t) }
            GlyphEffect.PULSE_RING -> {
                stroke.strokeWidth = 3f
                c.drawCircle(CX, CY, RAD * (0.35f + 0.65f * osc(t)), stroke); 255
            }
            GlyphEffect.PROGRESS_RING -> {
                stroke.strokeWidth = 2f
                c.drawCircle(CX, CY, RAD, stroke)                       // faint full track
                stroke.strokeWidth = 4f
                c.drawArc(OVAL, -90f, 360f * progress.coerceIn(0f, 1f), false, stroke); 255
            }
            GlyphEffect.EQUALIZER -> { drawBars(c, t); 255 }
            GlyphEffect.ORBIT -> {
                val a = t * 2.2f
                c.drawCircle(CX + cos(a) * ORBIT_R, CY + sin(a) * ORBIT_R, 3.2f, fill)
                c.drawCircle(CX + cos(a - 0.5f) * ORBIT_R, CY + sin(a - 0.5f) * ORBIT_R, 2f, fill); 255
            }
            GlyphEffect.RIPPLE -> { drawRipple(c, t); 255 }
            GlyphEffect.HEARTBEAT -> { c.drawCircle(CX, CY, RAD, fill); heartbeat(t) }
            GlyphEffect.SPARKLE -> { drawSparkle(c, t); 255 }
        }
        return bmp to brightness
    }

    private fun drawNowPlaying(c: Canvas, t: Float) {
        // Upper area: the title, centered if it fits, otherwise scrolling. Bottom: a progress bar.
        val txt = title.ifBlank { "RVX" }.uppercase()
        textPaint.textSize = 12f
        val tw = textPaint.measureText(txt)
        val baseY = 15f
        if (tw <= SIZE - 1f) {
            c.drawText(txt, (SIZE - tw) / 2f, baseY, textPaint)
        } else {
            val gap = 10f
            val span = tw + gap
            val off = (t * 10f) % span
            c.drawText(txt, -off, baseY, textPaint)
            c.drawText(txt, -off + span, baseY, textPaint)  // wrap for a seamless loop
        }
        // Progress bar along the bottom rows.
        val pw = SIZE * progress.coerceIn(0f, 1f)
        c.drawRect(0f, SIZE - 4f, pw, SIZE - 2f, fill)
    }

    private fun drawBars(c: Canvas, t: Float) {
        val bars = 5; val bw = 3f
        val gap = (SIZE - bars * bw) / (bars + 1)
        for (i in 0 until bars) {
            val x = gap + i * (bw + gap)
            val amp = 0.5f + 0.5f * sin((t * 3f + i * 0.8f).toDouble()).toFloat()
            val h = SIZE * 0.25f + SIZE * 0.6f * amp
            c.drawRect(x, SIZE - 2f - h, x + bw, SIZE - 2f, fill)
        }
    }

    private fun drawRipple(c: Canvas, t: Float) {
        stroke.strokeWidth = 2f
        val span = RAD + 3f
        for (k in 0 until 2) {
            val r = ((t * 6f + k * span / 2f) % span)
            c.drawCircle(CX, CY, r, stroke)
        }
    }

    private fun drawSparkle(c: Canvas, t: Float) {
        // Deterministic pseudo-twinkle: light a rotating subset of points within the disc.
        val n = 14
        for (i in 0 until n) {
            val seed = i * 2.399963f  // golden-angle spread
            val rr = RAD * (0.2f + 0.75f * ((i * 7 % 11) / 11f))
            val ang = seed + t * (1f + (i % 3) * 0.6f)
            val on = (0.5f + 0.5f * sin((t * 4f + i).toDouble()).toFloat()) > 0.55f
            if (on) c.drawCircle(CX + cos(ang) * rr, CY + sin(ang) * rr, 1.6f, fill)
        }
    }

    private fun hasPermission(): Boolean = runCatching {
        context.checkSelfPermission("com.nothing.ketchum.permission.ENABLE") ==
            android.content.pm.PackageManager.PERMISSION_GRANTED
    }.getOrDefault(false)

    private fun phaseName(p: PlaybackPhase) = when (p) {
        PlaybackPhase.PLAYING -> effect.label
        PlaybackPhase.BUFFERING -> "sweep"
        PlaybackPhase.PAUSED -> "static"
        PlaybackPhase.STOPPED -> "off"
    }

    private fun osc(t: Float): Float = 0.5f + 0.5f * sin(t.toDouble()).toFloat()

    private fun breathing(t: Float): Int =
        (50 + 205 * (0.5 + 0.5 * sin(t.toDouble()))).toInt().coerceIn(0, 255)

    private fun heartbeat(t: Float): Int {
        // Two quick beats then a rest, looping (~1.6s cycle).
        val p = (t % 1.6f)
        val b = when {
            p < 0.12f -> p / 0.12f
            p < 0.30f -> 1f - (p - 0.12f) / 0.18f
            p < 0.42f -> (p - 0.30f) / 0.12f
            p < 0.60f -> 1f - (p - 0.42f) / 0.18f
            else -> 0.12f
        }
        return (40 + 215 * b).toInt().coerceIn(0, 255)
    }

    private fun discBitmap(): Bitmap {
        val bmp = Bitmap.createBitmap(SIZE, SIZE, Bitmap.Config.ARGB_8888)
        Canvas(bmp).drawCircle(CX, CY, RAD, fill); return bmp
    }

    private fun arcBitmap(): Bitmap {
        val bmp = Bitmap.createBitmap(SIZE, SIZE, Bitmap.Config.ARGB_8888)
        val p = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE; style = Paint.Style.STROKE; strokeWidth = 3.5f }
        Canvas(bmp).drawArc(RectF(2.5f, 2.5f, SIZE - 2.5f, SIZE - 2.5f), -90f, 110f, false, p); return bmp
    }

    private companion object {
        const val SIZE = 25
        const val CX = 12.5f
        const val CY = 12.5f
        const val RAD = 11.5f
        const val ORBIT_R = 8.5f
        const val FRAME_MS = 50L
        const val FPS = 20
        val OVAL = RectF(1.5f, 1.5f, SIZE - 1.5f, SIZE - 1.5f)
    }
}
