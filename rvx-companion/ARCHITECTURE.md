# RVX Companion — Architecture

A standalone Kotlin + Jetpack Compose (Material 3) app that adds device integrations and
privacy-first observation *around* RVX/YouTube using only supported Android APIs. No network
access, no root, no modification of YouTube.

## The constraint that shapes everything

There is **no direct channel** between RVX (`anddea.youtube`) and Companion. RVX exposes no
binder, intent API, or broadcast to us. The only link is the **system MediaSession** YouTube
publishes to Android, which we read (one-way, read-only) through `MediaSessionManager`, gated by a
`NotificationListenerService`. Every "communication" feature is really *YouTube → Android
MediaSession → us*. We observe; we never control YouTube.

## Module / package structure

Single `:app` module, strict package layering (one-way deps: `ui → domain/data → media/hardware`):

```
com.rvx.companion
├── RvxApp.kt            Application + hand-rolled service locator (settings, watch, glyph, testEngine)
├── MainActivity.kt      Compose host, 5-tab NavigationBar
├── core/                EventLog (ring buffer), Format (clock/speed helpers)
├── media/               PlaybackBus + PlaybackSnapshot, MediaObserverService (NLS)
├── data/                SettingsRepository (DataStore), WatchRepository (JSON analytics)
├── diagnostics/         Diagnostics (pure health builder), DiagnosticItem, Health
├── testing/             TestEngine (in-process self-test)
├── glyph/               GlyphController (interface + NoOp + Reflective), GlyphStatus
├── edge/                EdgeLightingService + EdgeGlowView (overlay)
└── ui/                  theme/, Dashboard, Diagnostics, TestMode, Developer, Settings
```

## Data flow

```
YouTube (anddea.youtube)
   │ publishes MediaSession
   ▼
MediaSessionManager ──auth via NLS──▶ MediaObserverService
   │ Callback: state / metadata / progress   ├─▶ EventLog (last 50)
   ▼                                          └─▶ WatchRepository (analytics)
PlaybackBus  (StateFlow<PlaybackSnapshot>)
   ├─▶ Dashboard (now-playing + analytics)
   ├─▶ Diagnostics (live PASS/WARN/FAIL)
   ├─▶ Developer Mode (raw fields + event log)
   ├─▶ EdgeLightingService → overlay glow
   └─▶ GlyphController → Nothing Glyph (or no-op) → GlyphStatus

TestEngine ──synthetic snapshots (Source.TEST)──▶ PlaybackBus   [Test Mode gate]
```

## Key components

- **PlaybackSnapshot** — immutable state: phase, package, title, artist, position/duration, speed,
  album art, a position/elapsed sync baseline for `livePositionMs()` interpolation, measured
  session→observer latency, and a `source` (LIVE/TEST).
- **PlaybackBus** — process-wide hot `StateFlow`. A test-mode gate drops LIVE publishes so the test
  engine can drive the pipeline in isolation.
- **MediaObserverService** — a `NotificationListenerService` used purely as the authorization
  gateway to `MediaSessionManager` (we never read notification content). Picks the active
  controller, maps `PlaybackState`/`MediaMetadata`, computes latency, requests rebind on death.
- **Diagnostics** — a pure function of the snapshot + a few permission probes, grouped into
  Detection / Glyph / Edge lighting / System. Recomputed on snapshot change and screen resume.
- **TestEngine** — 8 in-process tests asserting the bus reflects injected events; reports
  PASS/FAIL + timing.
- **GlyphController** — capability-detected. `NoOpGlyphController` off Nothing devices;
  `ReflectiveGlyphController` talks to `com.nothing.ketchum` reflectively (real output needs the
  proprietary `.aar`). All state mirrored into an observable `GlyphStatus`.
- **EdgeLightingService** — foreground `specialUse` overlay; a custom `View` animates a
  playback-reactive glow via `postInvalidateOnAnimation` and idles at zero strength.

## Permissions

| Permission | Why |
|---|---|
| `BIND_NOTIFICATION_LISTENER_SERVICE` + user grant | Authorizes `getActiveSessions` |
| `SYSTEM_ALERT_WINDOW` | Edge-lighting overlay (optional, toggle-gated) |
| `FOREGROUND_SERVICE` + `_SPECIAL_USE` | Overlay service on Android 14+ |
| `POST_NOTIFICATIONS` | FGS notification on Android 13+ |
| `FLASHLIGHT` | Optional camera-flash notifications |
| **no `INTERNET`** | Privacy guarantee — the app cannot send data off-device |
| **no Accessibility** | MediaSession makes it unnecessary |

## Storage

DataStore (Preferences) for settings (one key per toggle, single `SettingsState` flow).
On-device JSON in `filesDir` for watch analytics — no database dependency, no network.

## Battery strategy

Event-driven only (MediaController callbacks; no polling, no wakelocks). The overlay foreground
service runs *only* while enabled and something is playing, and is torn down otherwise. Position is
interpolated on-frame in the UI, never on a background clock. Diagnostics surfaces battery-opt
status but never nags.

## Error handling

Crash-free optional-feature contract: every hardware/overlay/Glyph path is capability-checked, then
`runCatching`-guarded, then degrades with a Diagnostics entry — nothing propagates. `SecurityException`
on `getActiveSessions`, session death, and listener disconnect are all handled (rebind requested).

## Android 10–16 compatibility

`minSdk 29` (Android 10). API-specific calls guarded by `Build.VERSION.SDK_INT`. See
COMPATIBILITY_REPORT.md for the per-version matrix.
