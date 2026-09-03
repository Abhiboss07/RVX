# RVX Companion — Known Limitations

Honest boundaries of what a non-root, network-free companion app can do. None of these are bugs;
they are consequences of the Android platform and the observe-only design.

## Platform-imposed (cannot be changed without root or modifying YouTube)

1. **No control of RVX/YouTube.** We can *observe* the media session but cannot command YouTube
   (no reliable next/previous/seek from a third party). "Next/previous detection" means detecting a
   track/metadata change, not driving it.
2. **Video quality/resolution is invisible.** The MediaSession API does not expose resolution,
   codec, bitrate, or HDR to third-party apps. The Diagnostics row is permanently WARNING by design.
3. **No in-YouTube UI.** We cannot add buttons, bookmarks, or restyle anything inside YouTube's own
   screens — Android exposes none of that to an outside app. Those require ReVanced patches.
4. **No server-side watch history.** Analytics are computed only from sessions observed on-device
   while the app has Notification access; they are not YouTube's account history.
5. **Metadata depends on the source app.** Title, channel, album art, and duration are only as good
   as what YouTube/RVX publishes to its session. Missing album art or duration is the app's choice,
   not our omission.
6. **MediaSession latency is only measurable at a fresh state transition.** For an idle/just-bound
   session we report "—" rather than a misleading number (see BUG-001).

## Design choices (deliberate, could change if requirements change)

7. **No `INTERNET` permission.** The app cannot sync, back up, or update analytics across devices.
   This is a privacy guarantee, not an oversight.
8. **No Accessibility service.** Avoided on purpose; MediaSession makes it unnecessary and the
   permission is heavy/alarming.
9. **Battery-optimization exemption is not forced.** In deep Doze the system may delay the listener.
   We surface the status and rely on `requestRebind`; we don't nag for the exemption.

## Nothing Glyph (Phase 6)

10. **Real Glyph light output requires Nothing's proprietary SDK `.aar`.** The integration —
    capability detection, playback→animation mapping, permission/init/animation status — is fully
    wired and observable in Diagnostics, but until the official `com.nothing.ketchum` `.aar` is
    dropped into the build, `Glyph initialized` stays "No (SDK .aar absent)" and no LEDs light. On
    non-Nothing devices the feature is permanently inactive (safe no-op).

## Edge lighting (Phase 7)

11. **Requires the "Display over other apps" permission.** Without it the overlay cannot draw; the
    Diagnostics row flips to FAILED only when the feature is enabled but the permission is missing.
12. **Overlay render rate is vsync-driven, not a fixed measured FPS.** It repaints via
    `postInvalidateOnAnimation` and idles to zero cost when playback stops.

## Verification scope

13. **Emulator cannot prove live RVX playback.** An offline emulator has no logged-in YouTube
    session, so real title/progress/latency verification is a device task — see
    MANUAL_TEST_CHECKLIST.md.
