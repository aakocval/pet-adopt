# Security Policy

## Reporting a vulnerability

Please **do not open a public issue** for security problems.

Report privately through GitHub: **Security → Report a vulnerability** on this repository
(private vulnerability reporting). You should get a response within a few days.

## Supported versions

This is a showcase project. Only the latest release on `main` receives fixes.

## Security design

The full threat model, mapped to OWASP MASVS, is in [docs/SECURITY.md](docs/SECURITY.md),
including what is deliberately not protected and the certificate pin rotation plan.

In short:

- Session and access token encrypted at rest (AES-256-GCM, Android Keystore key); password never stored
- HTTPS only, system CAs only, certificate pinning for the photo API with an expiry safety net
- Deep links validated by an allow-list before anything acts on them
- `FLAG_SECURE` on account and application screens; optional biometric re-auth to reveal the email
- Only the launcher activity exported; backups disabled; one permission (`INTERNET`)
- No logging in release builds; R8 shrinking; release signing from CI secrets, never the repo
- gitleaks secret scanning, dependency graph + Dependabot alerts, dependency review on PRs
- GitHub Actions pinned to commit SHAs; Gradle wrapper checksum-validated
