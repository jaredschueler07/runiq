package com.runiq.data.local.database

import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.runiq.data.local.converters.Converters
import com.runiq.data.local.dao.*
import com.runiq.data.local.entities.*
import timber.log.Timber

/**
 * Room database for RunIQ app with comprehensive entity support
 */
@Database(
    entities = [
        RunSessionEntity::class,
        GpsTrackPointEntity::class,
        CoachEntity::class,
        CoachTextLineEntity::class,
        HealthMetricCacheEntity::class
    ],
    version = 1,
    exportSchema = true,
    autoMigrations = []
)
@TypeConverters(Converters::class)
abstract class RunIQDatabase : RoomDatabase() {
    
    // DAO abstract methods
    abstract fun runSessionDao(): RunSessionDao
    abstract fun gpsTrackDao(): GpsTrackDao
    abstract fun coachDao(): CoachDao
    abstract fun coachTextLineDao(): CoachTextLineDao
    abstract fun healthMetricDao(): HealthMetricDao
    
    companion object {
        private const val DATABASE_NAME = "runiq_database"
        
        @Volatile
        private var INSTANCE: RunIQDatabase? = null
        
        fun getInstance(context: android.content.Context): RunIQDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RunIQDatabase::class.java,
                    DATABASE_NAME
                )
                .addCallback(DatabaseCallback())
                .addMigrations(
                    // Add future migrations here
                    // MIGRATION_1_2,
                    // MIGRATION_2_3
                )
                .fallbackToDestructiveMigration() // Remove in production
                .build()
                
                INSTANCE = instance
                instance
            }
        }
        
        /**
         * For testing purposes - creates an in-memory database
         */
        fun getInMemoryDatabase(context: android.content.Context): RunIQDatabase {
            return Room.inMemoryDatabaseBuilder(
                context.applicationContext,
                RunIQDatabase::class.java
            )
            .addCallback(DatabaseCallback())
            .allowMainThreadQueries() // Only for testing
            .build()
        }
        
        /**
         * Clear all data from the database
         */
        suspend fun clearAllTables(database: RunIQDatabase) {
            database.clearAllTables()
        }
    }
}

/**
 * Database callback for initialization and setup
 */
private class DatabaseCallback : RoomDatabase.Callback() {
    
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        Timber.d("RunIQ Database created")
        
        // Initialize with default data if needed
        // This would typically be done in a background thread
        // Example: Insert default coaches, text lines, etc.
    }
    
    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        Timber.d("RunIQ Database opened")
        
        // Enable foreign key constraints
        db.execSQL("PRAGMA foreign_keys=ON")
        
        // Optimize database performance
        db.execSQL("PRAGMA journal_mode=WAL")
        db.execSQL("PRAGMA synchronous=NORMAL")
        db.execSQL("PRAGMA cache_size=10000")
        db.execSQL("PRAGMA temp_store=MEMORY")
    }
}

/**
 * Future migration examples - add these as needed
 */

// Example migration from version 1 to 2
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Example: Add new column
        // database.execSQL("ALTER TABLE run_sessions ADD COLUMN new_column TEXT")
        
        // Example: Create new table
        // database.execSQL("""
        //     CREATE TABLE IF NOT EXISTS new_table (
        //         id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
        //         name TEXT NOT NULL
        //     )
        // """)
        
        // Example: Create new index
        // database.execSQL("CREATE INDEX IF NOT EXISTS index_run_sessions_new_column ON run_sessions(new_column)")
    }
}

// Example migration from version 2 to 3
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Example: Rename table
        // database.execSQL("ALTER TABLE old_table RENAME TO new_table")
        
        // Example: Drop column (SQLite doesn't support DROP COLUMN directly)
        // 1. Create new table without the column
        // 2. Copy data from old table to new table
        // 3. Drop old table
        // 4. Rename new table to old table name
    }
}

/**
 * Database utilities and helper functions
 */
object DatabaseUtils {
    
    /**
     * Initialize the database with default data
     */
    suspend fun initializeWithDefaultData(database: RunIQDatabase) {
        try {
            // Insert default coaches
            val defaultCoaches = createDefaultCoaches()
            database.coachDao().insertAll(defaultCoaches)
            
            // Insert default coaching text lines
            val defaultTextLines = createDefaultTextLines()
            database.coachTextLineDao().insertAll(defaultTextLines)
            
            Timber.d("Database initialized with default data")
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize database with default data")
        }
    }
    
    /**
     * Create default coaches for the app
     */
    private fun createDefaultCoaches(): List<CoachEntity> {
        return listOf(
            CoachEntity(
                id = "coach_alex",
                name = "Alex",
                description = "Encouraging and supportive coach perfect for beginners",
                coachingStyle = CoachingStyle.ENCOURAGING,
                personalityTraits = listOf("supportive", "patient", "motivational"),
                voiceCharacteristics = com.runiq.domain.model.VoiceCharacteristics(
                    voiceId = "default_male",
                    voiceName = "Alex Voice"
                ),
                specializations = listOf("beginner", "motivation"),
                experienceLevel = ExperienceLevel.BEGINNER,
                motivationStyle = MotivationStyle.GENTLE,
                isActive = true,
                isPremium = false
            ),
            CoachEntity(
                id = "coach_sarah",
                name = "Sarah",
                description = "Data-driven coach focused on performance optimization",
                coachingStyle = CoachingStyle.ANALYTICAL,
                personalityTraits = listOf("analytical", "precise", "goal-oriented"),
                voiceCharacteristics = com.runiq.domain.model.VoiceCharacteristics(
                    voiceId = "default_female",
                    voiceName = "Sarah Voice"
                ),
                specializations = listOf("performance", "data", "advanced"),
                experienceLevel = ExperienceLevel.ADVANCED,
                motivationStyle = MotivationStyle.TACTICAL,
                isActive = true,
                isPremium = false
            ),
            CoachEntity(
                id = "coach_mike",
                name = "Mike",
                description = "High-energy coach who pushes you to your limits",
                coachingStyle = CoachingStyle.CHALLENGING,
                personalityTraits = listOf("energetic", "demanding", "competitive"),
                voiceCharacteristics = com.runiq.domain.model.VoiceCharacteristics(
                    voiceId = "default_energetic",
                    voiceName = "Mike Voice"
                ),
                specializations = listOf("speed", "intervals", "competition"),
                experienceLevel = ExperienceLevel.INTERMEDIATE,
                motivationStyle = MotivationStyle.INTENSE,
                isActive = true,
                isPremium = true
            )
        )
    }
    
    /**
     * Create default coaching text lines
     */
    private fun createDefaultTextLines(): List<CoachTextLineEntity> {
        return listOf(
            // Alex (Encouraging) - Motivation
            CoachTextLineEntity(
                coachId = "coach_alex",
                text = "You're doing great! Keep up that steady pace.",
                category = com.runiq.domain.model.TextCategory.MOTIVATION,
                conditions = listOf("phase:main", "pace:on_target"),
                priority = 7,
                emotionalTone = com.runiq.data.local.entities.EmotionalTone.ENCOURAGING
            ),
            CoachTextLineEntity(
                coachId = "coach_alex",
                text = "Remember, every step counts. You've got this!",
                category = com.runiq.domain.model.TextCategory.ENCOURAGEMENT,
                conditions = listOf("phase:main"),
                priority = 5,
                emotionalTone = com.runiq.data.local.entities.EmotionalTone.ENCOURAGING
            ),
            
            // Sarah (Analytical) - Pace guidance
            CoachTextLineEntity(
                coachId = "coach_sarah",
                text = "Your current pace is {pace} minutes per kilometer. Target pace is {target_pace}.",
                category = com.runiq.domain.model.TextCategory.PACE_GUIDANCE,
                conditions = listOf("phase:main"),
                templateVariables = listOf("pace", "target_pace"),
                priority = 8,
                emotionalTone = com.runiq.data.local.entities.EmotionalTone.NEUTRAL
            ),
            CoachTextLineEntity(
                coachId = "coach_sarah",
                text = "You're running {pace_difference} seconds faster than target. Consider slowing down slightly.",
                category = com.runiq.domain.model.TextCategory.PACE_GUIDANCE,
                conditions = listOf("pace:faster_than_target"),
                templateVariables = listOf("pace_difference"),
                priority = 9,
                emotionalTone = com.runiq.data.local.entities.EmotionalTone.NEUTRAL
            ),
            
            // Mike (Challenging) - Motivation
            CoachTextLineEntity(
                coachId = "coach_mike",
                text = "Come on! I know you can push harder than that!",
                category = com.runiq.domain.model.TextCategory.MOTIVATION,
                conditions = listOf("pace:slower_than_target"),
                priority = 9,
                emotionalTone = com.runiq.data.local.entities.EmotionalTone.CHALLENGING
            ),
            CoachTextLineEntity(
                coachId = "coach_mike",
                text = "That's the spirit! Keep that intensity up!",
                category = com.runiq.domain.model.TextCategory.ENCOURAGEMENT,
                conditions = listOf("pace:on_target", "hr:in_zone"),
                priority = 8,
                emotionalTone = com.runiq.data.local.entities.EmotionalTone.ENERGETIC
            )
        )
    }
    
    /**
     * Get database size in bytes
     */
    fun getDatabaseSize(context: android.content.Context): Long {
        val dbFile = context.getDatabasePath(DATABASE_NAME)
        return if (dbFile.exists()) dbFile.length() else 0L
    }
    
    /**
     * Export database for debugging (development only)
     */
    suspend fun exportDatabaseForDebugging(database: RunIQDatabase): DatabaseExport {
        return DatabaseExport(
            runSessionCount = database.runSessionDao().getCompletedRunCount("debug_user"),
            gpsPointCount = 0, // Would need to implement count query
            coachCount = database.coachDao().getActiveCoachCount(),
            textLineCount = database.coachTextLineDao().getActiveLineCount("coach_alex"),
            healthMetricCount = 0 // Would need to implement count query
        )
    }
}

/**
 * Data class for database export information
 */
data class DatabaseExport(
    val runSessionCount: Int,
    val gpsPointCount: Int,
    val coachCount: Int,
    val textLineCount: Int,
    val healthMetricCount: Int
)