package com.runiq.data.local.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.runiq.data.local.entities.*
import com.runiq.domain.model.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RunIQDatabaseTest {
    
    private lateinit var database: RunIQDatabase
    
    @Before
    fun createDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RunIQDatabase::class.java
        ).allowMainThreadQueries().build()
    }
    
    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }
    
    @Test
    fun databaseCreationAndAccess() = runTest {
        // Test that all DAOs are accessible
        assertNotNull("RunSessionDao should be accessible", database.runSessionDao())
        assertNotNull("GpsTrackDao should be accessible", database.gpsTrackDao())
        assertNotNull("CoachDao should be accessible", database.coachDao())
        assertNotNull("CoachTextLineDao should be accessible", database.coachTextLineDao())
        assertNotNull("HealthMetricDao should be accessible", database.healthMetricDao())
    }
    
    @Test
    fun foreignKeyConstraints() = runTest {
        val runSessionDao = database.runSessionDao()
        val gpsTrackDao = database.gpsTrackDao()
        
        // Insert a run session
        val session = RunSessionEntity(
            sessionId = "test_session",
            userId = "test_user",
            startTime = System.currentTimeMillis(),
            workoutType = WorkoutType.EASY_RUN,
            coachId = "test_coach"
        )
        runSessionDao.insert(session)
        
        // Insert GPS points
        val gpsPoints = listOf(
            GpsTrackPointEntity(
                sessionId = "test_session",
                latitude = 37.7749,
                longitude = -122.4194,
                timestamp = System.currentTimeMillis(),
                sequenceNumber = 0
            ),
            GpsTrackPointEntity(
                sessionId = "test_session",
                latitude = 37.7750,
                longitude = -122.4195,
                timestamp = System.currentTimeMillis() + 1000,
                sequenceNumber = 1
            )
        )
        gpsTrackDao.insertAll(gpsPoints)
        
        // Verify GPS points exist
        val retrievedPoints = gpsTrackDao.getBySession("test_session")
        assertEquals("Should have 2 GPS points", 2, retrievedPoints.size)
        
        // Delete the session - GPS points should be cascade deleted
        runSessionDao.deleteById("test_session")
        
        val remainingPoints = gpsTrackDao.getBySession("test_session")
        assertEquals("GPS points should be deleted with session", 0, remainingPoints.size)
    }
    
    @Test
    fun coachAndTextLineRelationship() = runTest {
        val coachDao = database.coachDao()
        val textLineDao = database.coachTextLineDao()
        
        // Insert a coach
        val coach = CoachEntity(
            id = "test_coach",
            name = "Test Coach",
            description = "A test coach",
            coachingStyle = CoachingStyle.ENCOURAGING,
            personalityTraits = listOf("friendly"),
            voiceCharacteristics = VoiceCharacteristics("test_voice", "Test Voice")
        )
        coachDao.insert(coach)
        
        // Insert text lines for the coach
        val textLines = listOf(
            CoachTextLineEntity(
                coachId = "test_coach",
                text = "Great job!",
                category = TextCategory.ENCOURAGEMENT
            ),
            CoachTextLineEntity(
                coachId = "test_coach",
                text = "Keep up the pace!",
                category = TextCategory.MOTIVATION
            )
        )
        textLineDao.insertAll(textLines)
        
        // Verify text lines exist
        val retrievedLines = textLineDao.getByCoach("test_coach")
        assertEquals("Should have 2 text lines", 2, retrievedLines.size)
        
        // Delete the coach - text lines should be cascade deleted
        coachDao.deleteById("test_coach")
        
        val remainingLines = textLineDao.getByCoach("test_coach")
        assertEquals("Text lines should be deleted with coach", 0, remainingLines.size)
    }
    
    @Test
    fun healthMetricCacheRelationship() = runTest {
        val runSessionDao = database.runSessionDao()
        val healthMetricDao = database.healthMetricDao()
        
        // Insert a run session
        val session = RunSessionEntity(
            sessionId = "test_session",
            userId = "test_user",
            startTime = System.currentTimeMillis(),
            workoutType = WorkoutType.EASY_RUN,
            coachId = "test_coach"
        )
        runSessionDao.insert(session)
        
        // Insert health metrics
        val metrics = listOf(
            HealthMetricCacheEntity(
                sessionId = "test_session",
                metricType = HealthMetricType.HEART_RATE,
                value = 140.0,
                unit = "bpm",
                timestamp = System.currentTimeMillis(),
                source = DataSource.PHONE_SENSORS
            ),
            HealthMetricCacheEntity(
                sessionId = "test_session",
                metricType = HealthMetricType.SPEED,
                value = 3.5,
                unit = "m/s",
                timestamp = System.currentTimeMillis() + 1000,
                source = DataSource.PHONE_GPS
            )
        )
        healthMetricDao.insertAll(metrics)
        
        // Verify metrics exist
        val retrievedMetrics = healthMetricDao.getBySession("test_session")
        assertEquals("Should have 2 health metrics", 2, retrievedMetrics.size)
        
        // Delete the session - health metrics should be cascade deleted
        runSessionDao.deleteById("test_session")
        
        val remainingMetrics = healthMetricDao.getBySession("test_session")
        assertEquals("Health metrics should be deleted with session", 0, remainingMetrics.size)
    }
    
    @Test
    fun typeConvertersIntegration() = runTest {
        val runSessionDao = database.runSessionDao()
        
        // Create a session with complex data types
        val coachingMessages = listOf(
            CoachingMessage(
                message = "Great pace!",
                category = TextCategory.ENCOURAGEMENT,
                isLLMGenerated = false
            ),
            CoachingMessage(
                message = "Time to speed up",
                category = TextCategory.PACE_GUIDANCE,
                isLLMGenerated = true,
                priority = CoachingMessage.Priority.HIGH
            )
        )
        
        val session = RunSessionEntity(
            sessionId = "test_session",
            userId = "test_user",
            startTime = System.currentTimeMillis(),
            workoutType = WorkoutType.INTERVAL_RUN, // Enum
            coachId = "test_coach",
            syncStatus = SyncStatus.PENDING, // Enum
            coachingMessages = coachingMessages // List<CoachingMessage>
        )
        
        runSessionDao.insert(session)
        
        val retrieved = runSessionDao.getById("test_session")
        assertNotNull("Session should be retrieved", retrieved)
        assertEquals("Workout type should be preserved", WorkoutType.INTERVAL_RUN, retrieved!!.workoutType)
        assertEquals("Sync status should be preserved", SyncStatus.PENDING, retrieved.syncStatus)
        assertEquals("Should have 2 coaching messages", 2, retrieved.coachingMessages.size)
        assertEquals("First message should match", "Great pace!", retrieved.coachingMessages[0].message)
        assertEquals("Second message category should match", TextCategory.PACE_GUIDANCE, retrieved.coachingMessages[1].category)
    }
    
    @Test
    fun complexQueryIntegration() = runTest {
        val runSessionDao = database.runSessionDao()
        val gpsTrackDao = database.gpsTrackDao()
        val healthMetricDao = database.healthMetricDao()
        
        // Create a complete run with all related data
        val sessionId = "complete_run_test"
        val startTime = System.currentTimeMillis()
        
        // Insert run session
        val session = RunSessionEntity(
            sessionId = sessionId,
            userId = "test_user",
            startTime = startTime,
            endTime = startTime + 1800000, // 30 minutes
            distance = 5000f, // 5km
            duration = 1800000L,
            workoutType = WorkoutType.TEMPO_RUN,
            averagePace = 6.0f,
            coachId = "test_coach",
            syncStatus = SyncStatus.SYNCED
        )
        runSessionDao.insert(session)
        
        // Insert GPS track
        val gpsPoints = (0..100).map { i ->
            GpsTrackPointEntity(
                sessionId = sessionId,
                latitude = 37.7749 + (i * 0.0001),
                longitude = -122.4194 + (i * 0.0001),
                timestamp = startTime + (i * 18000), // Every 18 seconds
                sequenceNumber = i,
                distance = i * 50f, // 50m intervals
                speed = 3.0f + (i % 10) * 0.1f, // Varying speed
                pace = 5.5f + (i % 10) * 0.1f // Varying pace
            )
        }
        gpsTrackDao.insertAll(gpsPoints)
        
        // Insert health metrics
        val heartRateData = (0..60).map { i ->
            HealthMetricCacheEntity(
                sessionId = sessionId,
                metricType = HealthMetricType.HEART_RATE,
                value = 130.0 + (i % 20), // Varying heart rate
                unit = "bpm",
                timestamp = startTime + (i * 30000), // Every 30 seconds
                source = DataSource.PHONE_SENSORS
            )
        }
        healthMetricDao.insertAll(heartRateData)
        
        // Test complex queries
        val completedRuns = runSessionDao.getCompletedRunCount("test_user")
        assertEquals("Should have 1 completed run", 1, completedRuns)
        
        val totalDistance = runSessionDao.getTotalDistance("test_user", 0L)
        assertEquals("Total distance should be 5000m", 5000f, totalDistance!!, 0.01f)
        
        val gpsPointCount = gpsTrackDao.getPointCount(sessionId)
        assertEquals("Should have 101 GPS points", 101, gpsPointCount)
        
        val maxSpeed = gpsTrackDao.getMaxSpeed(sessionId)
        assertTrue("Max speed should be reasonable", maxSpeed!! > 3.0f && maxSpeed < 4.0f)
        
        val averageHeartRate = healthMetricDao.getAverageHeartRate(sessionId)
        assertTrue("Average heart rate should be reasonable", averageHeartRate!! > 130.0 && averageHeartRate < 150.0)
        
        val heartRateRange = healthMetricDao.getValueRange(sessionId, HealthMetricType.HEART_RATE)
        assertNotNull("Heart rate range should not be null", heartRateRange)
        assertTrue("Heart rate range should be valid", heartRateRange!!.maxValue > heartRateRange.minValue)
    }
    
    @Test
    fun databaseUtilsIntegration() = runTest {
        // Test database initialization
        DatabaseUtils.initializeWithDefaultData(database)
        
        val coaches = database.coachDao().getActiveCoaches()
        assertTrue("Should have default coaches", coaches.isNotEmpty())
        
        val textLines = database.coachTextLineDao().getActiveByCoach(coaches[0].id)
        assertTrue("Should have default text lines", textLines.isNotEmpty())
        
        // Test database export
        val export = DatabaseUtils.exportDatabaseForDebugging(database)
        assertTrue("Should have coach count", export.coachCount > 0)
    }
    
    @Test
    fun performanceWithLargeDataset() = runTest {
        val runSessionDao = database.runSessionDao()
        val gpsTrackDao = database.gpsTrackDao()
        
        // Create multiple sessions
        val sessions = (1..10).map { i ->
            RunSessionEntity(
                sessionId = "session_$i",
                userId = "test_user",
                startTime = System.currentTimeMillis() - (i * 86400000L), // i days ago
                workoutType = WorkoutType.values()[i % WorkoutType.values().size],
                coachId = "test_coach"
            )
        }
        runSessionDao.insertAll(sessions)
        
        // Create GPS points for each session
        sessions.forEach { session ->
            val gpsPoints = (0..50).map { j ->
                GpsTrackPointEntity(
                    sessionId = session.sessionId,
                    latitude = 37.7749 + (j * 0.0001),
                    longitude = -122.4194 + (j * 0.0001),
                    timestamp = session.startTime + (j * 30000),
                    sequenceNumber = j
                )
            }
            gpsTrackDao.insertAll(gpsPoints)
        }
        
        // Test pagination
        val firstPage = runSessionDao.getByUserPaged("test_user", 5, 0)
        assertEquals("First page should have 5 sessions", 5, firstPage.size)
        
        val secondPage = runSessionDao.getByUserPaged("test_user", 5, 5)
        assertEquals("Second page should have 5 sessions", 5, secondPage.size)
        
        // Test sampling
        val sampledPoints = gpsTrackDao.getSampledPoints("session_1", 5)
        assertTrue("Should have sampled points", sampledPoints.isNotEmpty())
        assertTrue("Should have fewer sampled points than total", sampledPoints.size < 51)
    }
}