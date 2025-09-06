# KSP Build Error Troubleshooting Guide

## Initial Problem
**Error**: `e: [ksp] [MissingType]: Element 'com.runiq.data.local.converters.Converters' references a type that is not present`

This error prevented Room database and Dagger/Hilt from generating necessary code during the KSP (Kotlin Symbol Processing) phase.

## Root Cause Analysis
The KSP MissingType error was caused by multiple issues:
1. Missing TypeConverter implementations for complex data types used in Room entities
2. Database index column name mismatches (camelCase vs snake_case)
3. Missing enum definitions and enum value mismatches
4. Syntax errors in entity files
5. Problematic imports and complex type converters causing KSP processing failures

## Troubleshooting Steps Taken

### 1. Fixed Syntax Errors
**Files**: `CoachingMessage.kt`, `WorkoutType.kt`, `RunRepositoryImpl.kt`
- **Issue**: Extra closing braces causing "Expecting a top level declaration" errors
- **Fix**: Removed extra `}` characters from end of files
- **Result**: ✅ Syntax errors resolved

### 2. Created Missing Enum Definitions
**File**: `domain/model/TextCategory.kt` (Created)
- **Issue**: `TextCategory` enum was referenced but didn't exist
- **Fix**: Created comprehensive enum with 20 categories:
```kotlin
enum class TextCategory {
    MOTIVATION, PACE_GUIDANCE, FORM_CORRECTION, BREATHING_TIP,
    HYDRATION_REMINDER, ACHIEVEMENT, WARNING, ENCOURAGEMENT,
    TECHNICAL_TIP, MILESTONE, WORKOUT_UPDATE, START_RUN,
    END_RUN, INTERVAL_START, INTERVAL_END, WARM_UP,
    COOL_DOWN, RECOVERY, CHALLENGE, POSITIVE_FEEDBACK
}
```
- **Result**: ✅ Missing type reference resolved

### 3. Fixed Enum Value Mismatches
**File**: `data/local/entities/CoachingEnums.kt`
- **Issue**: Code was using `MotivationStyle.BALANCED` but enum had `MIXED`
- **Fix**: Changed `MIXED` to `BALANCED` in enum definition
- **Result**: ✅ Enum value mismatch resolved

### 4. Rewrote Repository Implementation
**File**: `data/repository/RunRepositoryImpl.kt`
- **Issues**: 
  - Incorrect Result API usage (`Result.failure` vs `Result.Error`)
  - Wrong DAO method calls
  - Missing imports
- **Fix**: Complete rewrite with proper:
  - Result API (`Result.Success`, `Result.Error`)
  - DAO method calls matching actual interface
  - Proper imports and error handling
- **Result**: ✅ Repository implementation aligned with interfaces

### 5. Fixed DI Module Issues
**Files**: `di/AppModule.kt`, `di/FirebaseModule.kt`, `di/HealthModule.kt`, `di/LocationModule.kt`
- **Issue**: Bindings to non-existent implementation classes
- **Fix**: Commented out problematic bindings to prevent "error.NonExistentClass" errors
- **Result**: ✅ DI configuration temporarily resolved

### 6. Addressed Database Schema Issues

#### Index Column Name Mismatches
**Files**: `RunSessionEntity.kt`, `GpsTrackPointEntity.kt`, `CoachTextLineEntity.kt`, `CoachEntity.kt`
- **Issue**: Index definitions using camelCase but entities using snake_case column names
- **Fixes**:
  - `RunSessionEntity.kt`: Changed `["user_id", "start_time"]` etc.
  - `GpsTrackPointEntity.kt`: Changed `["session_id", "timestamp"]` etc.
  - `CoachTextLineEntity.kt`: Changed `["coach_id", "category"]` etc.
  - `CoachEntity.kt`: Changed `["is_active"]`, `["coaching_style"]`
- **Result**: ✅ Database schema consistency improved

#### Foreign Key Mismatches
- **Issue**: Foreign key references using camelCase column names
- **Fix**: Updated foreign key `childColumns` to use snake_case names
- **Result**: ✅ Foreign key constraints properly defined

### 7. TypeConverter Troubleshooting

#### Initial Approach - Wildcard Import Removal
**File**: `Converters.kt`
- **Issue**: Wildcard imports potentially causing KSP resolution issues
- **Fix**: Replaced wildcard imports with explicit imports
- **Result**: ❌ KSP error persisted

#### Isolation Testing Approach
**Strategy**: Progressively simplified Converters to isolate problematic types
1. **Minimal Converters**: Started with only `WorkoutType` and `SyncStatus`
2. **Incremental Addition**: Added enum converters one by one
3. **Complex Type Testing**: Added Gson-based converters progressively

#### Successfully Added TypeConverters
```kotlin
// Enum converters (Working)
- WorkoutType ↔ String
- SyncStatus ↔ String  
- CoachingStyle ↔ String
- ExperienceLevel ↔ String
- MotivationStyle ↔ String
- TextCategory ↔ String
- EmotionalTone ↔ String

// List converters (Working)  
- List<String> ↔ JSON (using Gson)
```

#### Problematic TypeConverters
```kotlin
// These caused KSP MissingType errors:
- VoiceCharacteristics ↔ JSON (using Gson)
- List<CoachingMessage> ↔ JSON (using Gson)
```

### 8. Key Discoveries

#### KSP MissingType Trigger
- **Working State**: Enum-only converters with explicit imports
- **Failure Trigger**: Adding Gson-based complex object converters
- **Hypothesis**: VoiceCharacteristics class or CoachingMessage class may have unresolved dependencies during KSP processing

#### Build State Progression
1. **Initial**: Complete KSP failure with MissingType error
2. **After Syntax Fixes**: KSP progressed but still failed
3. **After Enum Fixes**: KSP progressed further with detailed error messages
4. **Minimal Converters**: KSP processed successfully but with missing converter warnings
5. **Complex Converters Added**: KSP MissingType error returned

## Current Status

### ✅ Successfully Resolved
- Syntax errors in entity files
- Missing TextCategory enum definition
- Enum value mismatches (MIXED → BALANCED)
- Repository implementation alignment
- Database index column name consistency
- Basic enum TypeConverters working
- List<String> TypeConverters working

### ❌ Still Problematic
- VoiceCharacteristics TypeConverter causes KSP MissingType error
- Build warnings about missing TypeConverters for complex objects
- Some entity relationships may still have issues

### 🔄 Partial Success
- KSP processes successfully with basic converters
- Database schema mostly consistent
- Build progresses much further than initially

## Recommended Next Steps

1. **Investigate VoiceCharacteristics Dependencies**
   - Check if VoiceCharacteristics class has unresolved imports
   - Verify all referenced types are available during KSP processing

2. **Alternative TypeConverter Approaches**
   - Consider using Moshi instead of Gson for JSON serialization
   - Implement custom TypeConverters without external JSON libraries
   - Use Room's built-in serialization for simple cases

3. **Incremental Testing Strategy**
   - Add one complex TypeConverter at a time
   - Test build after each addition
   - Isolate which specific type causes KSP failure

4. **Schema Validation**
   - Run Room schema validation
   - Ensure all entity relationships are properly defined
   - Verify all referenced foreign keys exist

## Lessons Learned

### KSP Error Debugging Strategy
1. **Start Simple**: Begin with minimal converters and build up
2. **Explicit Imports**: Avoid wildcard imports that may confuse KSP
3. **Incremental Testing**: Add one converter at a time to isolate issues
4. **Type Availability**: Ensure all referenced types are available during KSP processing phase

### Room Database Best Practices
1. **Consistent Naming**: Use snake_case for all database column names
2. **Index Alignment**: Ensure index definitions match actual column names
3. **Foreign Key Validation**: Verify all foreign key references use correct column names
4. **TypeConverter Simplicity**: Prefer simple converters over complex ones when possible

### Build Error Investigation
1. **Read Full Error Messages**: KSP provides detailed error context when it can process
2. **Systematic Elimination**: Remove potential causes one by one
3. **Version Dependencies**: Consider if annotation processors have conflicting requirements
4. **Clean Builds**: Use `./gradlew clean` between major changes

## File Change Summary

### Created Files
- `domain/model/TextCategory.kt` - Complete enum definition

### Modified Files
- `data/local/converters/Converters.kt` - Multiple iterations of TypeConverter implementations
- `data/local/entities/RunSessionEntity.kt` - Fixed index column names
- `data/local/entities/GpsTrackPointEntity.kt` - Fixed index column names and foreign keys
- `data/local/entities/CoachTextLineEntity.kt` - Fixed index column names and foreign keys
- `data/local/entities/CoachEntity.kt` - Fixed index column names
- `data/local/entities/CoachingEnums.kt` - Changed MIXED to BALANCED
- `data/repository/RunRepositoryImpl.kt` - Complete rewrite
- `di/AppModule.kt` - Commented out problematic bindings
- `di/FirebaseModule.kt` - Commented out problematic bindings
- `di/HealthModule.kt` - Commented out problematic bindings
- `di/LocationModule.kt` - Commented out problematic bindings

### Syntax Error Fixes
- `domain/model/CoachingMessage.kt` - Removed extra closing brace
- `domain/model/WorkoutType.kt` - Removed extra closing brace
- `data/repository/RunRepositoryImpl.kt` - Removed extra closing brace (before rewrite)

This troubleshooting session significantly improved the build state, resolving the majority of issues while identifying the specific remaining challenge with complex TypeConverter implementations.