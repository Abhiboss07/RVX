# RVX Companion — Feature Documentation

A standalone Kotlin + Jetpack Compose (Material 3) app that adds device integrations and
privacy-first analytics **around** the RVX/YouTube experience, without modifying YouTube.

## Why a separate app (and what it can/can't do)

The patched YouTube is Google's closed-source app; you can't add screens or read its internal
watch data. What Android *does* expose to a well-behaved third-party app is the **active media
session** (`MediaSessionManager`). RVX Companion observes that session and drives integrations
from it.

**Can:** observe play/pause/buffering + track metadata for any media app (YouTube, RVX, music
players); record local watch analytics; drive Nothing Glyph and a universal edge-lighting
overlay; theme itself with Material You / AMOLED; expose a toggle per feature.

**Cannot:** put bookmarks/PiP-buttons inside YouTube's UI, restyle YouTube, or read YouTube's
server-side watch history. Those require ReVanced patches (a different project) and aren't
exposed by any Android API.

## Architecture

```
MediaObserverService (NotificationListenerService)
        │  observes MediaSessionManager → active MediaController
        ▼
   PlaybackBus  ── StateFlow<PlaybackSnapshot> (phase, app, title, progress)
    ├──► WatchRepository   → on-device WatchSession records (JSON in filesDir) → WatchStats
    ├──► EdgeLightingService → overlay glow animated by phase
    └──► GlyphController     → Nothing Glyph LEDs (capability-gated), else no-op
        ▲
   SettingsRepository (DataStore)  ── one toggle per feature, single source of truth
   MainActivity (Compose) ── Dashboard + Settings, RvxTheme (Material You / AMOLED)
```

- **No `INTERNET` permission is declared** — the app is provably incapable of network egress.
- Service locator is `RvxApp` (no DI framework; process-scoped singletons).

## Features & settings

| Feature | What it does | Setting(s) | Graceful degradation |
|---|---|---|---|
| **Watch analytics** | Records play intervals locally; dashboard shows today/week/month, streak, last-7-days bars, top apps | "Local watch analytics" toggle; "Clear analytics data" | Off = nothing recorded |
| **Universal edge lighting** | Overlay glow synced to playback (breathing when playing, faster when buffering, dim when paused) | "Universal edge lighting" toggle; edge color | Needs "Display over other apps"; if not granted, service self-stops |
| **Nothing Glyph** | Maps playback → Glyph animations on Nothing devices | "Nothing Glyph" toggle | Auto-disabled + greyed out on non-Nothing hardware |
| **Camera-flash notifications** | Optional flash blink on key events | "Camera-flash notifications" toggle | Off by default |
| **Material You / AMOLED** | Dynamic wallpaper palette (Android 12+); true-black OLED theme | "Material You", "AMOLED black theme" | Falls back to brand palette pre-12 |
| **Performance mode** | Battery Saver / Balanced / Performance profile | chip selector | Balanced default |

## Nothing Glyph — how the integration is wired (and how to fully activate it)

`GlyphController` is an interface with two implementations:

- `NoOpGlyphController` — returned on any non-Nothing device (detected via `Build.MANUFACTURER`
  / `BRAND`). Every call is inert, so nothing crashes.
- `ReflectiveGlyphController` — returned on Nothing devices. It talks to the official Glyph SDK
  (`com.nothing.ketchum.GlyphManager`) **reflectively**, so the app compiles and ships without
  the proprietary `.aar`. The playback→animation mapping (PLAYING→breathing, PAUSED→static,
  BUFFERING→circular) and event hooks are implemented; the concrete Glyph channel frames are
  the drop-in point.

**To enable real Glyph output:** obtain Nothing's Glyph Developer Kit, add the `KetchumSDK`
`.aar` to `app/libs` + `implementation(files("libs/KetchumSDK.aar"))`, register your app key,
and fill in the frame construction in `ReflectiveGlyphController.onPlayback/signal`. No other
code changes are needed — the rest of the app already routes playback events to it.

## Permissions requested (and why)

| Permission | Purpose |
|---|---|
| `BIND_NOTIFICATION_LISTENER_SERVICE` | The only sanctioned way to call `getActiveSessions` as a non-system app. We read media sessions, not notification content. User grants it in system settings. |
| `SYSTEM_ALERT_WINDOW` | Edge-lighting overlay. Feature-gated. |
| `FOREGROUND_SERVICE` / `..._SPECIAL_USE` | Keep the overlay engine alive while active. |
| `POST_NOTIFICATIONS` | The edge-lighting foreground-service notification. |
| `FLASHLIGHT` | Optional camera-flash notifications. |
| *(no `INTERNET`)* | Intentionally absent — no data leaves the device. |

## Compatibility

- minSdk 29 (Android 10) → targetSdk 34 (Android 14); compiles against SDK 34.
- Dynamic color activates on Android 12+; below that, the brand palette is used.
- Overlay type `TYPE_APPLICATION_OVERLAY` is the modern (O+) window type.
- All hardware-specific features (Glyph, flash) capability-check and degrade to no-op.
