# RVX — Install Guide (non-root device)

Two APKs make up the product. **Order matters** — install GmsCore first so the patched
YouTube can bind to it on first launch.

| Component | File | Package |
|---|---|---|
| GmsCore | `microg-release-signed.apk` | `app.revanced.android.gms` |
| Patched YouTube | `youtube-revanced-extended-v20.51.39-all.apk` | `anddea.youtube` |

Both are signed with the same project keystore (signer SHA-256 `637c226c…`).

## 1. Install MicroG-RE (GmsCore)

File: `microg-release-signed.apk` (package `app.revanced.android.gms`).

1. Copy the APK to the device and open it. Allow "install from unknown source" if prompted.
2. Open **MicroG Settings** once installed.
3. Grant these so background services and login survive:
   - **Notifications** permission (Android 13+) — required for the persistent service.
   - **Disable battery optimization** for MicroG (Settings → Apps → MicroG → Battery →
     Unrestricted). Without this, Android kills the account/push service and login drops.
4. In MicroG Settings, tap **Google account → Add account** and sign in. This registers a
   Google account of type `app.revanced` in Android's AccountManager.
5. Verify **Self-Check** — all "Services" rows should be checked. "System grants signature
   spoofing" will be **unchecked on non-root**; that is expected and fine (the patched
   YouTube talks to MicroG by package name, not via signature spoofing).

## 2. Install patched YouTube (RVX)

File: `build/youtube-revanced-extended-v20.51.39-all.apk`
(package `anddea.youtube` — a **renamed** package, so it installs **alongside** stock
YouTube without conflict; you do **not** need to uninstall the Play Store copy).

1. Install the patched APK.
2. Launch it. On first run it detects GmsCore; if MicroG-RE is missing it shows an
   "Install GmsCore" prompt. Since you installed it in step 1, it binds automatically.
3. Sign in: **Account → Sign in** uses the MicroG account you added. Tokens are issued and
   refreshed by MicroG and survive reboot.

## Post-install verification

- YouTube: profile picture loads, subscriptions/history sync → login works.
- SponsorBlock, Return YouTube Dislike, background playback, downloads → patches active.
- Reboot the device, reopen YouTube → still logged in (MicroG restored the session).

## Updating

Both APKs are signed with the same project keystore, so new builds install over the old
ones without uninstalling. Update MicroG-RE and YouTube independently. If login breaks
after a YouTube update, re-check the battery-optimization exemption on MicroG.

## Supported Android versions

Tested surface: Android 10–15, non-root. minSdk of MicroG-RE is 24 (Android 7); the
patched YouTube's minSdk follows the stock APK. On rooted devices everything still works;
root simply is not required.
