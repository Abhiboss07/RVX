# RVX — Privacy Report

Date: 2026-07-05. Focus: what data leaves the device/build host, to whom, and what the
components can access.

## Data egress — build time (your PC)

The build contacts only these hosts, all over HTTPS, all to **download** (no user data
uploaded):

| Host | Purpose | Data sent |
|---|---|---|
| `github.com`, `api.github.com`, `raw.githubusercontent.com` | patcher CLI, patches, helper binaries | none beyond the request (optional `GITHUB_TOKEN` if you set it) |
| `apkmirror.com`, `archive.org`, `*.uptodown.com`, `dw.uptodown.com` | stock YouTube APK download | none (anonymous GET) |
| `j-hc.github.io` | `cmpr` prebuilt (module packaging) | none |

No build step transmits your files, identity, or credentials anywhere. GitHub sees an
anonymous (or token-authenticated, your choice) download.

## Data egress — runtime (end-user device)

- **MicroG-RE (GmsCore):** talks to **Google's** servers to provide account login, token
  refresh, device check-in, and push (GCM). This is inherent and necessary — it is a Google
  Play Services replacement; using Google login means talking to Google. There is **no
  third-party telemetry**: the source contains no analytics/tracking/crash-reporting SDK and
  no non-Google exfiltration endpoint (verified — see `SECURITY_AUDIT.md` Phase 2).
- **Patched YouTube:** communicates with Google/YouTube as normal YouTube does. The
  `anddea/revanced-patches` set commonly *removes* ad/tracking calls and adds SponsorBlock
  (which queries the community SponsorBlock API for segment data keyed by video ID, not by
  user identity). Exact per-patch behavior is governed by the upstream patch project, not by
  code in this repo.

## Sensitive-permission exposure

MicroG requests **no** camera, microphone, SMS, contacts, location, call-log, or
phone-state permissions (verified against the built APK). Its access is limited to accounts,
sync, push, and service-lifecycle permissions — appropriate to an auth/services provider.
See the full permission table in `SECURITY_AUDIT.md` Phase 3.

## Credential handling

Google account credentials are entered on **Google's own OAuth web page** (loaded by
microG's `LoginActivity`, confirmed at runtime). Account state is held by Android's
system **AccountManager** (per-app, system-protected), not in app-readable plaintext files.
Nothing in this repo logs or copies tokens.

## Data retention after logout

Account removal is handled by microG through AccountManager; removing the account clears its
stored auth state. There is no first-party cache in this repo that would retain user data
post-logout.

## Bottom line

No third-party tracking. Runtime network is Google (unavoidable for Google login) plus, for
YouTube, the same services stock YouTube uses (minus what the patches strip). Build time
sends no personal data anywhere.
