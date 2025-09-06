# RunIQ Testing Foundation

This document describes the comprehensive testing infrastructure set up for the RunIQ Android app.

## 🏗️ Testing Architecture

### Base Test Classes

- **BaseUnitTest**: Foundation for all unit tests with MockK and coroutine setup
- **BaseDaoTest**: Specialized for Room DAO testing with in-memory database
- **BaseViewModelTest**: ViewModel testing with coroutine test scope
- **BaseRepositoryTest**: Repository testing with common mocking patterns

### Test Utilities

- **TestDispatcherRule**: Replaces main dispatcher for synchronous test execution
- **TestCoroutineRule**: Extended coroutine testing with test scope and scheduler
- **TestDataFactory**: Consistent test data creation with realistic defaults
- **TestExtensions**: Helper functions for LiveData and Flow testing
- **MockkHelpers**: Common MockK patterns and DSL for readable test setup

## 🧪 Test Categories

### 1. Domain Model Tests
- `RunSessionTest`: Tests business logic and calculated properties
- `GpsTrackPointTest`: Tests GPS calculations and utility functions

### 2. Data Layer Tests
- `RunSessionDaoTest`: Database operations for run sessions
- `GpsTrackDaoTest`: GPS track point database operations
- `RunRepositoryImplTest`: Repository coordination and data source management

### 3. Domain Layer Tests
- `StartRunUseCaseTest`: Business logic validation and error handling

### 4. Presentation Layer Tests
- `RunViewModelTest`: State management and user interaction handling

### 5. Integration Tests
- `RunSessionIntegrationTest`: End-to-end flow testing across all layers

### 6. Utility Tests
- `TestDataFactoryTest`: Validates test data creation
- `TestExtensionsTest`: Tests custom testing utilities

## 🚀 Running Tests

### All Tests
```bash
./gradlew app:testDebugUnitTest
```

### Specific Test Classes
```bash
./gradlew app:testDebugUnitTest --tests "com.runiq.domain.model.*"
./gradlew app:testDebugUnitTest --tests "com.runiq.data.repository.*"
```

### With Coverage
```bash
./gradlew app:testDebugUnitTestCoverage
```

## 📊 Test Coverage Goals

- **Data Layer**: >70% coverage (DAOs, Repositories)
- **Domain Layer**: >80% coverage (Use Cases, Models)
- **Presentation Layer**: >60% coverage (ViewModels)

## 🔧 Test Configuration

### Dependencies
All testing dependencies are configured in `app/build.gradle.kts`:
- JUnit 4 for test framework
- MockK for mocking
- Turbine for Flow testing
- Robolectric for Android component testing
- Architecture Components testing utilities

### Test Runner
- Unit tests use standard JUnit runner
- Android tests use custom `HiltTestRunner` for dependency injection
- Robolectric tests use `RobolectricTestRunner` for Android components

## 💡 Testing Best Practices

### 1. Test Structure
Follow the Given-When-Then pattern:
```kotlin
@Test
fun `should do something when condition met`() = runTest {
    // Given
    val input = TestDataFactory.createRunSession()
    
    // When
    val result = repository.saveSession(input)
    
    // Then
    assertTrue(result.isSuccess)
}
```

### 2. Mock Setup
Use relaxed mocks and clear setup:
```kotlin
@Before
fun setUp() {
    repository = mockk(relaxed = true)
    coEvery { repository.startRun(any(), any(), any()) } returns Result.success(mockSession)
}
```

### 3. Flow Testing
Use Turbine for Flow testing:
```kotlin
flow.test {
    val item = awaitItem()
    assertEquals(expected, item)
    awaitComplete()
}
```

### 4. Coroutine Testing
Use test dispatchers and scopes:
```kotlin
@get:Rule
val testDispatcherRule = TestDispatcherRule()

@Test
fun `test coroutine function`() = runTest {
    // Test coroutine code here
}
```

## 🐛 Common Issues

### 1. Database Constraints
Remove foreign key constraints in test environments if they cause issues with test data setup.

### 2. Timing Issues
Use appropriate test dispatchers and avoid real delays in tests.

### 3. Mock Verification
Ensure mocks are set up before the code under test runs.

## 📈 Next Steps

1. Add more specific business logic tests as features are implemented
2. Add UI tests with Compose testing framework
3. Set up test coverage reporting
4. Integrate with CI/CD pipeline
5. Add performance tests for critical paths

## 🔍 Test Reports

Test reports are generated at:
- Unit tests: `app/build/reports/tests/testDebugUnitTest/index.html`
- Coverage: `app/build/reports/coverage/testDebugUnitTestCoverage/html/index.html`