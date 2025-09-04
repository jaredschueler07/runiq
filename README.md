# RunIQ - AI-Powered Running Coach 🏃‍♂️🤖

[![Android CI](https://github.com/jaredschueler07/runiq/actions/workflows/android-ci.yml/badge.svg)](https://github.com/jaredschueler07/runiq/actions/workflows/android-ci.yml)
[![API](https://img.shields.io/badge/API-28%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=28)
[![Kotlin](https://img.shields.io/badge/kotlin-2.0.21-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-28-green.svg)](https://developer.android.com/studio/releases/platforms#8.0)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-36-green.svg)](https://developer.android.com/studio/releases/platforms)

RunIQ is an intelligent Android fitness application that serves as your personal AI running coach. It combines real-time GPS tracking, heart rate monitoring, AI-powered coaching, and BPM-matched music to create the ultimate running experience.

## ✨ Features

### 🤖 AI Coaching
- **Personalized AI Coach**: Choose from multiple AI coaches with unique personalities and coaching styles
- **Real-time Voice Coaching**: Get motivational and technical guidance during your runs
- **Adaptive Training Plans**: AI-generated training programs that adapt to your progress
- **Performance Analysis**: Post-run insights and recommendations for improvement

### 🎵 Smart Music Integration
- **BPM Matching**: Automatically match music tempo to your running cadence
- **Spotify Integration**: Seamless integration with your Spotify playlists
- **Dynamic Playlist Generation**: AI-curated playlists based on your workout type and mood

### 📊 Health & Fitness Tracking
- **Health Connect Integration**: Sync with Android's unified health platform
- **GPS Tracking**: Accurate route tracking with detailed maps
- **Heart Rate Monitoring**: Real-time HR zones and analysis
- **Comprehensive Metrics**: Distance, pace, cadence, elevation, and more

### 🔄 Data Synchronization
- **Multi-platform Sync**: Seamless data sync across devices
- **Offline Support**: Full functionality without internet connection
- **Export Options**: Share your data with other fitness platforms

## 🛠️ Technology Stack

- **Language**: Kotlin 2.0.21 (100% Kotlin, no Java)
- **UI Framework**: Jetpack Compose with Material3
- **Architecture**: Clean Architecture + MVVM + Repository Pattern
- **Dependency Injection**: Hilt/Dagger
- **Database**: Room (local) + Health Connect (primary) + Firestore (cloud sync)
- **Async Programming**: Coroutines + Flow + StateFlow
- **Navigation**: Navigation Compose
- **Testing**: JUnit, Mockk, Compose UI Testing

### External Integrations
- **Health Connect API**: Primary health data source
- **Spotify SDK**: Music streaming and playlist management
- **Eleven Labs API**: High-quality text-to-speech for coaching
- **Google Gemini AI**: Intelligent coaching and analysis
- **Firebase Suite**: Analytics, Crashlytics, and cloud storage
- **Google Maps**: Route visualization and navigation

## 🚀 Getting Started

### Prerequisites

- **Android Studio**: Latest stable version (Hedgehog or newer)
- **JDK**: 17 or higher
- **Android SDK**: API level 28+ (Android 9.0)
- **Git**: For version control

### Required API Keys

Before building the app, you'll need to obtain API keys for the following services:

1. **Google Services** (`google-services.json`)
   - Create a project in [Firebase Console](https://console.firebase.google.com/)
   - Download `google-services.json` and place it in `app/` directory

2. **Spotify API**
   - Register at [Spotify Developer Dashboard](https://developer.spotify.com/dashboard)
   - Create `spotify_client_secret` file in project root

3. **Eleven Labs API**
   - Sign up at [Eleven Labs](https://elevenlabs.io/)
   - Create `eleven_labs_api_key` file in project root

4. **Google Gemini AI**
   - Get API key from [Google AI Studio](https://makersuite.google.com/)
   - Create `gemini_api_key` file in project root

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/jaredschueler07/runiq.git
   cd runiq
   ```

2. **Set up API keys**
   ```bash
   # Create API key files (add your actual keys)
   echo "your_spotify_client_secret_here" > spotify_client_secret
   echo "your_eleven_labs_api_key_here" > eleven_labs_api_key
   echo "your_gemini_api_key_here" > gemini_api_key
   ```

3. **Add Google Services configuration**
   - Place your `google-services.json` file in the `app/` directory

4. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned repository and select it

5. **Sync and Build**
   ```bash
   ./gradlew clean build
   ```

6. **Run the app**
   - Connect an Android device (API 28+) or start an emulator
   - Click "Run" in Android Studio or use: `./gradlew installDebug`

### Health Connect Setup

RunIQ requires Health Connect to be installed on the device:

1. **Install Health Connect**
   - Download from [Google Play Store](https://play.google.com/store/apps/details?id=com.google.android.apps.healthdata)
   - Or install via ADB for testing: `adb install health-connect.apk`

2. **Grant Permissions**
   - Open RunIQ and follow the permission setup flow
   - Grant access to: Exercise sessions, Heart rate, Distance, Steps, Location

## 🏗️ Project Structure

```
app/src/main/java/com/runiq/
├── data/
│   ├── local/              # Room database, DAOs, entities
│   ├── remote/             # API services, DTOs
│   └── repository/         # Repository implementations
├── domain/
│   ├── model/              # Business models
│   ├── repository/         # Repository interfaces
│   └── usecase/            # Business logic use cases
├── presentation/
│   ├── screens/            # Compose screens
│   │   ├── run/           # Running screens
│   │   ├── profile/       # User profile
│   │   └── coaching/      # AI coaching setup
│   ├── theme/             # Material3 theming
│   └── components/        # Reusable UI components
├── di/                    # Hilt modules
└── services/              # Background services
```

## 🧪 Testing

### Run Unit Tests
```bash
./gradlew test
```

### Run Instrumented Tests
```bash
./gradlew connectedAndroidTest
```

### Run Lint Checks
```bash
./gradlew lint
```

### Code Formatting
```bash
./gradlew ktlintFormat
```

## 🔧 Development

### Code Style

This project follows strict Kotlin coding standards:

- **100% Kotlin**: No Java code
- **Explicit Types**: All public APIs must declare explicit types
- **Immutability**: Prefer `val` over `var`, immutable collections
- **Early Returns**: Use guard clauses to reduce nesting
- **Sealed Classes**: For state management and result types

### Architecture Principles

1. **Clean Architecture**: Clear separation of concerns across layers
2. **Single Source of Truth**: Health Connect as primary data source
3. **Offline First**: Full functionality without internet
4. **Performance**: Battery-efficient background processing
5. **Accessibility**: Full support for accessibility services

### Contributing

1. **Fork the repository**
2. **Create a feature branch**: `git checkout -b feature/amazing-feature`
3. **Follow the coding standards** and run `./gradlew ktlintFormat`
4. **Write tests** for new functionality
5. **Commit your changes**: `git commit -m 'Add amazing feature'`
6. **Push to the branch**: `git push origin feature/amazing-feature`
7. **Open a Pull Request**

## 📱 Minimum Requirements

- **Android 9.0** (API level 28) or higher
- **RAM**: 3GB minimum, 4GB+ recommended
- **Storage**: 100MB for app + space for run data
- **GPS**: Required for accurate tracking
- **Internet**: Required for initial setup and AI features

## 🔒 Privacy & Security

- **Local Data**: All run data stored locally first
- **Health Connect**: Primary health data integration
- **Encryption**: Sensitive data encrypted at rest
- **No Tracking**: We don't track or sell your personal data
- **Open Source**: Transparent about data handling

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🤝 Support

- **Issues**: [GitHub Issues](https://github.com/jaredschueler07/runiq/issues)
- **Discussions**: [GitHub Discussions](https://github.com/jaredschueler07/runiq/discussions)
- **Email**: support@runiq.app

## 🙏 Acknowledgments

- **Health Connect Team**: For the unified health platform
- **Jetpack Compose Team**: For the modern UI toolkit
- **Spotify**: For music streaming integration
- **Eleven Labs**: For high-quality text-to-speech
- **Google AI**: For Gemini integration

---

**Made with ❤️ for the running community**