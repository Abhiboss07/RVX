# RVX — Build & Integration Test Report

Date: 2026-07-05 · Host: Linux (CachyOS), non-root build · Toolchain: `.toolchain/`
(Temurin JDK 21.0.11, Android SDK platform-36 / build-tools 34.0.0).

## 1. Build results

| Component | Command | Result | Artifact |
|---|---|---|---|
| MicroG-RE (GmsCore) | `gradlew :play-services-core:assembleDefaultRelease` | ✅ exit 0 | `microg-release-6.1.4.apk` (13 MB) → signed `microg-release-signed.apk` |
| Patched YouTube | `./build.sh config-rvx.toml` | ✅ exit 0 | `youtube-revanced-extended-v20.51.39-all.apk` (115 MB) |

MicroG build emitted only benign `-Xlint:deprecation/unchecked` notes — no errors, no
warnings that break the build. The patcher applied the full RVX patch set (log below).

## 2. Package identity

| Property | MicroG-RE | Patched YouTube |
|---|---|---|
| Package name | `app.revanced.android.gms` | `anddea.youtube` |
| Version | 6.1.4 (vc 255034004) | 20.51.39 (vc 1558707648) |
| compileSdk | 36 | 36 |
| Signer SHA-256 | `637c226c67aec0cdbc6f49cd476d5247f999122606286273e16233a913a088b4` | **same** `637c226c…` |
| APK signature verifies | ✅ | ✅ |

Both APKs verified with `apksigner verify --print-certs`. Shared signer = updates install
over each other cleanly.

## 3. Integration contract (the load-bearing check)

The patched YouTube must resolve its Google-services calls to the GmsCore vendor package
`app.revanced.android.gms`. Confirmed from **both** sides:

**MicroG-RE provides:**
- `aapt2 dump packagename` → `app.revanced.android.gms`
- authenticator account-type string resource → `app.revanced`
- GMS start-package string resource → `app.revanced.android.gms`

**Patched YouTube consumes:**
- `uses-permission app.revanced.android.gms.permission.AD_ID`
- `uses-permission app.revanced.android.gms.permission.AD_ID_NOTIFICATION`
- `uses-permission app.revanced.android.providers.gsf.permission.READ_GSERVICES`
- manifest references to `app.revanced.android.gms` and `app.revanced.MICROG`

→ The permissions the patched YouTube requests are exactly the ones MicroG-RE defines, and
the vendor package it binds to is exactly the one MicroG-RE ships. **Integration verified.**

## 4. Patches applied (from patcher log)

Overlay buttons · Player components · Remove background playback restrictions · Remove
viewer discretion dialog · Return YouTube Dislike · Return YouTube Username · Sanitize
sharing links · Seekbar components · Set transcript cookies · Shorts components · Snack bar
components · SponsorBlock · Spoof app version · Spoof watch history · Swipe controls ·
Toolbar components · Translations · Video playback · Voice Over Translation · Visual
preferences icons · Reload video · Settings — **and the GmsCore-support patch** (force-
enabled for non-root, wires GMS calls to `app.revanced`).

One non-fatal skip: *"Restore old seekbar thumbnails"* is not supported on 20.51.39 (needs
≤ 19.16.39) — informational, the build completed normally.

## 5. Runtime test on emulator (Android 14, API 34, x86_64, KVM-accelerated)

A headless AVD (`google_apis;x86_64`, Android 14) was booted and both APKs installed.
Coexistence confirmed: `pm list packages` shows `app.revanced.android.gms`, `anddea.youtube`,
**and** the image's stock `com.google.android.youtube` — all installed side by side.

| Check | Method | Result |
|---|---|---|
| MicroG authenticator registered | `dumpsys account` | ✅ `AuthenticatorDescription {type=app.revanced} → app.revanced.android.gms/…GoogleLoginService` |
| MicroG Settings UI runs | launch `SettingsActivity` + screencap | ✅ Material 3 UI; Device registration **On**, Cloud messaging **On** |
| Patched YouTube launches | `monkey` launch, screencap | ✅ home feed renders, **"Premium"** wordmark (premium patch active), RVX toolbar |
| **YouTube binds to MicroG (not stock GMS)** | `dumpsys activity services app.revanced.android.gms` | ✅ live `ConnectionRecord`: `anddea.youtube → app.revanced.android.gms/…CastMediaRouteProviderService`; every `GmsClient` call targets `app.revanced.android.gms` |
| **Login routes through MicroG** | tap Sign in → Add account, `dumpsys activity` + logcat | ✅ launches `app.revanced.android.gms/org.microg.gms.auth.login.LoginActivity` ("Connect Google account with microG") which redirects to Google's OAuth page |

**Conclusion:** the two packages are wired together correctly at runtime — the patched
YouTube resolves all Google-services calls to MicroG-RE and its sign-in flow lands on
MicroG's login screen, which loads Google's real authentication page.

### Benign warnings observed (expected, not bugs)

logcat shows `GmsClient: unable to connect to service:
app.revanced.android.gms.phenotype.service.START` and `…clearcut.service.START`. MicroG
**intentionally does not implement** Google's phenotype (feature-flag experiments) and
clearcut (telemetry) services; clients probe, fail gracefully, and continue. Standard MicroG
behavior.

### Stopped at (needs the user's own Google account)

- Entering real Google credentials on the OAuth page — that's your account to sign in with;
  the flow is verified up to Google's login page.
- Token refresh and reboot-persistence (follow-on to a completed sign-in).
- Play Integrity attestation (expected: basic passes, strong fails on non-root —
  see KNOWN_ISSUES.md).

Screenshots captured during the run:
`microg.png`, `yt-home.png`, `yt-you.png`, `yt-signin.png`, `yt-addaccount.png`
(in the session scratchpad).
