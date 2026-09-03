# RVX Companion — Bug Report

Bugs found during development/verification and their resolution. Empty of open bugs at release;
this records what was found and fixed so the history is auditable.

## Fixed

### BUG-001 — MediaSession latency reported a misleading ~29 s on an idle session
- **Severity:** Low (cosmetic/diagnostic accuracy; no functional impact).
- **Found:** Phase 6/7 emulator verification (2026-07-22). After granting Notification access, the
  Diagnostics "MediaSession latency" row showed **29275 ms (WARNING)** while merely bound to an
  idle system session (`com.google.android.googlequicksearchbox`).
- **Root cause:** Latency was computed as `now − PlaybackState.lastPositionUpdateTime` on every
  STATE callback. When binding to a session whose last state change was long ago, that gap is
  "time since the last change", not the session→observer observation delay we intend to report.
- **Fix:** `MediaObserverService` now only treats a STATE callback as measurable if its timestamp
  is within `FRESH_TRANSITION_MS` (3 s); otherwise latency is reported as unknown (`-1` → "—",
  INFO). Real transitions (pause/resume on active playback) still report the true small value.
- **Verified:** Rebuilt + reinstalled; the row now shows "—" (INFO) on the idle session, and Test
  Mode remained 8/8. Commit is in `media/MediaObserverService.kt`.

## Open

None.

## Notes / non-bugs (correct-by-design behaviours that can look like bugs)

- **"Video quality" is always WARNING "Not available."** Correct: resolution is not exposed by the
  MediaSession API to any third-party app. Not fixable and not a bug.
- **Duration/position WARN for live streams.** Correct: live sessions legitimately report no
  duration.
- **Glyph rows show N/A on non-Nothing devices.** Correct: graceful degradation; the feature is
  inactive, not broken.
- **MediaSession reconnect test ~270 ms** vs ~120 ms for others. Correct: it runs an extra
  lost-then-rebound sequence with a deliberate delay.
