# Azuku ⚡
> Next-generation Android developer toolkit and privileged service manager for Android 11–16.

Azuku provides a high-privilege service framework inspired by Shizuku, built with Jetpack Compose, native ART optimization, 3D glassmorphic UI, an interactive shell terminal, an ADB command builder, an app freezer, and full Android permission controls.

---

## 📦 Automated APK Build & Compression (GitHub Actions)

This repository includes a pre-configured GitHub Actions workflow in [`.github/workflows/build_apk.yml`](.github/workflows/build_apk.yml) that automatically compiles, signs, tests, and compresses APKs into a `.zip` archive on every commit, tag, or manual dispatch.

### 📥 How to Download the Built APK from GitHub Actions

1. Go to your repository on **GitHub**.
2. Click on the **Actions** tab in the top navigation bar.
3. Click on the latest workflow run: **"Build & Compress Azuku APK"**.
4. Scroll down to the **Artifacts** section at the bottom of the summary page.
5. Click on **`Azuku-APKs`** to download the compressed archive.
   - The archive contains:
     - `Azuku-v1.0-APKs.zip` (compressed APK package)
     - `Azuku-v1.0-release.apk` (optimized release build)
     - `Azuku-v1.0-debug.apk` (development debug build)

---

### 🚀 How to Trigger a Manual APK Build in GitHub

1. In your GitHub repository, open the **Actions** tab.
2. Under "Workflows" on the left, select **"Build & Compress Azuku APK"**.
3. Click the **Run workflow** dropdown button on the right.
4. Choose the branch (`main` or `master`) and select the build variant (`both`, `debug`, or `release`).
5. (Optional) Check **"Publish GitHub Release with APKs?"** to create an official GitHub Release.
6. Click **Run workflow**. Once finished, your compressed APKs will appear in the Artifacts and Releases section!

---

### 🏷️ Creating Automated Releases with Git Tags

To build and publish an APK release automatically:
```bash
git tag v1.0.0
git push origin v1.0.0
```
GitHub Actions will automatically detect the tag, build both APK variants, package them into `Azuku-v1.0-APKs.zip`, and publish a new GitHub Release with the download links attached.

---

## 🛠️ Building Locally

You can also build the APKs locally using Gradle:

```bash
# Make gradlew executable (macOS / Linux)
chmod +x gradlew

# Build Debug APK
./gradlew assembleDebug

# Build Release APK
./gradlew assembleRelease

# The generated APKs will be located in:
# app/build/outputs/apk/debug/app-debug.apk
# app/build/outputs/apk/release/app-release.apk
```

---

## ✨ Azuku Core Features

- **3D Glass Dashboard**: Real-time daemon status (Wireless ADB Port 5555, Shizuku Binder IPC, Root `su`), one-tap startup, and animated 3D status ring.
- **Shell Terminal**: Interactive Linux shell (`azuku@android:/ $`) with built-in commands and persistent SQLite history via Room.
- **ADB Command Builder**: Visual constructor for `pm grant`, `appops set`, `wm size/density`, `dumpsys battery`, and `settings put`.
- **Package Manager & App Freezer**: Search, inspect, freeze (disable), unfreeze, install, and uninstall apps.
- **Permission Center**: Overview and deep-links for Special App Access and Privileged ADB Permissions.
- **Log Viewer (Logcat)**: Real-time Android log buffer reader with tag/level filters and one-tap export.
- **System Settings Editor**: Global, Secure, and System setting key editor.
- **Process Viewer**: Active process inspector with memory metrics and process termination.
- **Automation Integration**: Registered `AzukuAutomationReceiver` for Tasker, MacroDroid, Termux, and ADB broadcasts (`com.aistudio.azuku.EXEC_COMMAND`).
- **Android 11–16 Design**: Full edge-to-edge support, custom adaptive launcher icon, and Space Grotesk / Roboto Mono typography.
