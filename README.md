# Freader

Freader is a high-fidelity, open-source ebook and audiobook reader built with **Kotlin Multiplatform (KMP)** and **Compose Multiplatform**. It features a modern, clean Material 3 design, delivering a seamless reading and listening experience across Android, Desktop (JVM), and iOS.

## 🚀 Key Achievements (Current State)

### 📱 Fully Immersive UI & Navigation
- **Navigation Shell**: A robust architecture using **Voyager** for screen-based navigation. Features an adaptive layout (Mobile Drawer/Tabs vs. Desktop Sidebar).
- **First-Run Experience**: A beautiful, interactive **Onboarding** flow that introduces the app's core capabilities.
- **Root Screens**:
    - **Home**: Trending book carousel and recent reading history.
    - **Library**: Adaptive grid view with powerful **Sort & Filter** sheets.
    - **Collections**: Folder-based organization for your media.
    - **Discover**: Immersive import and processing flow for new books.
- **Immersive Sub-pages**:
    - **Reader**: Distraction-free reading with a context-aware **Selection Toolbar**, font/theme controls, and Table of Contents navigation.
    - **Comic Reader**: Specialized UI for **CBZ/CBR** formats, featuring Manga Mode (RTL support) and automatic chapter/volume transitions.
    - **TTS Player**: Podcast-inspired audio player interface.
    - **Metadata Editor**: Edit title, author, description, and genre tags for any book.
    - **Reading Stats**: Visual insights into reading progress, activity charts, and achievements.

### 💾 Persistence Layer
- **SQLDelight Integration**: Fully integrated cross-platform database (`FreaderDatabase`) to persist library state, bookmarks, and reading progress across app restarts.
- **Reactive Architecture**: Uses `Flow` and `ViewModel` (ScreenModels) to ensure the UI stays in sync with the database at all times.

## 🛠 Tech Stack
- **Framework**: Compose Multiplatform
- **Navigation**: Voyager
- **Design System**: Material 3 (with custom theming & adaptive layouts)
- **Persistence**: SQLDelight (KMP)
- **Concurrency**: Kotlin Coroutines & Flow
- **Build System**: Gradle with Version Catalogs

## 🚦 Getting Started
1. **Sync Gradle**: Open in Android Studio and sync with Gradle.
2. **Database**: The app initializes an SQLite database on the first run.
3. **Run**: 
   - Android: Select `composeApp` run configuration.
   - Desktop: Run `./gradlew :composeApp:run`

## 📝 Roadmap & Future Implementation
- [ ] **Real Engine Integration**: Integrate PDF/EPUB parsers and image renderers.
- [ ] **File I/O**: Implement platform-specific file system access (Android Scoped Storage / Desktop FileChooser).
- [ ] **TTS Engine**: Bridge Android/iOS native TTS libraries.
- [ ] **Cloud Sync**: Implement Google Drive/Dropbox sync logic.
