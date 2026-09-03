# RVX Companion — Nothing Glyph Matrix Integration (Phone 3)

Real integration of Nothing's **official Glyph Matrix SDK** (`glyph-matrix-sdk-2.0.aar`, from
`Nothing-Developer-Programme/GlyphMatrix-Developer-Kit`) driving the 25×25 Glyph Matrix on
**Phone (3)** in reaction to playback.

## How it works

- **Detection:** `GlyphController.create()` uses the SDK's own `Common.is23112()` to detect Phone
  (3). Only then does it return the real `GlyphMatrixController`; every other device (incl. the
  emulator and non-Phone-3 phones) gets the safe `NoOpGlyphController`.
- **Connection:** `GlyphMatrixManager.getInstance() → init(callback) → register(Glyph.DEVICE_23112)`.
- **App-based control:** frames are pushed with `setAppMatrixFrame(int[])` (falls back to
  `setMatrixFrame` on older firmware), so the Matrix reacts to playback **without** you having to
  cycle to a Glyph Toy. `closeAppMatrix()` clears it when playback stops.
- **Driven globally** from `RvxApp` off the `PlaybackBus` + the Glyph toggle — independent of edge
  lighting.

## Playback → Matrix mapping

| Playback | Matrix |
|---|---|
| **Playing** | the **selected effect** (see below), animated at 20 fps |
| **Buffering** | an **arc that rotates** around the ring |
| **Paused** | filled disc, static **dim** |
| **Stopped** | Matrix **cleared** |

## Selectable effects (Settings → Nothing Glyph → Glyph effect)

Pick any of these while Glyph is enabled; the change applies live.

| Effect | What it looks like |
|---|---|
| **Now Playing** *(default)* | the **title scrolls** across the Matrix with a **progress bar** along the bottom — shows what's actually playing, from any music/video app |
| **Breathing** | filled disc, brightness pulses in and out |
| **Pulse ring** | a ring that expands and contracts |
| **Progress ring** | an arc that fills to show how far through the track you are |
| **Equalizer** | five bars bouncing like a music visualizer |
| **Orbit** | a comet dot circling the ring |
| **Ripple** | concentric rings expanding outward |
| **Heartbeat** | a double-thump pulse, then rest |
| **Sparkle** | points twinkling across the matrix |

The picker appears in **Settings → Device integrations → Nothing Glyph** once the Glyph toggle is
on (only on Phone 3). Your choice is saved.

## How to test on your Phone (3)

1. Install the APK, open Companion.
2. **Settings → Nothing Glyph → toggle ON.** (The row is active on your Phone 3, not greyed.)
3. Open **Checks → Glyph** group. Expected:
   - Glyph supported: **Yes**
   - Glyph permission: **Granted** (the `com.nothing.ketchum.permission.ENABLE` is declared)
   - **Glyph Matrix connected: Yes** (service bound + registered as DEVICE_23112)
4. Play a video/song in RVX. **Look at the Matrix on the back of the phone:**
   - It should show a **breathing disc** while playing, **dim** when paused, a **rotating arc**
     while buffering, and go dark when you stop.
5. Cross-check **Dev tab → Glyph** = "Supported · enabled" and the event log shows
   `[glyph] Glyph Matrix connected + registered (Phone 3)`.

## Honest status

- **Compiles against the real SDK and no-ops safely** on non-Nothing hardware — both verified on an
  Android 14 emulator (build, launch, Test Mode 8/8, Glyph shows "no hardware", no crash).
- **The visible Matrix behaviour can only be confirmed on real Phone (3) hardware** — I can't see
  your Matrix from here. This is a first, deliberately simple animation set. If something's off
  (wrong brightness, arc direction, nothing shows, priority conflict with a Glyph Toy), tell me
  what you see on the Matrix + the **Checks → Glyph** rows + **Dev event log**, and I'll tune it.

## Known caveats (from Nothing's SDK docs)

- A running **Glyph Toy** has higher priority than app control — if you press the Glyph Button and
  its carousel is showing, it overrides our output until dismissed.
- `setAppMatrixFrame` needs a Phone (3) system version from **2025-08-01 or later**; older builds
  fall back to `setMatrixFrame`.
- The Matrix is **monochrome white** — we animate brightness/shape, not color.
