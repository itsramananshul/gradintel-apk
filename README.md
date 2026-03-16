# Gradintel Android App

> Track · Predict · Improve — Native Android wrapper for Gradintel

## Get the APK (GitHub Actions — no installs needed)

### Step 1 — Create a GitHub repo
1. Go to **github.com** → click **+** → **New repository**
2. Name it `gradintel-android`, set to **Private** (or Public)
3. Click **Create repository**

### Step 2 — Upload this project
On the GitHub repo page, click **uploading an existing file** and drag in this entire folder.
Or use Git:
```bash
git init
git add .
git commit -m "Initial commit"
git remote add origin https://github.com/YOUR_USERNAME/gradintel-android.git
git push -u origin main
```

### Step 3 — Trigger the build
1. Go to your repo → **Actions** tab
2. Click **Build Gradintel APK** → **Run workflow** → **Run workflow**
3. Wait ~3–4 minutes

### Step 4 — Download your APK
1. Click the completed workflow run
2. Scroll down to **Artifacts**
3. Download **Gradintel-v1.0-debug**
4. Extract the zip → you have `app-debug.apk`

### Step 5 — Install on your phone
1. Transfer the APK to your Android phone (AirDrop-style via Files, Google Drive, email, etc.)
2. Open it on your phone
3. If prompted: **Settings → Install unknown apps → Allow**
4. Install ✅

---

## Build locally (Android Studio)

1. Download [Android Studio](https://developer.android.com/studio) (free)
2. Open Android Studio → **Open** → select this folder
3. Wait for Gradle sync (~2 min first time)
4. **Build** menu → **Build Bundle(s) / APK(s)** → **Build APK(s)**
5. Click **locate** in the notification → `app-debug.apk`

---

## App features

| Feature | Description |
|---|---|
| **Splash screen** | Animated logo, purple `#818cf8` theme, pop + fade animations |
| **Dark theme** | Exact match to website (`#07070e` background, purple/pink accents) |
| **Full WebView** | Renders your complete Gradintel web app |
| **Pull to refresh** | Swipe down to reload, themed spinner |
| **Offline detection** | Shows friendly error screen with retry button |
| **Back navigation** | Hardware back goes back in app history |
| **Edge-to-edge** | Transparent status bar, dark nav bar |
| **Safe area insets** | Notch/punch-hole aware — content never hidden |
| **Hardware accelerated** | Smooth 60fps WebView rendering |
| **Supabase compatible** | Internet permission + mixed content enabled |

---

## Project structure

```
gradintel-android/
├── .github/workflows/build.yml       ← Auto-build CI
├── app/
│   ├── build.gradle                  ← compileSdk 34, minSdk 24 (Android 7+)
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── assets/www/index.html     ← Your Gradintel website
│       └── java/com/gradintel/app/
│           ├── SplashActivity.kt     ← Branded splash
│           └── MainActivity.kt       ← WebView host
├── build.gradle
├── settings.gradle
└── gradle/wrapper/gradle-wrapper.properties
```

---

## Release / Play Store

To build a signed release APK for the Play Store:

1. Generate a keystore in Android Studio:
   **Build → Generate Signed Bundle/APK → APK → Create new keystore**
2. Add signing config to `app/build.gradle`
3. Run: `./gradlew assembleRelease`

Or tag a release on GitHub (`git tag v1.0.0 && git push --tags`) to trigger the automated release workflow.

---

## Minimum requirements
- Android 7.0 (API 24) or higher
- Internet connection (for Supabase auth and data sync)
