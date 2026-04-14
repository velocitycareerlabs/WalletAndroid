# WalletAndroid SDK

**WalletAndroid** is the Android SDK for Velocity Career Labs' verifiable credential platform.

This SDK allows mobile developers to integrate verifiable credential issuing and presentation workflows within Android apps.

---

## 🛠 Features

- Generate and validate JWT-based verifiable credentials
- Secure storage using Android Keystore + `security-crypto`
- Seamless integration with OpenID4VC/OAuth flows
- Published to Maven Central via [JReleaser](https://jreleaser.org)

---

## 📦 Installation

Add this to your `build.gradle`:

```groovy
dependencies {
    implementation 'io.velocitycareerlabs:vcl:2.9.2' // or latest version
    implementation 'com.nimbusds:nimbus-jose-jwt:10.0.2'
    implementation "androidx.security:security-crypto:1.1.0-alpha07"
}
```

---

## 🧪 Testing the SDK

Run unit tests locally:

```bash
./gradlew test
```

---

## 🚀 Release Workflow

This SDK uses GitHub Actions + JReleaser to publish releases and RCs:

- Push to `main`: triggers `dev` build
- Manual workflow dispatch with `rc` or `prod` triggers versioned release
- Artifacts are published to Maven Central

---

## 🔐 Security

This SDK signs its artifacts using in-memory GPG keys (configured via GitHub Secrets).
Keys are never persisted or committed.

---

## 🧩 License

Apache 2.0 — see [LICENSE](https://github.com/velocitycareerlabs/WalletAndroid/blob/main/LICENSE)

---

## 🙋 Contributing

Issues and PRs are welcome.
