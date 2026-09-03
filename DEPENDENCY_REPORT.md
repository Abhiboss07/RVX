# RVX — Dependency Report

Date: 2026-07-05. Source: `MicroG-RE-6.1.4/build.gradle` (the only component with a managed
dependency graph; the YouTube side consumes prebuilt tools, covered separately below).

## MicroG-RE dependencies

| Library | Version | Assessment |
|---|---|---|
| Android Gradle Plugin | 8.13.2 | Current stable |
| Gradle (wrapper) | 8.14.3 | Current stable |
| Kotlin | 2.2.10 | Current stable |
| kotlinx-coroutines | 1.10.2 | Current |
| androidx.annotation | 1.9.1 | Current |
| androidx.appcompat | 1.7.1 | Current |
| androidx.core | 1.17.0 | Current |
| androidx.fragment | 1.8.9 | Current |
| androidx.lifecycle | 2.9.2 | Current |
| androidx.navigation | 2.9.3 | Current |
| androidx.preference | 1.2.1 | Current |
| androidx.recyclerview | 1.4.0 | Current |
| androidx.webkit | 1.14.0 | Current |
| androidx.biometric | 1.1.0 | Latest stable (line is old but current) |
| androidx.mediarouter | 1.8.1 | Current |
| com.google.android.material | 1.14.0-alpha08 | **Pre-release (alpha)** — see note |
| Cronet | 119.6045.31 | Google networking; pinned |
| OkHttp | 5.1.0 | Current major |
| Square Wire | 5.3.10 | Current (protobuf) |
| Volley | 1.2.1 | Latest (project is low-activity but not abandoned) |
| slf4j | 2.0.17 | Current |
| multidex | 2.0.1 | Latest |
| conscrypt (JNI) | bundled `libconscrypt_gmscore_jni.so` | Google TLS provider |

### CVE / risk notes

- **No dependency is pinned to a version with a known applicable CVE** based on this review.
  The graph is unusually current for a microG fork.
- **Material `1.14.0-alpha08` is a pre-release.** Not a security issue, but alphas can carry
  UI regressions/instability. Consider pinning to the latest stable Material 1.x once the
  M3-Expressive features microG uses land in stable. Risk: **Low**.
- **Volley** is maintained but low-activity; it is used here for simple image/requests. No
  known open critical CVE. Risk: **Low**.
- **Cronet 119** is a pinned Chromium-net build. Google ships security fixes in newer Cronet;
  periodically bumping it is good hygiene. Risk: **Low** (TLS stack is Chromium's, patched
  line). 

### Recommendation
Run a live advisory check at build time for defense-in-depth:
`./gradlew dependencyCheckAnalyze` (OWASP plugin) or `gradle --refresh-dependencies` with a
lockfile. This repo has no lockfile; adding one would make the graph reproducible and make
future CVE triage precise. Priority: **Low–Medium**.

## YouTube-side prebuilt tools (supply-chain dependencies)

These are **checked-in or downloaded binaries**, not versioned library deps. Trust in them
is trust in their publishers:

| Artifact | Origin | Note |
|---|---|---|
| `bin/apksigner.jar`, `dexlib2.jar`, `paccer.jar` | Android SDK / j-hc | verify vs upstream hashes |
| `bin/aapt2/*`, `bin/htmlq/*`, `bin/toml/*` (ELF) | j-hc prebuilts | multi-arch build helpers |
| `morphe-cli-*.jar` | `MorpheApp/morphe-cli` GitHub release | downloaded at build |
| `patches-*.mpp/.rvp` | `anddea/revanced-patches` GitHub release | downloaded at build |
| stock YouTube APK | APKMirror/archive.org/Uptodown | **hash-pinned in `sig.txt`** |

**Residual risk (Medium):** the CLI jar and patches are pulled from GitHub releases without
in-repo hash pinning. If you want stronger guarantees, record their SHA-256 after a known-good
build and verify on subsequent runs (the stock APK is already pinned via `sig.txt`).
