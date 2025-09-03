# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

RunIQ is an Android fitness AI coach app built with modern Android development practices. The app focuses on AI-powered running coaching, BPM-matched music integration, Health Connect integration, real-time GPS tracking, and voice coaching capabilities.

**Key Technologies:**
- Kotlin (100% - no Java)
- Jetpack Compose with Material3
- Clean Architecture + MVVM + Repository Pattern  
- Hilt/Dagger for DI
- Room Database + Health Connect + Firestore
- Coroutines + Flow + StateFlow
- Navigation Compose

## Build Commands

```bash
# Build the app
./gradlew assembleDebug

# Build release variant
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Run specific test class
./gradlew test --tests "com.runiq.domain.usecase.StartRunUseCaseTest"

# Run lint checks
./gradlew lint

# Clean build
./gradlew clean

# Generate test coverage report
./gradlew testDebugUnitTestCoverage
```

## Architecture Overview

### Clean Architecture Layers

**Domain Layer (`domain/`)**: Core business logic, no Android dependencies
- `model/`: Business entities (Run, Coach, etc.)
- `repository/`: Interface definitions
- `usecase/`: Business use cases

**Data Layer (`data/`)**: Multi-source data management
- **Primary**: Health Connect (fitness data source of truth)
- **Local Cache**: Room database (offline access)
- **Cloud Backup**: Firestore (AI analysis, sync)

**Presentation Layer (`presentation/`)**: UI and ViewModels
- `screens/`: Compose UI screens with ViewModels
- `theme/`: Material3 theming
- Clean state hoisting pattern

### Dependency Injection Structure

**Modules** (`di/`):
- `DatabaseModule.kt`: Room database configuration
- `NetworkModule.kt`: API clients, network configuration  
- `RepositoryModule.kt`: Repository implementations

**Service Layer** (`services/`):
- `LocationTrackingService.kt`: Foreground GPS tracking
- `CoachingService.kt`: AI coaching and TTS

## Key Development Patterns

### Data Flow Priority
1. **Health Connect** (primary source of truth for fitness data)
2. **Room Database** (local cache for offline access)  
3. **Firestore** (cloud backup for AI analysis and sync)

All repository operations coordinate between these three sources with proper error handling and fallback strategies.

### Hybrid AI Coaching System
- **Rule-based templates**: Fast, offline coaching messages
- **LLM fallback**: Complex/personalized messages via Gemini
- **TTS Integration**: Eleven Labs API with system TTS fallback

### State Management
- Use `StateFlow` for UI state in ViewModels
- Implement `sealed class` for result types and UI states
- Follow state hoisting principles in Compose
- Use `derivedStateOf` for computed values

## Code Conventions (from Cursor Rules)

### Naming
- Classes: PascalCase (`RunSessionRepository`)
- Functions/Properties: camelCase (`startRun()`, `isRunning`)
- Constants: SCREAMING_SNAKE_CASE (`DEFAULT_WORKOUT_DURATION`)
- Packages: lowercase (`com.runiq.data.repository`)

### File Organization
- Match class names or use lowercase with underscores
- Place components in: `app/src/main/java/com/runiq/ui/components/`
- Place screens in: `app/src/main/java/com/runiq/ui/screens/`
- Place tests in standard test directories

### Compose Best Practices
- Always include `@Preview` annotations for components
- Use `Modifier` as first parameter
- Implement proper state hoisting
- Use `remember` for expensive operations
- Follow Material3 design system

## Testing Strategy

### Unit Tests
- Test business logic in domain layer
- Mock external dependencies with Mockk
- Use `runTest` for coroutine testing
- Verify sequential operations with `coVerifySequence`

### Compose UI Tests
- Use `ComposeTestRule` for UI testing
- Test user interactions and state changes
- Verify accessibility with content descriptions
- Include both light and dark mode previews

## Firebase Integration

The project includes Firebase/Google Services:
- **Firebase Analytics**: Usage tracking and insights
- **Firebase BoM**: Version coordination for Firebase dependencies
- **Google Services Plugin**: Required for Firebase integration

Configuration files:
- `google-services.json`: Firebase project configuration (app-level)
- Build plugins include `com.google.gms.google-services`

## Key Configuration Files

- **Application ID**: `com.example.runiq`
- **Min SDK**: 30, **Target SDK**: 36
- **Java Version**: 11
- **Kotlin Version**: 2.0.21
- **AGP Version**: 8.12.2

## Performance Considerations

### Battery Optimization
- Use appropriate location accuracy based on workout type
- Implement geofencing for auto-pause detection
- Batch network requests when possible
- Use WorkManager for background sync

### Memory Management
- Use appropriate Dispatchers (IO for disk/network, Default for CPU work)
- Implement proper caching with Room and in-memory caches
- Use LazyColumn/LazyRow with proper keys
- Profile regularly with Android Studio Profiler

## Security Guidelines

- Store API keys in `local.properties` or secure storage, never in code
- Use `EncryptedSharedPreferences` for sensitive data
- Implement certificate pinning for API calls
- Never log sensitive information
- Use ProGuard/R8 rules properly

## Error Handling Pattern

```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    data object Loading : Result<Nothing>()
}
```

Always wrap repository operations in try-catch blocks with proper logging using Timber, and provide meaningful error states to the UI layer.