# RVX — Performance Report

Date: 2026-07-05.

## Measured

| Metric | Value | Source |
|---|---|---|
| MicroG-RE APK size | 13 MB | built APK |
| Patched YouTube APK size | 115 MB | built APK (stock YouTube is the bulk) |
| MicroG cold start (settings) | ~1s to first frame | emulator run (`Displayed … +1s630ms` for LoginActivity) |
| MicroG signature schemes | v2 + v3 | keeps install/verify fast, no legacy v1 |
| Both APKs zipaligned | yes | uncompressed-resource access at runtime |

## Assessment

- The **115 MB** YouTube APK is dominated by Google's stock APK payload (native libs,
  resources); the patches add a modest dex delta ("Writing 6430 new classes"). Size is
  inherent to YouTube, not to the patching.
- MicroG at **13 MB** is normal for GmsCore.

## Optimization opportunities (all Low priority, none applied — see rationale)

1. **R8 + resource shrinking for MicroG** (`minifyEnabled false` today). Could cut size and
   dead code. **Not applied:** upstream microG ships R8-off deliberately because its
   GMS-compatibility surface is reflection-heavy and aggressive shrinking risks breaking
   service resolution. Enabling it safely requires comprehensive keep-rules + full re-test —
   worth doing as a dedicated task, not as a blind flag flip on a verified-working build.
2. **ABI-split the YouTube APK.** The build currently produces an `-all` (universal) APK.
   Building per-ABI (`arm64-v8a` only) would roughly halve on-device size. The builder
   supports arch selection via `config.toml` (`arch = "arm64-v8a"`).
3. **Cronet bump** — newer Cronet builds carry perf + security fixes (also in
   `DEPENDENCY_REPORT.md`).

## Battery / background

MicroG runs a persistent foreground service for account/push — this is required for login to
survive and for push to arrive. The single biggest user-facing perf/battery factor is the
**battery-optimization exemption** (documented in `INSTALL.md` / `KNOWN_ISSUES.md`): without
it Android kills the service (login drops); with it the service is stable. No wake-lock
misuse was introduced by this repo.
