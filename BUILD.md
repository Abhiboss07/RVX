# RVX — Build Guide

Everything builds user-locally; **no root is required on the build machine**. The
provisioned toolchain lives in `.toolchain/` (created once, ~1.5 GB):

| Tool | Location | Why |
|---|---|---|
| Temurin JDK 21 | `.toolchain/jdk-21.0.11+10` | Gradle 8.14.3 cannot run on the system's Java 26 |
| Android SDK | `.toolchain/android-sdk` (platform 36, build-tools 34, platform-tools) | MicroG-RE compileSdk 36 |
| Info-ZIP `zip` | `.toolchain/bin/zip` | required by the APK builder; not installed system-wide |

Environment used by every command below:

```sh
export JAVA_HOME=/home/abhiboss/Projects/RVX/.toolchain/jdk-21.0.11+10
export ANDROID_HOME=/home/abhiboss/Projects/RVX/.toolchain/android-sdk
export PATH="$JAVA_HOME/bin:/home/abhiboss/Projects/RVX/.toolchain/bin:$PATH"
```

## 1. MicroG-RE (GmsCore) — `microg-release-6.1.4.apk`

```sh
cd MicroG-RE-6.1.4
# local.properties must point at the SDK (already written):
#   sdk.dir=/home/abhiboss/Projects/RVX/.toolchain/android-sdk
./gradlew :play-services-core:assembleDefaultRelease
```

Output: `play-services-core/build/outputs/apk/default/release/microg-release-6.1.4.apk`
(unsigned — sign it with the shared project keystore so upgrades install over each other):

```sh
java -jar ../YouTube-ReVanced-Extended-303/bin/apksigner.jar sign \
  --ks ../YouTube-ReVanced-Extended-303/ks-p12.keystore \
  --ks-pass pass:123456789 --key-pass pass:123456789 --ks-key-alias jhc \
  --out microg-release-signed.apk  microg-release-6.1.4.apk
```

Variants: `assembleHuaweiRelease` for the Huawei flavor; `assembleDefaultDebug` for a
debug build.

## 2. Patched YouTube (RVX) — `build/youtube-extended-…-apk.apk`

```sh
cd YouTube-ReVanced-Extended-303
./build.sh config-rvx.toml
```

`config-rvx.toml` is the focused config: **YouTube ReVanced Extended, non-root APK only**
(no Magisk modules, no YT Music, no Morphe duplicates). The stock `config.toml` still
builds the full matrix if you want everything: `./build.sh` (no args).

What the script does: downloads `morphe-cli` + `anddea/revanced-patches` (dev) from
GitHub releases → downloads the latest patch-supported stock YouTube APK (archive.org,
APKMirror or Uptodown) → verifies it against `sig.txt` → patches (GmsCore-support patch
force-enabled for non-root builds) → signs with `ks.keystore` (alias `jhc`).

Output lands in `build/`.

### Requirements

- network access (GitHub API + APK mirror sites); unauthenticated GitHub API allows
  60 requests/hour — export `GITHUB_TOKEN` if you hit the limit
- `jq`, `zip`, Java 17+ on PATH (all provided by the environment block above)

## Cleaning

```sh
cd YouTube-ReVanced-Extended-303 && ./build.sh clean   # removes temp/ and build/
cd MicroG-RE-6.1.4 && ./gradlew clean
```
