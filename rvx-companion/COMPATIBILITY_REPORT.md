# RVX Companion — Compatibility Report

**Supported range:** Android 10 (API 29) → Android 16 (API 36). `minSdk 29`, `targetSdk 34`,
`compileSdk 34`. No root. Any manufacturer (hardware features degrade gracefully).

## Build/toolchain

| Item | Version |
|---|---|
| AGP | 8.7.3 |
| Kotlin | 2.1.0 (Compose compiler plugin 2.1.0) |
| Compose BOM | 2024.12.01 |
| JDK (build) | Temurin 21 |
| DataStore | 1.1.1 |
| Java/Kotlin target | 17 |

## Per-version behaviour

| API | Android | What we do |
|---|---|---|
| 29 | 10 | Baseline. NotificationListenerService + MediaSessionManager, overlay via TYPE_APPLICATION_OVERLAY. |
| 30 | 11 | Scoped storage respected (we only use `filesDir`). No package-visibility queries needed (we resolve labels only for the observed session, which is granted). |
| 31 | 12 | **Dynamic color** (Material You) enabled; `android:exported` set explicitly; FGS-launch rules honored; edge-to-edge. |
| 33 | 13 | **POST_NOTIFICATIONS** requested for the edge-lighting FGS notification; listener rebind handled. |
| 34 | 14 | **FOREGROUND_SERVICE_SPECIAL_USE** declared with a subtype property for the overlay service; stricter FGS starts respected. |
| 35 | 15 | Edge-to-edge already adopted (`enableEdgeToEdge`); no exact-alarm/other restricted APIs used. |
| 36 | 16 | targetSdk can be raised without code changes; no APIs in the removed/behavior-changed set are used. |

All API-specific calls are guarded (`Build.VERSION.SDK_INT`) or provided by AndroidX compat.

## Hardware/feature compatibility

| Feature | Requirement | Off-requirement behaviour |
|---|---|---|
| Media detection | Notification access | Prompted in Settings; Diagnostics FAILED until granted |
| Edge lighting | "Display over other apps" | Toggle-gated; Diagnostics WARNING/FAILED, no crash |
| Nothing Glyph | Nothing device **+** Glyph `.aar` | NoOp controller; rows N/A; toggle greyed; no crash |
| Camera-flash notifications | Flash unit | Optional; guarded |
| Material You | Android 12+ | Falls back to brand palette below 12 |

## Verified on

- **Android 14 emulator (API 34, x86_64):** build, install, launch, 5-tab UI, Test Mode 8/8, live
  MediaSession detection, permission-reactive Diagnostics, grouped Glyph/Edge sections, no crashes.
  (See TEST_REPORT.md / PHASE2_VERIFICATION.md.)
- **Other API levels / real hardware:** to be confirmed via MANUAL_TEST_CHECKLIST.md. No
  version-specific APIs are used outside the guarded paths above, so no compatibility breaks are
  expected; report any via the checklist format.

## Coexistence with RVX

RVX Companion (`com.rvx.companion`) is a **separate app**. It does not modify, replace, or update
patched YouTube (`anddea.youtube`) or MicroG (`app.revanced.android.gms`). Installing/updating the
companion never touches those.
