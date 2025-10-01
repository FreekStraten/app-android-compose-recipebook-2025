# RecipeBook (Android / Jetpack Compose)

![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4)
![Status](https://img.shields.io/badge/status-archived-inactive)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](./LICENSE)

**Rol:** Solo | **Jaar:** 2025

Jetpack Compose showcase app die TheMealDB-recepten combineert met een lichte eigen receptenbeheerder.

## Tech stack
- Kotlin
- Jetpack Compose + Material 3
- AndroidX Navigation Compose
- Retrofit & Gson voor TheMealDB
- Coil voor afbeeldingen
- Kotlin Coroutines Flow voor state management

## Highlights
- Online recepten zoeken, filteren en random ophalen via TheMealDB API.
- Schermflow online lijst -> detail -> lokale collectie, volledig in Compose.
- Eigen recepten aanmaken, bewerken en verwijderen in een in-memory repository met live updates.
- Afbeeldingencache en foutafhandeling voor netwerk en invoer.

## Demo
![RecipeBook demo](docs/demo.gif)

## Snel starten

### Optie A — Android Studio (aanrader)
1) **Vereisten:** Android Studio **Iguana** (of nieuwer), **JDK 17**, **Android SDK 35**  
2) **Clone & open:** `File → Open…` en selecteer de repo-root (niet alleen `/app`)  
3) **Sync:** wacht tot Gradle sync klaar is  
4) **Run:** kies een emulator of fysiek toestel (minSdk **24**) → **Run 'app'**

### Optie B — CLI (zonder Studio)
```bash
git clone https://github.com/FreekStraten/app-android-compose-recipebook-2025.git
cd app-android-compose-recipebook-2025
# macOS/Linux:
./gradlew assembleDebug
# Windows:
gradlew.bat assembleDebug

# Installeer op verbonden device/emulator:
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

**Netwerk/API:** werkt out-of-the-box met de openbare TheMealDB-endpoints (geen extra configuratie).  
**Permissions:** alleen internet-toegang (zie `AndroidManifest.xml`).

> **Build issues?**
> - Installeer Android **API 35** via SDK Manager (Tools → SDK Manager).
> - Zet **Gradle JDK** op **17** (Settings → Build Tools → Gradle → Gradle JDK).
