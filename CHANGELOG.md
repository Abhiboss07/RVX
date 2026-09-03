# RVX — Changelog

All notable changes made to this workspace while assembling the non-root YouTube + GmsCore
build. Dates are ISO. This log covers the integration/build harness — not the upstream
patch or GmsCore feature history (those live in their respective repos).

## 2026-07-05 — Security & stability audit

### Added
- `SECURITY_AUDIT.md`, `PRIVACY_REPORT.md`, `DEPENDENCY_REPORT.md`, `BUG_REPORT.md`,
  `PERFORMANCE_REPORT.md` — evidence-based production audit. Result: **no Critical/High
  findings, no malware/backdoor/exfiltration, no surveillance permissions, HTTPS-only,
  no hardcoded secrets** beyond the intentional public ReVanced keystore. Residual items
  are the shared signing key and prebuilt-binary supply-chain trust (both documented, both
  expected for a community ReVanced build).

### Verified (no code change required)
- Permissions, native code, dynamic-loading, secrets, network, and dependency graph audited;
  each category cited in `SECURITY_AUDIT.md`. The only engagement code change remains the
  `utils.sh` download-retry hardening below.

## 2026-07-05 — Initial build & integration

### Added
- `ARCHITECTURE.md` — full map of both components, the `app.revanced` vendor-group-ID
  integration contract, and why a single merged APK is not possible on non-root.
- `BUILD.md`, `INSTALL.md`, `KNOWN_ISSUES.md`, this `CHANGELOG.md`.
- `.toolchain/` — user-local build toolchain provisioned without root:
  Temurin **JDK 21.0.11** (Gradle 8.14.3 is incompatible with the host's JDK 26),
  **Android SDK** (platform-36, build-tools 34.0.0, platform-tools), and an Info-ZIP
  **`zip`** binary (not installed system-wide but required by the APK builder).
- `YouTube-ReVanced-Extended-303/config-rvx.toml` — focused build config: YouTube ReVanced
  Extended, **non-root APK only** (drops the Magisk-module / YT-Music / Morphe matrix from
  the default `config.toml` to keep the build within GitHub's unauthenticated API quota).
- `MicroG-RE-6.1.4/local.properties` — points Gradle at the provisioned SDK
  (`sdk.dir=…/.toolchain/android-sdk`).

### Fixed
- `YouTube-ReVanced-Extended-303/utils.sh` (`_req`): the downloader used
  `--connect-timeout 10 --retry 1`, which failed under the sandbox's intermittently slow
  systemd-resolved stub (DNS resolution occasionally exceeds 10 s). Hardened to
  `--connect-timeout 45 --retry 6 --retry-delay 3 --retry-all-errors`.
  **Why:** the first build aborted on `curl (28) Resolving timed out` fetching the `cmpr`
  prebuilt. **Risk:** none on healthy networks — only widens retry/timeout tolerance.
  **Outcome:** downloads survive transient DNS failures.

### Verified
- MicroG-RE builds `app.revanced.android.gms`; anddea/Morphe patches default the GmsCore
  vendor group ID to `app.revanced` — the two match, so the patched YouTube binds to the
  built GmsCore. (See task #5 / verification section for the concrete checks.)
