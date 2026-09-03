# RVX — Security Audit

Date: 2026-07-05 · Scope: `/home/abhiboss/Projects/RVX` · Method: static, evidence-based.
Every claim below cites what was inspected. Where a category found nothing, that is stated
explicitly with the check that was run.

## Scope & honest limitations (read first)

What is actually in this repository, and therefore auditable:

1. **Build/shell pipeline** (`YouTube-ReVanced-Extended-303/*.sh`, `module/*.sh`) — runs on
   your build machine.
2. **Checked-in binaries & keystores** (`bin/`, `*.keystore`).
3. **MicroG-RE source + built APK** — a fork of open-source microG GmsCore.
4. **Network endpoints** the build contacts.

What is **not** in this repository and therefore **cannot** be line-audited here:

- The **patched YouTube app's runtime code** is Google's closed-source YouTube APK plus the
  `anddea/revanced-patches` bytecode transforms, both **downloaded at build time** — no
  source is present. Its security rests on upstream Google + the anddea/Morphe patch
  projects, not on code in this repo.
- microG GmsCore has **thousands** of source files; this audit inspected its security-
  relevant surface (manifest, permissions, native code, dynamic-loading, network, secrets,
  dependencies), not every line of every service implementation.

This is a supply-chain + manifest + build-surface audit, which is where the controllable
risk for this kind of project actually lives. It is not a line-by-line review of upstream
microG or of Google's YouTube binary.

## Summary of findings

| # | Finding | Risk | Status |
|---|---|---|---|
| S1 | Shared **public** signing key (`CN=ReVanced`, password `123456789`) | Medium (by ecosystem design) | Documented — not fixable without your own key |
| S2 | Checked-in prebuilt binaries (`bin/` CLI jars, aapt2/htmlq/tq) trusted implicitly | Medium | Documented — mitigation: verify against upstream hashes |
| S3 | `eval` in `utils.sh` on config-derived strings | Low | Documented — not remote-controlled |
| S4 | Build downloads stock APK from mirror sites | Low | Mitigated upstream by `sig.txt` hash pinning |
| — | Malware / backdoor / exfiltration / miner | **None found** | See Phase 2 |
| — | Dangerous runtime permissions (camera/mic/SMS/location) | **None requested** | See Phase 3 |
| — | Hardcoded API keys / tokens / private keys | **None found** | See Phase 4 |
| — | Plaintext HTTP / hardcoded IPs | **None found** | See Phase 5 |

No Critical or High severity issue was found.

---

## Phase 1 — Code / dependency surface

- MicroG-RE toolchain: **AGP 8.13.2, Gradle 8.14.3, Kotlin 2.2.10, compileSdk 36** — all
  current stable. Full dependency version list and CVE assessment in `DEPENDENCY_REPORT.md`.
- The YouTube side has no compiled source in-repo; it orchestrates prebuilt tools.

## Phase 2 — Malware / backdoor scan  →  **No confirmed findings**

Checks run and results:

- **Dynamic code loading / command execution:** `grep -rnE 'DexClassLoader|Runtime…exec|
  ProcessBuilder|System.load'` across all `.java`/`.kt` → **0** matches for
  DexClassLoader / Runtime.exec / ProcessBuilder. The single `System.load` is
  `ProviderInstallerImpl.java:152`, loading microG's own Conscrypt TLS JNI lib
  (`libconscrypt_gmscore_jni.so`) — a documented, legitimate TLS provider, not remote code.
- **Native libraries:** the only `.so` in the build is `libconscrypt_gmscore_jni.so`
  (Google's Conscrypt). No unexplained native blobs.
- **Hidden exfiltration endpoints:** every non-Google host referenced in MicroG source is a
  documentation/support link (`github.com`, `dontkillmyapp.com` [battery-optimization help],
  `reddit.com`, `x.com`, `img.shields.io` badges, `oss.sonatype.org`, `jacoco.org`). **No
  analytics/tracking/crash-reporting SDK endpoints, no miner, no silent-download URLs.**
- **Build scripts:** no `curl … | sh`, no `base64 -d | sh`, no piped remote execution.

## Phase 3 — Permission audit  →  appropriate, least-privilege for an auth provider

MicroG's built APK requests **24** permissions. Every one maps to a GmsCore function:

| Group | Permissions | Justification |
|---|---|---|
| Accounts/auth | AUTHENTICATE_ACCOUNTS, GET_ACCOUNTS, MANAGE_ACCOUNTS, USE_CREDENTIALS | It *is* the Google account provider |
| Sync | READ/WRITE_SYNC_SETTINGS, READ_SYNC_STATS | Account sync framework |
| Push (GCM) | c2dm RECEIVE/SEND, gtalkservice, microg STATUS_BROADCAST | Cloud Messaging |
| Service lifecycle | FOREGROUND_SERVICE, WAKE_LOCK, RECEIVE_BOOT_COMPLETED, USE_EXACT_ALARM, REQUEST_IGNORE_BATTERY_OPTIMIZATIONS | Persistent service + session-restore-after-reboot |
| Network | INTERNET, ACCESS_NETWORK_STATE | OAuth + service calls |
| Misc | SYSTEM_ALERT_WINDOW (login overlays), WRITE/READ_EXTERNAL_STORAGE | |
| System (no-op on non-root) | UPDATE_APP_OPS_STATS, CHANGE_DEVICE_IDLE_TEMP_WHITELIST | Signature/system perms; **not granted** to a normal app, silently ignored |

**Not requested (verified absent):** CAMERA, RECORD_AUDIO, any SMS, READ_CONTACTS, any
LOCATION, READ_CALL_LOG, READ_PHONE_STATE, CALENDAR, BODY_SENSORS. None of the
surveillance-associated permissions the audit brief worried about are present.

## Phase 4 — Secrets / credentials  →  only the intentional public keystore

- Repo-wide search for API keys / tokens / client secrets / private keys → **none**, except:
- **S1 (Medium, by design):** `utils.sh:550` hardcodes keystore password `123456789`, and
  `ks.keystore` / `ks-p12.keystore` (`CN=ReVanced`) are checked in. This is the **well-known
  public ReVanced community key** — it is intentionally shared so anyone can rebuild.
  Implication: the APK signature is **not a trust anchor** (anyone can sign an
  upgrade-compatible APK). It is not a leaked production/Google secret. **To make signature
  meaningful, generate your own keystore and keep it private** (BUILD.md shows where it is
  wired). For a personal/community build, the shared key is acceptable and expected.

## Phase 5 — Network security  →  HTTPS-only, no plaintext, hashes pinned

- Every URL in scripts/config is **HTTPS**. Plaintext-`http://` search → **0** (excluding
  XML namespace/license URIs, which are identifiers, not requests).
- No hardcoded IP addresses.
- Endpoints contacted by the build: `github.com`, `api.github.com`,
  `raw.githubusercontent.com`, `apkmirror.com`, `archive.org`, `*.uptodown.com`,
  `j-hc.github.io`. All expected for a ReVanced builder. Detail in `PRIVACY_REPORT.md`.
- **Mitigation already present:** `sig.txt` pins SHA-256 of the expected stock YouTube /
  YT Music APKs, so a tampered download from a mirror is detected before patching.
- I **hardened** `_req` (`utils.sh`) earlier to `--connect-timeout 45 --retry 6
  --retry-all-errors`; this is a reliability change, not a security regression (still
  `--fail`, still HTTPS).

## Phase 6 — Google login security

The auth flow is upstream microG's `org.microg.gms.auth.*` (verified at runtime: sign-in
routes to `LoginActivity` → Google OAuth page — see `TEST_REPORT.md`). Token lifecycle,
refresh, and storage are implemented by upstream microG, which stores account state via
Android's AccountManager (system-protected, per-app). No token-logging or plaintext-token
sink was introduced by anything in this repo. A deep review of microG's token storage
internals is upstream scope.

## Phase 7 — Local storage

No app source in this repo writes credentials to disk. microG relies on AccountManager
(system-managed). No SharedPreferences/SQLite in-repo code stores tokens in plaintext
(there is no first-party app code here beyond the microG fork).

## Phases 8–10 — Memory / crashes / performance

No first-party application code exists in this repo to leak contexts or crash — the
deliverables are (a) a build pipeline and (b) a microG fork built from upstream. Evidence of
runtime health: the built MicroG APK **installed and ran on Android 14** with services
active, and the patched YouTube launched and bound to it without crashing
(`TEST_REPORT.md`). A line-by-line leak/crash audit of upstream microG's service code is
out of scope and would duplicate upstream review. See `BUG_REPORT.md` / `PERFORMANCE_REPORT.md`.

## Phase 11 — Dependencies / CVEs

See `DEPENDENCY_REPORT.md`. Summary: all MicroG dependencies are current stable releases; no
version pinned to a known-vulnerable release was identified.

## Phase 12 — Build configuration

- MicroG release build: `minifyEnabled false` (R8 off), unsigned by Gradle → signed
  post-build with the shared key. **Recommendation (Low):** enabling R8 + resource shrinking
  would reduce size and strip unused code, but upstream microG ships R8-off by choice
  (reflection-heavy GMS-compat surface risks breakage under aggressive shrinking). If enabled,
  it must be paired with thorough keep-rules and re-testing. Not changed here to avoid
  breaking the verified-working build.
- Both APKs are **zipaligned** and signed with **v2 + v3** signature schemes (verified).

## Phases 13–15 — Static analysis / stability / final verification

- Lint: MicroG builds with only benign `-Xlint:deprecation/unchecked` notes (no errors).
- Verified end-to-end on Android 14 emulator: build-from-scratch → install → launch →
  YouTube binds to GmsCore → sign-in reaches Google OAuth. Full evidence in `TEST_REPORT.md`.
- No existing feature was removed by this audit; the only code change in the whole engagement
  was the download-retry hardening in `utils.sh` (documented in `CHANGELOG.md`).

## Recommendations (prioritized)

1. **(If distributing to others) generate your own private keystore** so the signature means
   something and only you can ship upgrades. The shared ReVanced key is fine for personal use.
2. **Pin/verify the checked-in `bin/` binaries and downloaded CLI/patches** against upstream
   release SHA-256 sums (S2) — this is the main residual supply-chain trust.
3. Keep `sig.txt` current so stock-APK hash pinning stays effective.
4. Optionally evaluate R8 for the microG build behind thorough re-testing (Low priority).
