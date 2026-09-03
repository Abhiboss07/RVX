# RVX Companion — Phase 2 (MVP) Verification

Date: 2026-07-22. Device: Android 14 emulator (API 34, `sdk_gphone64_x86_64`, x86_64, headless).
Build: `app-debug.apk` (57.8 MB), `BUILD SUCCESSFUL in 20s`, no warnings.

## Scope verified

Core MediaSession detection + display, Diagnostics page, Test Mode, and Developer Mode — the
agreed Phase 2 MVP plus the requested Developer Mode. No Glyph animations, edge-lighting effects,
or analytics expansion were added (deferred by instruction until the core is verified).

## Results

| Check | Method | Result |
|---|---|---|
| Compiles | `:app:assembleDebug` | ✅ BUILD SUCCESSFUL, 0 warnings |
| Installs + launches | adb install, monkey launch | ✅ MainActivity resumes, no ANR |
| No crashes | full `logcat` scan for FATAL/AndroidRuntime on our package | ✅ none |
| 5-tab navigation | screenshots of every tab | ✅ Home · Checks · Test · Dev · Settings |
| Home now-playing | screenshot | ✅ "Nothing playing / Stopped" card + analytics tiles |
| **Test Mode self-tests** | tap "Run all tests" | ✅ **8 passed · 0 failed** |
| Diagnostics (pre-grant) | screenshot | ✅ 1 PASS / 9 WARN / **2 FAIL** (listener + session, correct) |
| Diagnostics reacts to grant | `cmd notification allow_listener`, resume | ✅ FAILs → **0 FAIL** (4 PASS / 9 WARN) |
| **Live session detection** | after grant | ✅ detected real system session `com.google.android.googlequicksearchbox` |
| Developer Mode | screenshot | ✅ all 12 live fields + event log with test timeline |
| Graceful HW degradation | Glyph on non-Nothing device | ✅ "Disabled (no hardware)" / Settings row greyed |
| Honest "quality" row | Diagnostics | ✅ WARNING "Not exposed by MediaSession" (as designed) |

### Test Mode timings (from Developer Mode event log)

```
Play: PASS (122ms)          Buffering: PASS (121ms)
Pause: PASS (121ms)         Completion: PASS (121ms)
Resume: PASS (121ms)        Metadata update: PASS (120ms)
Seek: PASS (121ms)          MediaSession reconnect: PASS (274ms)
```

Reconnect is longer by design — it runs a lost-then-rebound sequence.

## What the emulator could NOT prove

An offline emulator cannot produce a real *PLAYING* YouTube/RVX session with title, channel, and
progress. Live detection was nonetheless demonstrated against the system's own media session
(`googlequicksearchbox`), confirming the observer, permission gating, and diagnostics are wired
end-to-end. Verifying RVX-specific playback (title/progress/latency filling in during a real
video) is a device task — see the Phase 5 manual checklist (upcoming).

## Evidence

Screenshots (session scratchpad): `s_home.png`, `s_checks.png` (pre-grant), `s_test0.png`
(pending), `s_test1.png` (8× PASS), `s_dev.png`, `s_checks2.png` (post-grant, 0 FAIL),
`s_settings.png`.

## Gate

Phase 2 MVP + Diagnostics + Test Mode + Developer Mode: **verified.** Ready for the next milestone.
