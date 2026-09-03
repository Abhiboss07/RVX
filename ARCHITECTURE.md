# RVX — Architecture

This workspace produces a **complete YouTube experience for non-root devices** out of two
cooperating components. They are separate Android packages by necessity (Android's security
model does not allow one app to embed or impersonate another package's services), but they
are designed as a single product: the patched YouTube app detects, prompts for, and binds to
the bundled GmsCore automatically.

```
/home/abhiboss/Projects/RVX
├── YouTube-ReVanced-Extended-303/   # Component A: patched-YouTube APK builder (shell pipeline)
├── MicroG-RE-6.1.4/                 # Component B: GmsCore fork (Gradle Android project)
├── .toolchain/                      # Local JDK 21, Android SDK, zip (provisioned, no root)
└── *.md                             # Documentation (this file, BUILD, INSTALL, …)
```

---

## Component A — YouTube-ReVanced-Extended-303 (APK builder)

A fork of **j-hc/revanced-magisk-module**: a bash pipeline that downloads a stock YouTube
APK, applies ReVanced Extended (RVX) patches, and signs the result. It can emit both
non-root APKs and Magisk modules; for this project the non-root APK is the product.

### Pipeline (`build.sh` → `utils.sh`)

1. **Config** — `config.toml` declares one table per app. Active tables:
   - `[YouTube-Extended]` — patches from `anddea/revanced-patches` (dev), brand "ReVanced Extended"
   - `[YouTube-Music-Extended]` — same source, for YT Music (arm64 + arm builds)
   - `[YouTube-Morphe]`, `[YouTube-Music-Morphe]` — patches from `MorpheApp/morphe-patches`
   - CLI: `MorpheApp/morphe-cli` (latest release)
2. **Prebuilts** — downloads the patcher CLI jar and patches (`.rvp`) from GitHub releases
   into `temp/`; helper binaries live in `bin/` (`aapt2`, `apksigner.jar`, `dexlib2.jar`,
   `paccer.jar`, `htmlq`, `toml`).
3. **Stock APK** — fetched from the first available source per app: `archive.org` →
   `apkmirror` → `uptodown`. The version is auto-selected as the latest the patches support.
4. **Patching** — `java -jar morphe-cli patch <stock.apk> -p <patches.rvp> …`.
   For non-root (`build-mode = apk` / `both`) builds, `utils.sh` **force-enables the
   GmsCore-support patch** (`build_rv()`, utils.sh:694–716) — this is what rewires the app
   from Google Play Services to Component B.
5. **Signing** — `ks.keystore` / `ks-p12.keystore` (alias `jhc`, checked into the repo).
   All output APKs share this signature, so updates install over each other.
6. **Output** — `build/yt-revanced-extended-*.apk` (and Magisk `.zip` modules when
   `build-mode = both`). `sig.txt` pins expected stock-APK signatures to defend against
   tampered downloads.

### What the patches provide

SponsorBlock, Return YouTube Dislike, background playback, ad removal, swipe controls,
Shorts controls, premium-style features, custom branding hooks — plus **GmsCore support**,
which redirects every Google-services binding (auth, account manager, push) from
`com.google.android.gms` to the vendor package below.

## Component B — MicroG-RE 6.1.4 (GmsCore fork)

MorpheApp's fork of **microG GmsCore** with a Material 3 Expressive UI, rebuilt under an
alternative package identity so it can be installed **without root** alongside real Google
Play Services:

| Property | Value |
|---|---|
| Application ID | `app.revanced.android.gms` (`basePackageName` = `app.revanced`) |
| Namespace | `com.google.android.gms` (source-compatible with GMS APIs) |
| Version | 6.1.4 (versionCode 255034004) |
| minSdk / targetSdk / compileSdk | 24 / 29 / 36 |
| Toolchain | AGP 8.13.2, Gradle 8.14.3, Kotlin 2.2.10, JDK 17+ (jvmTarget 18) |
| Flavors | `default`, `huawei` |
| Output | `microg-release-6.1.4.apk` (unsigned release; signed post-build) |

### Module layout (Gradle)

- `play-services-core` — the application module (~82 manifest components across two
  processes, `:persistent` for long-lived services and `:ui` for settings screens).
- `play-services-{base,basement,tasks,auth,auth-base,cast,cast-framework,gcm,iid,…}` —
  API-compatible reimplementations of the corresponding Play Services libraries.
- `play-services-core-proto`, `safe-parcel-processor` — protobuf definitions (Square Wire)
  and an annotation processor for SafeParcelable code generation.

### What it provides to the patched YouTube

- **Google account login** — real OAuth against Google's endpoints (AccountManager
  authenticator of account type `app.revanced`), token issuance and refresh, session
  restore across reboots.
- **Push / GCM**, **device registration (checkin)**, **Cast framework**.

## The integration contract

The two components meet on one string: the **GmsCore vendor group ID**, `app.revanced`.

- anddea/Morphe patches default `gmsCoreVendorGroupId` to `app.revanced`, so the patched
  YouTube looks up `app.revanced.android.gms` for every GMS binding.
- MicroG-RE builds exactly that package (`applicationId "${basePackageName}.android.gms"`).
- On launch, the GmsCore-support patch **verifies the vendor package is installed and its
  services are reachable**; if not, it shows an "Install GmsCore" dialog — this is the
  built-in automatic detection that makes the two packages feel like one product.
- MicroG-RE's own settings UI handles account management, permission checks (battery
  optimization exemption, notifications), and diagnostics for the services side.

### Why one merged APK is impossible (and what we do instead)

GmsCore must run as its own package: YouTube's patched GMS client code resolves a
*different package's* ContentProviders/Services by authority and binds across process
boundaries, and AccountManager authenticators are registered per-package. A single APK
cannot host both identities on a non-root device. The unified experience is therefore
delivered as: **install MicroG-RE once → install patched YouTube → everything else
(detection, prompts, account UI) happens inside the apps.**

## Build environment (provisioned in `.toolchain/`)

The system ships only Java 26 (too new for Gradle 8.14) and lacks `zip` and an Android
SDK. `.toolchain/` therefore contains a Temurin JDK 21, Android cmdline-tools + SDK
(platform 36, matching build-tools), and an Info-ZIP `zip` binary — all user-local,
no root required. See BUILD.md for exact usage.
