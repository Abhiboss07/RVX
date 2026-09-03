# RVX — Known Issues & Limitations

## Expected install-time prompts (benign — tell users to expect these)

Both are Android/Google notices about *how* the APK was distributed, not defects in it.
Neither prevents installation or use.

- **"This app was built for an older version of Android."** Shown for **MicroG-RE only**
  (targetSdk 29). This is **intentional and must not be "fixed" by raising targetSdk.**
  MicroG declares 16 services with no `foregroundServiceType`; targetSdk ≥ 34 makes those
  mandatory, so a naive bump would crash the app at runtime on Android 14+ (this is why
  upstream microG deliberately targets 29). Verified: MicroG-RE installs and runs correctly
  on Android 14 as-is (Device Registration + Cloud Messaging **On**). The patched YouTube is
  already targetSdk **36** and does **not** trigger this prompt. Users can tap through it.

- **Google Play Protect warning ("unsafe app" / "app not commonly downloaded").** Expected
  for any self-signed, patched, re-signed APK not distributed via Google Play. It is a
  trust/reputation check on the signing certificate + install source — **not fixable in
  code** and not a sign the APK is unsafe. Users choose "Install anyway" / "More details →
  Install without scanning." It cannot be removed short of Play Store distribution or the
  original developer's signing key (neither is possible for a ReVanced-derived build).

## Architectural (by design, not bugs)

- **Two packages, not one.** GmsCore (`app.revanced.android.gms`) and the patched YouTube
  (`com.google.android.youtube`) are separate installs. Android's security model forbids one
  normal app from hosting another package's AccountManager authenticators and cross-process
  GMS services, so a single merged non-root APK is not possible. The apps cooperate at
  runtime to feel unified (auto-detection, install prompt, shared account). See
  ARCHITECTURE.md § "Why one merged APK is impossible".
- **Signature spoofing shows "unavailable" in MicroG Self-Check.** Expected on non-root.
  The patched YouTube binds to MicroG by package name via the GmsCore-support patch, so
  signature spoofing (which needs Xposed/root) is not used and not required.
- **Play Integrity / SafetyNet strong attestation fails.** Non-root MicroG cannot pass
  hardware-backed attestation. Basic Google login, sync, and content playback work;
  features gated behind strong integrity (some purchases, certain DRM tiers) may not.

## Operational

- **Battery optimization kills login.** If MicroG is not exempted from battery optimization,
  Android may kill its persistent service and the YouTube session drops after some idle
  time. Fix: exempt MicroG (INSTALL.md step 1.3). This is the single most common
  "login keeps logging out" cause.
- **Patched YouTube is a renamed package (`anddea.youtube`).** The anddea patches rename
  the package, so the patched build installs **alongside** stock YouTube without a signature
  conflict — no need to uninstall the Play Store copy. (Both can run; they are independent
  apps.)
- **First launch needs MicroG already present.** If YouTube is opened before MicroG is
  installed, login options are hidden until MicroG is installed and the app is relaunched.

## Build-environment

- **Flaky sandbox DNS.** The build host's systemd-resolved stub (127.0.0.53) intermittently
  times out. `utils.sh` `_req` was hardened (`--connect-timeout 45 --retry 6
  --retry-delay 3 --retry-all-errors`) so downloads survive slow resolution. On a normal
  network this change is harmless. See CHANGELOG.md.
- **System Java is 26; Gradle needs ≤21.** Gradle 8.14.3 does not run on JDK 26, so the
  build uses the provisioned Temurin JDK 21 in `.toolchain/`. Always export `JAVA_HOME` as
  shown in BUILD.md.
- **Unauthenticated GitHub API = 60 req/hour.** A full multi-app `config.toml` run can
  exhaust it. Use the focused `config-rvx.toml` (single app), or export `GITHUB_TOKEN`.
- **GmsCore release APK is unsigned.** `assembleDefaultRelease` produces an unsigned APK
  (`minifyEnabled false`, no signingConfig in the release build type). Sign it with the
  project keystore before installing (BUILD.md § 1).

## Upstream patch dependency

- The patch set tracks `anddea/revanced-patches` **dev** and `MorpheApp/morphe-cli`
  **latest**. Patches only apply to the specific stock YouTube versions they target; the
  builder auto-selects a compatible version. If Google ships a new YouTube and patches lag,
  a build may pin an older YouTube version until the patches catch up.
