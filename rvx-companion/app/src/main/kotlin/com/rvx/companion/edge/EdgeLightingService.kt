package com.rvx.companion.edge

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.graphics.RectF
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.rvx.companion.R
import com.rvx.companion.RvxApp
import com.rvx.companion.media.PlaybackPhase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.math.sin

/**
 * Universal edge-lighting: a full-screen, non-touchable overlay window that paints a soft
 * glow around the screen border and animates it in response to playback phase:
 *   PLAYING → gentle breathing pulse, BUFFERING → faster sweep, PAUSED → dim static,
 *   STOPPED → fades out. Works on any device with the "Display over other apps" permission,
 *   which is why it is the universal fallback for devices without Glyph hardware.
 */
class EdgeLightingService : Service() {

    private var windowManager: WindowManager? = null
    private var overlay: EdgeGlowView? = null
    private var scope: CoroutineScope? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!canDrawOverlay()) {
            stopSelf()
            return START_NOT_STICKY
        }
        startForeground(NOTIF_ID, buildNotification())
        if (overlay == null) attachOverlay()
        _running.value = overlay != null
        return START_STICKY
    }

    private fun attachOverlay() {
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val view = EdgeGlowView(this)
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT,
        ).apply { gravity = Gravity.TOP or Gravity.START }

        runCatching { wm.addView(view, params) }
        windowManager = wm
        overlay = view

        val app = RvxApp.instance
        scope = CoroutineScope(Dispatchers.Main).also { s ->
            app.playback.state.onEach { snap ->
                val s2 = app.settingsSnapshot
                val color = if (s2.edgeFollowsArt) snap.dominantColor ?: s2.edgeColor else s2.edgeColor
                view.setColor(color)
                view.setPhase(if (s2.edgeLightingEnabled) snap.phase else PlaybackPhase.STOPPED)
                // Glyph is driven globally from RvxApp, so no call is needed here.
            }.launchIn(s)
        }
    }

    override fun onDestroy() {
        scope?.let { (it.coroutineContext[Job])?.cancel() }
        overlay?.let { v -> runCatching { windowManager?.removeView(v) } }
        overlay = null
        _running.value = false
        super.onDestroy()
    }

    private fun canDrawOverlay(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this)

    private fun buildNotification(): Notification {
        val nm = getSystemService(NotificationManager::class.java)
        val ch = NotificationChannel(CHANNEL, "Edge lighting", NotificationManager.IMPORTANCE_MIN)
        ch.setShowBadge(false)
        nm.createNotificationChannel(ch)
        return Notification.Builder(this, CHANNEL)
            .setContentTitle("Edge lighting active")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .build()
    }

    companion object {
        private const val CHANNEL = "edge_lighting"
        private const val NOTIF_ID = 42

        private val _running = MutableStateFlow(false)

        /** Whether the overlay service is currently running (surfaced in Diagnostics/Developer). */
        val running: StateFlow<Boolean> = _running.asStateFlow()

        fun start(context: Context) {
            val i = Intent(context, EdgeLightingService::class.java)
            context.startForegroundService(i)
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, EdgeLightingService::class.java))
        }
    }
}

/** The actual drawing surface for the edge glow. Self-animating via postInvalidateOnAnimation. */
private class EdgeGlowView(context: Context) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }
    private var phase: PlaybackPhase = PlaybackPhase.STOPPED
    private var glowColor: Int = 0xFF4D6DFF.toInt()
    private var intensity = 0f            // eased 0..1 current visible strength
    private var target = 0f               // phase-driven target strength
    private var t = 0f                    // animation clock

    fun setPhase(p: PlaybackPhase) {
        phase = p
        target = when (p) {
            PlaybackPhase.PLAYING -> 1f
            PlaybackPhase.BUFFERING -> 0.9f
            PlaybackPhase.PAUSED -> 0.35f
            PlaybackPhase.STOPPED -> 0f
        }
        postInvalidateOnAnimation()
    }

    fun setColor(color: Int) {
        glowColor = color
        postInvalidateOnAnimation()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        // Ease intensity toward target so phase changes are smooth, not abrupt.
        intensity += (target - intensity) * 0.12f
        t += 0.05f

        val wave = when (phase) {
            PlaybackPhase.PLAYING -> 0.75f + 0.25f * sin(t.toDouble()).toFloat()   // breathing
            PlaybackPhase.BUFFERING -> 0.6f + 0.4f * sin(t.toDouble() * 3).toFloat() // faster
            else -> 1f
        }
        val strength = (intensity * wave).coerceIn(0f, 1f)

        if (strength > 0.01f) {
            val maxStroke = width * 0.06f
            val inset = maxStroke / 2f
            paint.strokeWidth = maxStroke * strength
            paint.color = glowColor
            paint.alpha = (200 * strength).toInt().coerceIn(0, 255)
            val r = width * 0.10f
            canvas.drawRoundRect(
                RectF(inset, inset, width - inset, height - inset), r, r, paint,
            )
            postInvalidateOnAnimation() // keep animating while visible
        }
    }
}
