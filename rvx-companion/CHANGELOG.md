# RVX Companion — Changelog

## v1.0.0 — 2026-07-05

First release. A standalone Kotlin + Jetpack Compose (Material 3) companion app that adds
device integrations and privacy-first analytics around the RVX/YouTube experience without
modifying YouTube.

### Added
- **Media-session observer** (`MediaObserverService`, a `NotificationListenerService`) that
  reads the active `MediaController` via `MediaSessionManager` and publishes a process-wide
  `PlaybackBus` (phase, app, title, progress).
- **Privacy-first watch analytics** (`WatchRepository`): on-device `WatchSession` records
  (JSON in filesDir, no network), aggregated into today/week/month, streak, last-7-days, and
  top-apps stats. One-tap clear.
- **Universal edge-lighting** (`EdgeLightingService`): a foreground overlay that animates a
  screen-edge glow by playback phase (breathing/ sweep/ dim/ fade).
- **Nothing Glyph integration** (`GlyphController`): capability-detected; reflective SDK
  backend on Nothing devices, safe no-op elsewhere. Playback→animation mapping wired; vendor
  `.aar` is a documented drop-in.
- **Material You + AMOLED theming**, **performance profiles**, and **a settings toggle for
  every feature** (`SettingsRepository` over DataStore).
- Debug + signed release builds; **R8 + resource shrinking** (release APK 1.9 MB).

### Security/privacy posture
- **No `INTERNET` permission declared** — the app cannot send data off-device.
- Permissions limited to media-session access (notification listener), overlay, foreground
  service, notifications, and optional flashlight.

### Known scope limits (see FEATURE_DOCUMENTATION.md)
- Cannot modify YouTube's own UI, add in-video bookmarks/PiP controls, or read YouTube's
  server-side watch history — Android exposes none of that to a third-party app. Those require
  ReVanced patches (separate project).
- Real Glyph light output requires adding Nothing's proprietary Glyph SDK `.aar`.
