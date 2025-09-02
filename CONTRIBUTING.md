# Contributing to RunIQ 🤝

Thank you for your interest in contributing to RunIQ! This document provides guidelines and information for contributors.

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or newer
- JDK 17+
- Git
- Basic knowledge of Kotlin and Android development

### Development Setup
1. Fork the repository
2. Clone your fork: `git clone https://github.com/YOUR_USERNAME/runiq.git`
3. Follow the setup instructions in [README.md](README.md)
4. Create a new branch: `git checkout -b feature/your-feature-name`

## 📋 Development Guidelines

### Code Style
We follow strict Kotlin coding standards:

#### Language Requirements
- **100% Kotlin**: No Java code allowed
- **Explicit Types**: All public APIs must declare explicit types
- **Immutability**: Prefer `val` over `var`, immutable collections
- **Early Returns**: Use guard clauses to reduce nesting

#### Naming Conventions
```kotlin
// Classes: PascalCase
class RunSessionRepository
data class CoachingMessage
sealed class RunState

// Functions/Properties: camelCase  
fun startRun()
val isRunning: Boolean

// Constants: SCREAMING_SNAKE_CASE
const val DEFAULT_WORKOUT_DURATION = 1800000L

// Packages: lowercase
com.runiq.data.repository
```

#### Architecture Patterns
- **Clean Architecture**: Separate domain, data, and presentation layers
- **MVVM**: ViewModels for UI state management
- **Repository Pattern**: Single source of truth with multiple data sources
- **Dependency Injection**: Use Hilt for all dependencies

### 🧪 Testing Requirements

#### Unit Tests
- Write unit tests for all business logic
- Use MockK for mocking dependencies
- Aim for >80% code coverage
- Test edge cases and error conditions

```kotlin
@Test
fun `when starting run, should write to Health Connect first`() = runTest {
    // Given
    val workoutType = WorkoutType.EASY_RUN
    coEvery { 
        healthConnectManager.writeRunSession(any()) 
    } returns Result.success("hc_123")
    
    // When
    val result = runRepository.startRun(workoutType)
    
    // Then
    assertTrue(result.isSuccess)
    coVerifySequence {
        runSessionDao.insert(any())
        healthConnectManager.writeRunSession(any())
        firestoreService.saveSession(any())
    }
}
```

#### Compose UI Tests
- Test all UI components with Compose testing
- Include accessibility testing
- Test different screen sizes and orientations

```kotlin
@Test
fun runScreenShowsCorrectMetrics() {
    composeTestRule.setContent {
        RunIQTheme {
            RunScreen(/* ... */)
        }
    }
    
    composeTestRule.onNodeWithText("5:00").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Pause run").assertIsEnabled()
}
```

### 🎨 UI/UX Guidelines

#### Compose Best Practices
- Use Material3 components exclusively
- Implement proper state hoisting
- Include `@Preview` for all composables
- Support both light and dark themes
- Ensure accessibility compliance

#### Performance
- Use `remember` and `derivedStateOf` for expensive computations
- Implement proper `key` parameters in `LazyColumn`/`LazyRow`
- Avoid unnecessary recomposition
- Profile memory and battery usage

### 🔒 Security Guidelines
- Never commit API keys or secrets
- Use `EncryptedSharedPreferences` for sensitive data
- Validate all external inputs
- Implement proper OAuth2 flows
- Follow Android security best practices

### 📊 Performance Standards
- **Battery Efficient**: Optimize background processing
- **Memory Conscious**: Profile and optimize memory usage
- **Responsive UI**: Maintain 60fps during interactions
- **Fast Startup**: App should launch in <3 seconds
- **Offline First**: Core functionality works without internet

## 🔄 Contribution Process

### 1. Planning
- Check existing issues and discussions
- Create an issue for new features or significant changes
- Discuss architecture decisions before implementation

### 2. Development
- Create a feature branch from `main`
- Follow the coding standards
- Write comprehensive tests
- Update documentation as needed

### 3. Quality Assurance
```bash
# Run before submitting PR
./gradlew ktlintFormat  # Format code
./gradlew ktlintCheck   # Check formatting
./gradlew lint          # Android lint
./gradlew test          # Unit tests
./gradlew connectedAndroidTest  # Instrumented tests
```

### 4. Pull Request
- Use the provided PR template
- Include screenshots/videos for UI changes
- Link to related issues
- Request review from maintainers

## 🏗️ Architecture Deep Dive

### Data Flow Priority
1. **Health Connect**: Primary source of truth for fitness data
2. **Room Database**: Local cache for offline access
3. **Firestore**: Cloud backup for AI analysis and sync

### Key Components

#### Repository Pattern
```kotlin
@Singleton
class RunRepositoryImpl @Inject constructor(
    private val healthConnectManager: HealthConnectManager,
    private val runSessionDao: RunSessionDao,
    private val firestoreService: FirestoreService
) : RunRepository {
    // Coordinate all data sources with proper error handling
}
```

#### State Management
```kotlin
sealed class RunState {
    data object Idle : RunState()
    data class Active(
        val duration: Long,
        val distance: Float,
        val currentPace: Float
    ) : RunState()
    data object Paused : RunState()
}
```

## 🎯 Areas for Contribution

### High Priority
- **AI Coaching Improvements**: Enhance coaching algorithms
- **Music Integration**: Improve BPM matching and playlist generation
- **Performance Optimization**: Battery and memory improvements
- **Accessibility**: Screen reader support and large text

### Medium Priority
- **New Workout Types**: Add cycling, walking, hiking support
- **Social Features**: Share runs, compete with friends
- **Advanced Analytics**: Detailed performance insights
- **Wearable Integration**: Wear OS companion app

### Documentation
- **Code Comments**: Improve inline documentation
- **Architecture Docs**: Detailed component documentation
- **User Guides**: Help documentation for users
- **API Documentation**: External integration guides

## 🐛 Bug Reports

When reporting bugs:
1. Use the bug report template
2. Include device information
3. Provide reproduction steps
4. Add logs if possible
5. Include screenshots/videos

## ❓ Questions & Support

- **GitHub Discussions**: For general questions and ideas
- **GitHub Issues**: For bugs and feature requests
- **Code Reviews**: Tag maintainers for urgent reviews

## 🏆 Recognition

Contributors will be recognized in:
- GitHub contributors list
- App credits section
- Release notes for significant contributions

## 📜 Code of Conduct

- Be respectful and inclusive
- Focus on constructive feedback
- Help newcomers get started
- Maintain professional communication

---

**Happy coding! 🎉**