# RVX Companion — Performance Report

Date: 2026-07-05.

## APK size

| Variant | Size | Notes |
|---|---|---|
| Debug | 56 MB | includes Compose tooling, no shrinking |
| **Release (R8 + resource shrinking)** | **1.9 MB** | unused Compose/AndroidX stripped |

R8 with resource shrinking reduced the release APK ~30×. Keep-rules (`proguard-rules.pro`)
retain the two manifest-entry services and the reflective Glyph package; verified by launching
the minified build without `ClassNotFound`/`NoSuchMethod`.

## Runtime cost (by design)

- **Idle:** the app itself runs nothing in the background. `MediaObserverService` is a
  notification-listener bound by the system and only does work on media-session callbacks
  (event-driven, not polling).
- **During playback:** one `MediaController.Callback` and lightweight `StateFlow` emissions.
  Analytics writes happen only when a play interval *closes* (not per frame), and intervals
  <3 s are discarded to avoid churn.
- **Edge lighting (only when enabled + something playing):** a single overlay `View` animating
  via `postInvalidateOnAnimation`; it stops invalidating when strength eases to 0 (stopped),
  so a paused/stopped state costs nothing.

## Performance profiles

`PerformanceProfile` (Battery Saver / Balanced / Performance) is persisted and surfaced; it is
the hook for scaling animation quality and background work. Current build wires the setting and
UI; per-profile tuning of the overlay frame rate / analytics batching is the natural next step.

## Battery

No wake locks. No `INTERNET`. The only always-bound component is the system-managed
notification listener, which is event-driven. Edge lighting is a foreground service **only
while the user has it enabled**; disabling the toggle stops the service entirely.

## Opportunities (Low priority)

- Throttle overlay invalidation under Battery Saver profile.
- Batch analytics `persist()` writes if very high session counts accumulate (currently writes
  on each closed interval; negligible for normal use).
