# Freader

Freader is an open-source ebook and audiobook reader built with **Kotlin Multiplatform (KMP)** and **Compose Multiplatform**. It features a modern, clean Material 3 design, delivering a seamless reading and listening experience across Android, Desktop (JVM), and iOS.

## 🚀 Key Achievements

### 📱 Fully Immersive UI & Navigation
- **Navigation Shell**: A robust architecture using **Voyager** for screen-based navigation with adaptive layouts.
- **Library & Reader**: Includes specialized support for PDF, EPUB, MOBI, and Comic formats with an adaptive UI.
- **Immersive Experiences**: Distraction-free reader, comic viewer, and robust tag/metadata management.

### 💾 Persistence Layer
- **SQLDelight Integration**: Cross-platform database to persist library state, bookmarks, and reading progress.

## 🛠 Tech Stack
- **Framework**: Compose Multiplatform
- **Navigation**: Voyager
- **Design System**: Material 3
- **Persistence**: SQLDelight (KMP)
- **Concurrency**: Kotlin Coroutines & Flow
- **Build System**: Gradle with Version Catalogs (Modern KMP structure)

## 🏗 Project Structure
The project follows the modern **KMP + Android Application** structure:
- `:androidApp`: Pure Android application module (Entry point for Android).
- `:composeApp`: Pure Kotlin Multiplatform library module containing common UI and core logic.
- `:shared`: Core business logic and domain models shared across all platforms.

## 🚦 Getting Started
1. **Sync Gradle**: Open in Android Studio and sync with Gradle.
2. **Database**: The app automatically initializes the database on the first run.
3. **Run**:
   - **Android**: Run the `:androidApp` module.
   - **Desktop**: Run `./gradlew :composeApp:run`.

## 📝 Roadmap
- [ ] **File I/O**: Refine cross-platform file access.
- [ ] **TTS Engine**: Integrate platform-specific text-to-speech.
- [ ] **Cloud Sync**: Implement drive-based synchronization.
