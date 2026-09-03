# RVX Custom Video Quality & Watchdog Patches

This directory contains the patches and bytecode fixes implemented to eliminate the video quality freeze, view lockout, and format stalling in YouTube ReVanced Extended on Android 17 / SDK 37 devices (e.g. Nothing Phone 3).

## Key Fixes
1. **`AdvancedVideoQualityMenuPatch`**:
   - Injects `unblockViews(View)` on all flyout menu drawing passes to recursively force `setClickable(true)` and `setEnabled(true)`.
   - Prevents YouTube's native Litho UI from locking out the Quality item when buffering or transitioning streams.
   - Fixes the blank menu bug by gating quick-quality parent visibility hiding strictly on a successful `callOnClick()` return.
2. **`VideoQualityWatchdog`**:
   - Runs a 5-second watchdog timer on quality switch requests (`setCurrentQuality`).
   - Automatically cancels if format commits (`setVideoQuality`) or new video begins (`newVideoStarted`).
   - On timeout/stall, automatically resyncs UI label and calls `VideoUtils.reloadVideo()` to seamlessly reconnect the stream.
