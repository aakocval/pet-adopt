# Security Policy

## Reporting a vulnerability

Please **do not open a public issue** for security problems.

Report privately through GitHub: **Security → Report a vulnerability** on this repository
(private vulnerability reporting). You should get a response within a few days.

## Supported versions

This is a showcase project. Only the latest release on `main` receives fixes.

## Security design

What the app protects, what it doesn't, and why, mapped to OWASP MASVS, is documented in
`docs/SECURITY.md` (added in the security hardening milestone, v0.8.0).

Already in place:

- No secrets in the repository; `local.properties`, keystores and signing files are git-ignored
- App backups disabled with explicit data extraction rules
- Only the launcher activity is exported
- Release builds are minified with R8
- Dependabot for Gradle dependencies and GitHub Actions; Actions pinned to commit SHAs
- Gradle wrapper pinned by SHA-256 and validated in CI
