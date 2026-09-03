# RVX Companion — Phase 5 Manual Device Test Checklist

Run these on a **real device** (they cover what an offline emulator cannot). For each: how to
test, expected behaviour, possible failure, where to look, and how to recover. Tick each result.

Legend for "Debug location": **Dev** = Developer Mode tab (live fields + event log),
**Checks** = Diagnostics tab, **logcat** = `adb logcat | grep -i rvx`.

---

## 1. Media detection

**Steps:** 1. Grant Notification access (Settings → Notification access → RVX Companion).
2. Open RVX. 3. Play any video. 4. Open Companion → Home.
**Expected:** The now-playing card shows the video title + channel within ~2 seconds; Checks →
"Media session detected" = PASS with `anddea.youtube` as the active package.
**Possible failure:** Card stays "Nothing playing".
**Debug location:** Checks ("Notification listener" row), Dev (Active package / event log).
**Recovery:** Re-toggle Notification access off/on; reopen Companion. If still empty, confirm RVX
actually shows a media notification while playing.

## 2. Play / pause detection

**Steps:** With a video playing, pause it in RVX, then resume.
**Expected:** Dev → Playback state flips PLAYING ↔ PAUSED within ~1s; Home card dot/label updates.
**Possible failure:** State stuck on one value.
**Debug location:** Dev (Playback state + event log `[state]` lines).
**Recovery:** Scrub the video (forces a state event); re-open Companion.

## 3. Progress & duration

**Steps:** Let a video play for ~15s on Home / Dev.
**Expected:** Home progress bar advances; Dev → Position increments smoothly (interpolated) and
shows `m:ss / m:ss`; Checks → Duration = PASS.
**Possible failure:** Position frozen at 0:00 or no duration.
**Debug location:** Dev (Position), Checks (Position/Duration rows).
**Recovery:** Some content (live streams) reports no duration by design — expected WARNING, not a
bug. For normal videos, pause/resume to force a fresh position sample.

## 4. Track / video change (next / previous)

**Steps:** Play a video, then tap next (or pick another video).
**Expected:** Title + channel update; Dev event log shows a new `[metadata] Now: …` line.
**Possible failure:** Old title lingers.
**Debug location:** Dev (event log), Home card.
**Recovery:** Reopen Companion; confirm RVX updated its own media notification.

## 5. MediaSession latency

**Steps:** Pause/resume a few times, watch Dev → MediaSession latency.
**Expected:** A value in the tens–hundreds of ms; Checks latency row PASS (≤1500 ms).
**Possible failure:** Consistently high (>1500 ms) → WARNING.
**Debug location:** Dev (MediaSession latency), Checks.
**Recovery:** Usually device Doze/throttling — see test 12 (battery optimization).

## 6. Diagnostics accuracy

**Steps:** Open Checks with nothing playing, then with a video playing.
**Expected:** Idle → listener PASS, session WARNING/None. Playing → session PASS, title/channel
PASS, "Video quality" always WARNING ("not exposed by MediaSession" — by design).
**Possible failure:** A row contradicts reality (e.g. says None while a video plays).
**Debug location:** Checks; cross-check Dev live fields.
**Recovery:** Background/foreground Companion to force a re-check (permission rows refresh on
resume).

## 7. Test Mode (self-test)

**Steps:** Test tab → Run all tests.
**Expected:** 8 passed · 0 failed; each row shows PASS + a few-hundred-ms time.
**Possible failure:** Any FAIL (shows the assertion message inline).
**Debug location:** Test tab (inline error), Dev event log (`[test]` lines).
**Recovery:** Note the failing case + message and report it — this is an in-process test, so a
FAIL indicates a real pipeline regression.

## 8. Developer Mode

**Steps:** Open Dev while playing; leave it open ~30s.
**Expected:** All fields populate; Memory stays roughly flat; event log grows (capped at 50).
**Possible failure:** Memory climbs steadily (leak) or log frozen.
**Debug location:** Dev (Memory), logcat.
**Recovery:** Report the memory trend + repro steps.

## 9. Edge lighting (Phase 7)

**Steps:** Settings → grant "Display over other apps" → enable "Universal edge lighting". Play a
video and leave Companion.
**Expected:** A glow animates around the screen edges — breathing while playing, faster while
buffering, dim when paused, fades when stopped. Checks → "Edge lighting service" = Running.
**Possible failure:** No glow, or it never stops.
**Debug location:** Checks (Edge lighting group), Dev (Edge lighting row), logcat.
**Recovery:** Confirm overlay permission granted; toggle the feature off/on. Turning the toggle
off must stop the service (verify in Checks).

## 10. Nothing Glyph (Phase 6)

**Steps (Nothing device):** Ensure the Glyph SDK `.aar` is present; Settings → enable Nothing
Glyph; play a video. **(Non-Nothing device):** just open Checks.
**Expected (Nothing):** Checks → Glyph supported PASS, initialized PASS, playback synced PASS
while playing. **(Non-Nothing):** every Glyph row INFO/N-A, toggle greyed, **no crash**.
**Possible failure:** Crash on a non-Nothing device, or "supported" on non-Nothing hardware.
**Debug location:** Checks (Glyph group), Dev event log (`[glyph]` lines).
**Recovery:** On non-Nothing hardware the correct state is inactive; report any crash with logcat.
Real light output requires Nothing's proprietary `.aar` (see KNOWN_LIMITATIONS.md).

## 11. Theming — Material You & AMOLED

**Steps:** Settings → toggle Material You; change wallpaper (Android 12+). Toggle AMOLED in dark
mode.
**Expected:** Colors follow the wallpaper with Material You on; AMOLED makes surfaces true-black.
**Possible failure:** No color change / not black.
**Debug location:** visual; Settings toggles.
**Recovery:** Material You needs Android 12+; below that the brand palette is expected.

## 12. Battery optimization

**Steps:** Checks → read "Battery optimization". Optionally exempt RVX Companion in system
settings, re-check.
**Expected:** Shows Optimized (WARNING) or Exempt (PASS) accurately.
**Possible failure:** Listener drops out after long idle (detection stops).
**Debug location:** Checks, Dev event log (`[listener] disconnected/connected`).
**Recovery:** Exempt the app from battery optimization; the app also calls `requestRebind` on
disconnect.

## 13. Background execution & MediaSession reconnect

**Steps:** Play a video, swipe Companion away (not force-stop), keep playing 1–2 min, reopen.
**Expected:** On reopen the current state is shown; the listener reconnected silently.
**Possible failure:** Blank state after reopen.
**Debug location:** Dev event log (connect/disconnect), Checks.
**Recovery:** The listener is system-bound and rebinds automatically; if not, re-toggle
Notification access.

## 14. Screen off / on

**Steps:** Play a video, turn the screen off ~30s, turn it on, open Companion.
**Expected:** State resumes correctly; no crash; edge lighting (if on) resumes.
**Possible failure:** Stale state or overlay artifact.
**Debug location:** Dev, logcat.
**Recovery:** Reopen Companion; toggle edge lighting.

## 15. Orientation change

**Steps:** Rotate the device on each tab (Home/Checks/Test/Dev/Settings).
**Expected:** UI re-lays out; selected tab and in-progress test state are preserved; no crash.
**Possible failure:** Crash or lost state on rotation.
**Debug location:** logcat (look for Activity recreation issues).
**Recovery:** Report the tab + repro; state is held in the app-scoped singletons so it should
survive.

---

### Report format for any failure

> Test #, device model + Android version, what you saw vs expected, and the relevant Dev event-log
> lines or a `logcat` snippet. That's enough for me to reproduce and fix.
