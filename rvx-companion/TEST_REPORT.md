# RVX Companion — Test Report

Date: 2026-07-22. Covers Phases 2–8. Evidence is from an Android 14 emulator (API 34,
`sdk_gphone64_x86_64`, x86_64, headless) plus the in-app Test Mode and Diagnostics.

## Build

| Variant | Result | Notes |
|---|---|---|
| `:app:assembleDebug` | ✅ BUILD SUCCESSFUL | 0 warnings; app-debug.apk ~58 MB |
| `:app:assembleRelease` | ✅ (R8 + resource shrinking) | signed; see release APK in zip |

## Per-feature results

For each feature: implementation → how tested → evidence → limitations.

### Media detection (Phase 2)
- **Impl:** `MediaObserverService` (NLS) → `MediaSessionManager` → `PlaybackBus`.
- **Tested:** granted Notification access on emulator; observer bound a live system session
  (`com.google.android.googlequicksearchbox`) and Diagnostics flipped "Media session detected" to
  PASS.
- **Evidence:** `s_checks2.png` (0 FAIL after grant, real package shown).
- **Limitations:** live RVX title/progress needs a real device (KNOWN_LIMITATIONS #13).

### Play/pause/progress/duration/speed (Phase 2)
- **Impl:** `PlaybackSnapshot` maps PlaybackState → phase, position/duration/speed, interpolated
  `livePositionMs()`; Home card renders art + progress bar + times.
- **Tested:** Test Mode Play/Pause/Resume/Seek/Buffering/Completion assert bus reflects each; Home
  card verified rendering.
- **Evidence:** `s_test1.png` / `g_test_final.png` (8/8), `s_home.png`.

### Diagnostics (Phase 3)
- **Impl:** `Diagnostics.build()` pure function → grouped Detection/Glyph/Edge/System rows, each
  PASS/WARN/FAIL/INFO + explanation; recomputes on snapshot change and screen resume.
- **Tested:** pre-grant showed 2 FAIL (listener, session); post-grant 0 FAIL; grouped Glyph/Edge
  sections render with honest N/A on non-Nothing hardware.
- **Evidence:** `s_checks.png`, `s_checks2.png`, `g_glyph.png`, `g_edge.png`.

### Test Mode (Phase 4)
- **Impl:** `TestEngine` injects synthetic (Source.TEST) snapshots behind a bus test-mode gate;
  8 cases assert + time.
- **Tested:** ran twice (before and after Phase 6/7 + bug fix). **8 passed · 0 failed** both times.
- **Evidence:** `s_test1.png`, `g_test_final.png`, Dev event log.

### Developer Mode (added by request)
- **Impl:** live card (12 fields incl. latency, battery estimate, memory, device/OS, Glyph, edge) +
  event log (last 50) with Clear; ticks ~3/s for interpolation/memory.
- **Tested:** all fields populated; event log captured the full test-run timeline; Glyph shows
  "Disabled (no hardware)".
- **Evidence:** `s_dev.png`.

### Manual device checklist (Phase 5)
- **Deliverable:** `MANUAL_TEST_CHECKLIST.md` — 15 scenarios in the required format (how to test /
  expected / possible failure / debug location / recovery), covering detection, play/pause,
  progress, track change, latency, diagnostics, test mode, dev mode, edge lighting, Glyph, theming,
  battery, background/reconnect, screen off/on, orientation.

### Nothing Glyph (Phase 6)
- **Impl:** `GlyphController` (NoOp + Reflective) with observable `GlyphStatus`; capability
  detection; playback→animation mapping; 7 Glyph diagnostics rows (supported/permission/initialized/
  animation/synced/brightness/fps).
- **Tested:** non-Nothing emulator → every Glyph path inert, rows N/A/INFO, toggle greyed, **no
  crash**; event log shows `[glyph] Glyph SDK not present (.aar drop-in required)`.
- **Evidence:** `g_glyph.png`.
- **Limitations:** real light output needs Nothing's proprietary `.aar` (KNOWN_LIMITATIONS #10).

### Edge lighting (Phase 7)
- **Impl:** existing overlay service + 6 diagnostics rows (overlay perm/enabled/service/synced/
  render/color); `EdgeLightingService.running` StateFlow.
- **Tested:** rows render and reflect state (overlay not granted → WARN, service Stopped, color
  #FF4D6DFF); toggling behaviour covered by manual checklist #9.
- **Evidence:** `g_edge.png`.

### Final validation (Phase 8)
- **Deliverables:** this report + ARCHITECTURE.md, KNOWN_LIMITATIONS.md, BUG_REPORT.md,
  COMPATIBILITY_REPORT.md.

## Bugs found & fixed during testing

- **BUG-001** — MediaSession latency showed a misleading ~29 s on an idle session. Fixed (only
  report latency for fresh transitions); re-verified "—". See BUG_REPORT.md.

## Stability

No `FATAL EXCEPTION` for our package across the entire session (repeated logcat scans), across all
five tabs, Test Mode runs, permission grant, and reinstalls.

## Not proven on emulator (device tasks)

Live RVX playback (real title/channel/progress/latency), real Glyph output, the visible edge glow,
and lifecycle scenarios (screen off/on, orientation, background) — all in MANUAL_TEST_CHECKLIST.md.
