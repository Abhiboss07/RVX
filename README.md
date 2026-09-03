# RVX — Non-root YouTube with Google login

A complete YouTube experience for **non-root** Android, delivered as two cooperating
packages that behave as one product:

- **Patched YouTube (RVX)** — YouTube with ReVanced Extended patches (SponsorBlock,
  Return YouTube Dislike, background playback, downloads, ad removal, swipe controls,
  Shorts controls, premium-style features).
- **MicroG-RE (GmsCore)** — a rootless Google Play Services replacement
  (`app.revanced.android.gms`) that provides real Google account login, token refresh,
  session restore, and push.

The patched YouTube auto-detects MicroG-RE, prompts to install it if missing, and binds to
it for every Google-services call. A single merged APK is impossible on non-root (Android
forbids one app from hosting another package's account authenticators and GMS services), so
the unified feel is achieved at runtime instead. Full rationale in
[ARCHITECTURE.md](ARCHITECTURE.md).

## Docs

| File | What |
|---|---|
| [ARCHITECTURE.md](ARCHITECTURE.md) | How both components work and the `app.revanced` integration contract |
| [BUILD.md](BUILD.md) | Build both APKs (toolchain is provisioned in `.toolchain/`) |
| [INSTALL.md](INSTALL.md) | Install order and setup on a non-root device |
| [KNOWN_ISSUES.md](KNOWN_ISSUES.md) | Limitations (Play Integrity, battery optimization, etc.) |
| [CHANGELOG.md](CHANGELOG.md) | Changes made in this workspace |

## Quick build

```sh
export JAVA_HOME=/home/abhiboss/Projects/RVX/.toolchain/jdk-21.0.11+10
export ANDROID_HOME=/home/abhiboss/Projects/RVX/.toolchain/android-sdk
export PATH="$JAVA_HOME/bin:/home/abhiboss/Projects/RVX/.toolchain/bin:$PATH"

# GmsCore
cd MicroG-RE-6.1.4 && ./gradlew :play-services-core:assembleDefaultRelease

# Patched YouTube (non-root APK only)
cd ../YouTube-ReVanced-Extended-303 && ./build.sh config-rvx.toml
```

## Deliverables

- `MicroG-RE-6.1.4/play-services-core/build/outputs/apk/default/release/microg-release-signed.apk`
  — GmsCore, package `app.revanced.android.gms` v6.1.4 (13 MB), signed & zipaligned.
- `YouTube-ReVanced-Extended-303/build/youtube-revanced-extended-v20.51.39-all.apk`
  — patched YouTube, package `anddea.youtube` v20.51.39 (115 MB), signed.

Both share signer SHA-256 `637c226c…`. Install MicroG-RE first, then the patched YouTube.
See [INSTALL.md](INSTALL.md). Verification results are in [TEST_REPORT.md](TEST_REPORT.md).
