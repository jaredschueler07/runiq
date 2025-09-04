# Dependency Injection (Hilt) Setup

This directory contains all Hilt dependency injection modules for the RunIQ application.

## Module Overview

### 🏗️ Core Modules

- **`AppModule`** - Application-level dependencies (SharedPreferences, CoroutineScope, Dispatchers)
- **`DatabaseModule`** - Room database and DAO providers
- **`NetworkModule`** - Retrofit, OkHttp, and API service configurations
- **`RepositoryModule`** - Repository interface bindings

### 🌐 Service Modules

- **`FirebaseModule`** - Firebase service configurations (Auth, Firestore, Storage, Analytics)
- **`HealthModule`** - Health Connect client and manager
- **`LocationModule`** - Location services and GPS tracking
- **`WorkerModule`** - WorkManager for background tasks

## Usage Examples

### ViewModel Injection
```kotlin
@HiltViewModel
class RunViewModel @Inject constructor(
    private val runRepository: RunRepository,
    private val healthRepository: HealthRepository
) : ViewModel()
```

### Service Injection
```kotlin
@AndroidEntryPoint
class LocationTrackingService : LifecycleService() {
    @Inject lateinit var locationManager: LocationManager
    @Inject lateinit var runRepository: RunRepository
}
```

### Repository Implementation
```kotlin
@Singleton
class RunRepositoryImpl @Inject constructor(
    private val runSessionDao: RunSessionDao,
    private val healthConnectManager: HealthConnectManager,
    private val firestoreService: FirestoreService
) : RunRepository
```

## Scoping Strategy

- **`@Singleton`** - Application lifetime (repositories, managers, database)
- **`@ViewModelScoped`** - ViewModel lifetime (use cases, temporary state)
- **`@ActivityScoped`** - Activity lifetime (UI-specific dependencies)

## Testing

Use `HiltTestRunner` in `androidTest` for integration tests:

```kotlin
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MyTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    
    @Inject
    lateinit var repository: MyRepository
    
    @Before
    fun setup() {
        hiltRule.inject()
    }
}
```

## Best Practices

1. **Use qualifiers** for multiple instances of the same type
2. **Prefer `@Binds`** over `@Provides` for interface implementations
3. **Keep modules focused** - single responsibility per module
4. **Use proper scoping** to avoid memory leaks
5. **Test injection** with dedicated test cases