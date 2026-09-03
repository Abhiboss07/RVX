# RVX Companion — Release Notes

## v1.0.0 (2026-07-05) — first release

RVX Companion is a standalone, privacy-first companion app for the RVX/YouTube experience. It
observes the currently-playing media session and turns it into on-device analytics and device
integrations — **without touching YouTube and without any network access** (the app declares no
`INTERNET` permission).

### Highlights

- **Privacy-first watch analytics.** Today / week / month watch time, viewing streak, a
  last-7-days bar chart, and your most-watched apps — all computed on-device from observed
  media sessions. Nothing is uploaded. One tap clears all data.
- **Universal edge lighting.** A playback-synced glow around the screen edges that breathes
  while playing, sweeps faster while buffering, and dims when paused. Works on any device with
  the "Display over other apps" permission.
- **Nothing Glyph integration.** On Nothing phones, playback maps to Glyph animations; on every
  other device the feature auto-disables cleanly. (Real Glyph output activates when the official
  Glyph SDK `.aar` is dropped in — see FEATURE_DOCUMENTATION.md.)
- **Material You + AMOLED.** Wallpaper-derived dynamic color on Android 12+, plus a true-black
  AMOLED theme.
- **Performance profiles** (Battery Saver / Balanced / Performance) and a **toggle for every
  feature**.

### Scope, stated honestly

This app cannot add features *inside* YouTube's own screens (bookmarks, PiP buttons, UI
restyling) or read YouTube's server-side watch history — Android exposes none of that to a
third-party app. Those belong to ReVanced patches, a separate project. What's here is the set
of premium integrations that are genuinely achievable from outside YouTube, built and verified
end to end.

### Compatibility

Android 10–14 (minSdk 29 / targetSdk 34). Hardware features (Glyph, camera flash) capability-
check and degrade gracefully. No root required.

### Install

1. Install `app-debug.apk` (or the signed release APK).
2. Open **Settings → Permissions → Notification access** and enable RVX Companion (needed to
   read the media session).
3. For edge lighting, grant **Display over other apps** and toggle it on.
4. Play something in any media app; watch the dashboard populate.
