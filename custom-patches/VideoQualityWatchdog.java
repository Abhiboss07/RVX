package app.morphe.extension.youtube.patches.video;

import android.os.Handler;
import android.os.Looper;
import app.morphe.extension.shared.utils.Logger;
import app.morphe.extension.youtube.utils.VideoUtils;
import com.google.android.libraries.youtube.innertube.model.media.VideoQuality;

public class VideoQualityWatchdog {
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
    private static Runnable watchdogRunnable = null;
    private static int pendingResolution = -1;

    public static synchronized void startWatchdog(final int targetResolution) {
        cancelWatchdog();
        pendingResolution = targetResolution;

        watchdogRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Logger.printDebug(() -> "VideoQualityWatchdog: Quality switch to " + targetResolution + "p timed out (5s)");
                    
                    VideoQuality current = VideoQualityPatch.getCurrentQuality();
                    if (current != null) {
                        VideoQualityPatch.updateQualityString(current.patch_getQualityName());
                    }

                    Logger.printDebug(() -> "VideoQualityWatchdog: Triggering player reload to recover stream");
                    VideoUtils.reloadVideo();
                } catch (Exception ex) {
                    Logger.printException(() -> "VideoQualityWatchdog execution failed", ex);
                } finally {
                    pendingResolution = -1;
                    watchdogRunnable = null;
                }
            }
        };

        mainHandler.postDelayed(watchdogRunnable, 5000L);
        Logger.printDebug(() -> "VideoQualityWatchdog: Started for " + targetResolution + "p (timeout 5s)");
    }

    public static synchronized void notifyQualityChanged(int newResolution) {
        if (watchdogRunnable != null) {
            Logger.printDebug(() -> "VideoQualityWatchdog: Successfully reached " + newResolution + "p, canceling watchdog");
            cancelWatchdog();
        }
    }

    public static synchronized void cancelWatchdog() {
        if (watchdogRunnable != null) {
            mainHandler.removeCallbacks(watchdogRunnable);
            watchdogRunnable = null;
        }
        pendingResolution = -1;
    }
}
