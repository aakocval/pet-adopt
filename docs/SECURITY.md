# Security design and threat model

What Pet Adopt protects, against whom, how, and what it deliberately doesn't protect. Controls
are mapped to [OWASP MASVS v2](https://mas.owasp.org/MASVS/) categories.

## What's worth protecting

| Asset | Where it lives | Sensitivity |
|---|---|---|
| Session: display name, email, access token | DataStore, **encrypted** (AES-256-GCM, Android Keystore key) | High (token = account access) |
| Password | In-memory UI state only, cleared after sign-in | High |
| Adoption application: name, email, phone, home details, message | ViewModel memory + `SavedStateHandle` while the flow is open | Medium (PII) |
| Favorites, onboarding flag | Memory / plain DataStore | Low |
| Pet catalog and photo URLs | App asset / plain file cache | Public |

## Who we defend against

1. **Network attacker** (hostile Wi-Fi, compromised router): reads or alters traffic.
2. **Other apps on the device**: send crafted intents and deep links, read shared storage.
3. **Someone holding the phone**, or looking at it: screenshots, Recents, shoulder-surfing.
4. **Someone with the files**: a backup, a device transfer, a rooted device's file system.
5. **The supply chain**: a vulnerable or malicious dependency, a tampered build tool.
6. **Us**: a leaked secret, a debug log in production, a missed validation.

## Controls

### MASVS-STORAGE: data at rest
- The session (name, email, **access token**) is encrypted with AES-256-GCM before it touches
  disk; the preferences file only ever contains ciphertext (`SecureSessionStore`). A test reads
  the raw file and asserts neither the email nor the token appears.
- Installs from v0.5-0.7 (plaintext session keys) are migrated by a DataStore migration on first
  read, then the plaintext keys are deleted.
- The password is never persisted, logged, or put in `SavedStateHandle`.
- `allowBackup="false"` plus explicit `data_extraction_rules.xml`: nothing goes to cloud backup or
  device-to-device transfer.

### MASVS-CRYPTO: cryptography
- Keys are generated **inside** the Android Keystore (`KeystoreKeys`): non-exportable, hardware
  backed where available, `setRandomizedEncryptionRequired(true)` so an IV can't be reused.
- AES-GCM (authenticated): tampered ciphertext fails to decrypt instead of producing garbage.
- No custom crypto, no hardcoded keys. `androidx.security:security-crypto` is deprecated, so the
  app uses `javax.crypto` + Keystore directly (no extra dependency).
- If the key is lost (e.g. after a device restore), decryption fails and the user is simply
  signed out: no crash, no fallback to plaintext.

### MASVS-AUTH: authentication
- Sign-in validates and sanitizes input, ignores double submits, and never navigates itself: the
  root reacts to session state and clears the back stack, so Back can't return to login after
  sign-in or to the app after sign-out.
- Revealing the account email requires re-authentication (`BiometricPrompt`, biometric or device
  credential). On a device with no screen lock there's nothing to verify, so it reveals directly.
- The backend is fake (see "Not protected"). Tokens are opaque, random (`SecureRandom`, 256 bits).

### MASVS-NETWORK: network communication
- **No cleartext** anywhere: `cleartextTrafficPermitted="false"` in the network security config
  and `usesCleartextTraffic="false"` in the manifest. Release builds trust **system CAs only**.
- **Certificate pinning** for the photo API, `dog.ceo` (see the rotation plan below). Pins are on
  CA public keys, not the leaf certificate, so routine certificate renewals don't break the app.
- Responses are untrusted input: Dog CEO breed paths are allow-listed before use, and only HTTPS
  image URLs on the expected hosts are accepted.
- Debug builds may trust a user-installed proxy CA (`debug-overrides`) for traffic inspection.
  This never applies to release builds, which aren't debuggable.

### MASVS-PLATFORM: platform interaction
- Only the launcher activity is exported. The merged manifest adds no other exported component
  of ours.
- Deep links (`petadopt://pet/<id>`) are attacker-controlled input. `DeepLinkParser` is an
  allow-list: exact scheme and host, no credentials or port, one path segment validated on the
  **raw** (undecoded) path against the id format. Valid links wait behind the auth gate.
- `FLAG_SECURE` on Profile and the adoption flow: no screenshots, no screen recording, blank in
  Recents. Reference-counted so it stays on between Form and Review.
- One permission: `INTERNET`.

### MASVS-CODE: code quality and build
- Release: `isDebuggable = false`, R8 full mode with code and resource shrinking.
- Logging goes through `AppLogger`: logcat in debug, a no-op binding in release (the logcat
  implementation isn't even compiled into release), plus R8 rules that strip `android.util.Log`
  calls from all code. Call sites log failure types, never URLs, emails or tokens.
- Inputs are validated in the domain layer (pure Kotlin), and validated again before submit.
- Static analysis (detekt, Android Lint with warnings as errors) runs on every push.

### MASVS-RESILIENCE and MASVS-PRIVACY
- Resilience (anti-tamper, root detection) is intentionally out of scope; see below.
- Privacy: no analytics, no tracking, no third-party SDKs that phone home. The only network calls
  are to fetch public pet photos.

### Supply chain and secrets
- **No secrets in the repo.** `local.properties`, keystores and `keystore.properties` are
  git-ignored. Release signing comes from environment variables (CI secrets) or an untracked
  `keystore.properties`; without them the release build is unsigned.
- **gitleaks** scans the full history on every push, every PR and weekly.
- **Dependencies:** Dependabot version updates; Gradle's resolved dependency graph is submitted
  to GitHub so Dependabot security alerts cover transitive dependencies; PRs fail on a new
  dependency with a known high-severity advisory (dependency review).
- GitHub Actions are pinned to commit SHAs; the Gradle wrapper is pinned by checksum and
  validated in CI.

## Not protected, and why

| Gap | Why it's accepted |
|---|---|
| Fake backend: no real server-side auth or rate limiting | It's a showcase. A real backend owns credential checks, lockout and token lifetime. |
| Rooted or instrumented devices (Frida, hooking) | Root/hook detection is bypassable and costly to maintain; the data here doesn't justify it. A real app with payments would add Play Integrity. |
| Adoption draft in `SavedStateHandle` | Needed to survive process death. The OS keeps it in the app's private saved state and drops it when the task is removed. It's PII, not a credential. |
| `images.dog.ceo` and `cataas.com` aren't pinned | Public images on third-party CDNs that rotate certificates without notice; pinning would break photos for no confidentiality gain. TLS with system CAs still applies. |
| Image disk cache and photo URL cache aren't encrypted | Public content. |
| Favorites are in memory | Low sensitivity; persisted storage would use the same plain DataStore. |
| Obfuscation as protection | R8 obfuscation shrinks and slows casual reading; it isn't a security boundary and nothing relies on it. |

## Certificate pin rotation plan

Pinned host: `dog.ceo`. Pins (SHA-256 of the SubjectPublicKeyInfo):
- Primary: Google Trust Services **WE1** (issuing intermediate, valid until 2029-02-20)
- Backup: **GTS Root R4** (its root), so a reissued intermediate under the same root still works

Safety nets:
- The pin set has an `expiration` (2027-06-30). After it, pins are ignored and normal CA
  validation still applies: a forgotten rotation degrades to "unpinned", never to "no photos".
- `NetworkSecurityConfigTest` fails CI **60 days before** that date.
- If pinning ever fails, photos fall back to the illustrated faces; nothing else breaks.

To rotate (every release cycle, or when the test warns):
1. Read the live chain: `openssl s_client -connect dog.ceo:443 -servername dog.ceo -showcerts`.
2. Compute each certificate's pin:
   `openssl x509 -pubkey -noout | openssl pkey -pubin -outform der | openssl dgst -sha256 -binary | base64`.
3. Keep at least one pin that matches today's chain and add the new one **before** the old one
   stops being served (ship both for one release).
4. Move `expiration` forward (at most about a year), run the tests, and ship.
5. If the host moves to a different CA, add the new CA's intermediate and root as pins in a
   release that ships before the switch, or let the pins expire if the switch is imminent.

## Reporting a vulnerability

See [SECURITY.md](../SECURITY.md): use GitHub's private vulnerability reporting.
