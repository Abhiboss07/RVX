# RVX — Bug Report

Date: 2026-07-05.

## Conclusion: no confirmed defects in the auditable surface

Honest scope: this repository contains a **build pipeline** and a **microG fork built from
upstream**. There is no first-party application code (Kotlin/Java/Flutter) authored here in
which to find lifecycle leaks, NPEs, race conditions, etc. — so there is nothing first-party
to fix. The claim is backed by:

- **Build succeeds cleanly.** MicroG `assembleDefaultRelease` → exit 0, only benign
  `-Xlint:deprecation/unchecked` notes, no errors. Patched YouTube build → exit 0.
- **Runtime health verified on Android 14** (emulator): MicroG installs and runs with
  services active; patched YouTube launches, renders, and binds to GmsCore without a crash;
  sign-in reaches Google's OAuth page. Full evidence in `TEST_REPORT.md`.

## One issue found and fixed during the engagement

| ID | Severity | Component | Root cause | Fix | Validation |
|---|---|---|---|---|---|
| B1 | Low (build reliability) | `utils.sh` `_req` | Downloader used `--connect-timeout 10 --retry 1`; the sandbox's systemd-resolved stub intermittently exceeds 10 s for DNS, aborting the build | Hardened to `--connect-timeout 45 --retry 6 --retry-delay 3 --retry-all-errors` | Build subsequently completed end-to-end; documented in `CHANGELOG.md` |

## Not covered (would duplicate upstream review)

Crash/leak analysis of microG GmsCore's own service implementations and of the
(closed-source, downloaded) YouTube binary is upstream scope — those codebases are reviewed
by their own projects and are not modified here. No evidence of runtime defects surfaced in
the black-box Android-14 run.
