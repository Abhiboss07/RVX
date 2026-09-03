# RVX Companion

A standalone **Kotlin + Jetpack Compose (Material 3)** app that adds premium device
integrations and privacy-first analytics *around* the RVX/YouTube experience — without
modifying YouTube and **without any network access** (no `INTERNET` permission).

It exists because YouTube's own UI can't be extended from outside; what Android *does* expose
is the active **media session**, which this app observes to drive everything below.

## Features

- **Privacy-first watch analytics** — today/week/month, streak, 7-day chart, top apps; all
  on-device, one-tap clear.
- **Universal edge lighting** — playback-synced screen-edge glow (any device with overlay
  permission).
- **Nothing Glyph** — capability-detected; maps playback to Glyph on Nothing phones, safe
  no-op elsewhere.
- **Material You + AMOLED**, **performance profiles**, **a toggle for every feature**.

## Build

```sh
export JAVA_HOME=/home/abhiboss/Projects/RVX/.toolchain/jdk-21.0.11+10
export ANDROID_HOME=/home/abhiboss/Projects/RVX/.toolchain/android-sdk
cd rvx-companion
./gradlew :app:assembleDebug      # → app/build/outputs/apk/debug/app-debug.apk (56 MB)
./gradlew :app:assembleRelease    # → app/build/outputs/apk/release/ (R8, 1.9 MB; sign before install)
```

## Install & use

1. Install the APK.
2. **Settings → Notification access → enable RVX Companion** (to read the media session).
3. For edge lighting: grant **Display over other apps**, toggle it on.
4. Play anything in any media app → the dashboard fills and the glow animates.

## Docs

- [FEATURE_DOCUMENTATION.md](FEATURE_DOCUMENTATION.md) — architecture, per-feature detail, Glyph drop-in
- [RELEASE_NOTES.md](RELEASE_NOTES.md) · [CHANGELOG.md](CHANGELOG.md)
- [TEST_REPORT.md](TEST_REPORT.md) — verified on Android 14 emulator
- [PERFORMANCE_REPORT.md](PERFORMANCE_REPORT.md)

## Honest scope

This app **cannot** add bookmarks/PiP-buttons inside YouTube, restyle YouTube's UI, or read
YouTube's server-side watch history — Android exposes none of that to a third-party app. Those
need ReVanced patches (a different project). This delivers the premium integrations that are
genuinely achievable from outside YouTube.
