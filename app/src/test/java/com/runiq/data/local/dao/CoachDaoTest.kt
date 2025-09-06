package com.runiq.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.runiq.data.local.database.RunIQDatabase
import com.runiq.data.local.entities.CoachEntity
import com.runiq.data.local.entities.CoachingStyle
import com.runiq.data.local.entities.ExperienceLevel
import com.runiq.data.local.entities.MotivationStyle
import com.runiq.domain.model.VoiceCharacteristics
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class CoachDaoTest {
    
    private lateinit var database: RunIQDatabase
    private lateinit var coachDao: CoachDao
    
    @Before
    fun createDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RunIQDatabase::class.java
        ).allowMainThreadQueries().build()
        
        coachDao = database.coachDao()
    }
    
    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }
    
    @Test
    fun insertAndGetCoach() = runTest {
        val coach = createTestCoach("coach1", "Alex", CoachingStyle.ENCOURAGING)
        
        val insertedId = coachDao.insert(coach)
        assertTrue("Insert should return positive ID", insertedId > 0)
        
        val retrieved = coachDao.getById(coach.id)
        assertNotNull("Retrieved coach should not be null", retrieved)
        assertEquals("Coach IDs should match", coach.id, retrieved!!.id)
        assertEquals("Coach names should match", coach.name, retrieved.name)
        assertEquals("Coaching styles should match", coach.coachingStyle, retrieved.coachingStyle)
    }
    
    @Test
    fun observeCoachUpdates() = runTest {
        val coach = createTestCoach("coach1", "Alex", CoachingStyle.ENCOURAGING)
        coachDao.insert(coach)
        
        val flow = coachDao.observeById(coach.id)
        val initialValue = flow.first()
        
        assertNotNull("Initial value should not be null", initialValue)
        assertEquals("Initial usage count should be 0", 0, initialValue!!.usageCount)
        
        // Update usage count
        coachDao.incrementUsageCount(coach.id)
        
        val updatedValue = flow.first()
        assertEquals("Usage count should be incremented", 1, updatedValue!!.usageCount)
    }
    
    @Test
    fun getActiveCoaches() = runTest {
        val coaches = listOf(
            createTestCoach("coach1", "Alex", CoachingStyle.ENCOURAGING, isActive = true),
            createTestCoach("coach2", "Sarah", CoachingStyle.ANALYTICAL, isActive = false),
            createTestCoach("coach3", "Mike", CoachingStyle.CHALLENGING, isActive = true)
        )
        
        coachDao.insertAll(coaches)
        
        val activeCoaches = coachDao.getActiveCoaches()
        assertEquals("Should have 2 active coaches", 2, activeCoaches.size)
        assertTrue("Should contain Alex", activeCoaches.any { it.name == "Alex" })
        assertTrue("Should contain Mike", activeCoaches.any { it.name == "Mike" })
        assertFalse("Should not contain Sarah", activeCoaches.any { it.name == "Sarah" })
    }
    
    @Test
    fun getByPremiumStatus() = runTest {
        val coaches = listOf(
            createTestCoach("coach1", "Alex", CoachingStyle.ENCOURAGING, isPremium = false),
            createTestCoach("coach2", "Sarah", CoachingStyle.ANALYTICAL, isPremium = true),
            createTestCoach("coach3", "Mike", CoachingStyle.CHALLENGING, isPremium = false)
        )
        
        coachDao.insertAll(coaches)
        
        val freeCoaches = coachDao.getFreeCoaches()
        assertEquals("Should have 2 free coaches", 2, freeCoaches.size)
        
        val premiumCoaches = coachDao.getPremiumCoaches()
        assertEquals("Should have 1 premium coach", 1, premiumCoaches.size)
        assertEquals("Premium coach should be Sarah", "Sarah", premiumCoaches[0].name)
    }
    
    @Test
    fun getByCoachingStyle() = runTest {
        val coaches = listOf(
            createTestCoach("coach1", "Alex", CoachingStyle.ENCOURAGING),
            createTestCoach("coach2", "Sarah", CoachingStyle.ANALYTICAL),
            createTestCoach("coach3", "Mike", CoachingStyle.CHALLENGING),
            createTestCoach("coach4", "Emma", CoachingStyle.ENCOURAGING)
        )
        
        coachDao.insertAll(coaches)
        
        val encouragingCoaches = coachDao.getByCoachingStyle(CoachingStyle.ENCOURAGING)
        assertEquals("Should have 2 encouraging coaches", 2, encouragingCoaches.size)
        
        val analyticalCoaches = coachDao.getByCoachingStyle(CoachingStyle.ANALYTICAL)
        assertEquals("Should have 1 analytical coach", 1, analyticalCoaches.size)
        assertEquals("Analytical coach should be Sarah", "Sarah", analyticalCoaches[0].name)
        
        val styles = listOf(CoachingStyle.ENCOURAGING, CoachingStyle.CHALLENGING)
        val multiStyleCoaches = coachDao.getByCoachingStyles(styles)
        assertEquals("Should have 3 coaches with specified styles", 3, multiStyleCoaches.size)
    }
    
    @Test
    fun getByExperienceAndMotivation() = runTest {
        val coaches = listOf(
            createTestCoach("coach1", "Beginner Coach", CoachingStyle.ENCOURAGING, 
                experienceLevel = ExperienceLevel.BEGINNER, motivationStyle = MotivationStyle.GENTLE),
            createTestCoach("coach2", "Advanced Coach", CoachingStyle.ANALYTICAL, 
                experienceLevel = ExperienceLevel.ADVANCED, motivationStyle = MotivationStyle.TACTICAL),
            createTestCoach("coach3", "Elite Coach", CoachingStyle.CHALLENGING, 
                experienceLevel = ExperienceLevel.ELITE, motivationStyle = MotivationStyle.INTENSE)
        )
        
        coachDao.insertAll(coaches)
        
        val beginnerCoaches = coachDao.getByExperienceLevel(ExperienceLevel.BEGINNER)
        assertEquals("Should have 1 beginner coach", 1, beginnerCoaches.size)
        
        val gentleCoaches = coachDao.getByMotivationStyle(MotivationStyle.GENTLE)
        assertEquals("Should have 1 gentle coach", 1, gentleCoaches.size)
    }
    
    @Test
    fun searchCoaches() = runTest {
        val coaches = listOf(
            createTestCoach("coach1", "Alex Runner", CoachingStyle.ENCOURAGING, 
                description = "Great for beginners and motivation"),
            createTestCoach("coach2", "Sarah Analytics", CoachingStyle.ANALYTICAL, 
                description = "Data-driven performance coach"),
            createTestCoach("coach3", "Mike Speed", CoachingStyle.CHALLENGING, 
                description = "Speed and interval specialist")
        )
        
        coachDao.insertAll(coaches)
        
        val beginnerSearch = coachDao.searchCoaches("beginner")
        assertEquals("Should find 1 coach for 'beginner'", 1, beginnerSearch.size)
        assertEquals("Should find Alex", "Alex Runner", beginnerSearch[0].name)
        
        val dataSearch = coachDao.searchCoaches("data")
        assertEquals("Should find 1 coach for 'data'", 1, dataSearch.size)
        assertEquals("Should find Sarah", "Sarah Analytics", dataSearch[0].name)
        
        val speedSearch = coachDao.searchCoaches("speed")
        assertEquals("Should find 1 coach for 'speed'", 1, speedSearch.size)
        assertEquals("Should find Mike", "Mike Speed", speedSearch[0].name)
    }
    
    @Test
    fun getBySpecialization() = runTest {
        val coaches = listOf(
            createTestCoach("coach1", "Marathon Coach", CoachingStyle.ENCOURAGING, 
                specializations = listOf("marathon", "endurance", "beginner")),
            createTestCoach("coach2", "Speed Coach", CoachingStyle.CHALLENGING, 
                specializations = listOf("speed", "intervals", "track")),
            createTestCoach("coach3", "All-rounder", CoachingStyle.ANALYTICAL, 
                specializations = listOf("beginner", "intermediate", "data"))
        )
        
        coachDao.insertAll(coaches)
        
        val marathonCoaches = coachDao.getBySpecialization("marathon")
        assertEquals("Should find 1 marathon coach", 1, marathonCoaches.size)
        assertEquals("Should find Marathon Coach", "Marathon Coach", marathonCoaches[0].name)
        
        val beginnerCoaches = coachDao.getBySpecialization("beginner")
        assertEquals("Should find 2 beginner coaches", 2, beginnerCoaches.size)
    }
    
    @Test
    fun popularityAndRatings() = runTest {
        val coaches = listOf(
            createTestCoach("coach1", "Popular Coach", CoachingStyle.ENCOURAGING, 
                usageCount = 100, averageRating = 4.5f, totalRatings = 50),
            createTestCoach("coach2", "New Coach", CoachingStyle.ANALYTICAL, 
                usageCount = 5, averageRating = null, totalRatings = 0),
            createTestCoach("coach3", "Highly Rated", CoachingStyle.CHALLENGING, 
                usageCount = 50, averageRating = 4.8f, totalRatings = 25)
        )
        
        coachDao.insertAll(coaches)
        
        val topRated = coachDao.getTopRatedCoaches(2)
        assertEquals("Should have 2 top rated coaches", 2, topRated.size)
        assertEquals("First should be Highly Rated", "Highly Rated", topRated[0].name)
        assertEquals("Second should be Popular Coach", "Popular Coach", topRated[1].name)
        
        val mostPopular = coachDao.getMostPopularCoaches(2)
        assertEquals("Should have 2 most popular coaches", 2, mostPopular.size)
        assertEquals("First should be Popular Coach", "Popular Coach", mostPopular[0].name)
        assertEquals("Second should be Highly Rated", "Highly Rated", mostPopular[1].name)
        
        val highRated = coachDao.getHighRatedCoaches(4.0f)
        assertEquals("Should have 2 highly rated coaches", 2, highRated.size)
    }
    
    @Test
    fun usageStatistics() = runTest {
        val coach = createTestCoach("coach1", "Test Coach", CoachingStyle.ENCOURAGING, 
            usageCount = 10)
        coachDao.insert(coach)
        
        coachDao.incrementUsageCount("coach1")
        
        val updated = coachDao.getById("coach1")
        assertEquals("Usage count should be incremented", 11, updated!!.usageCount)
    }
    
    @Test
    fun ratingOperations() = runTest {
        val coach = createTestCoach("coach1", "Test Coach", CoachingStyle.ENCOURAGING, 
            averageRating = null, totalRatings = 0)
        coachDao.insert(coach)
        
        // Add first rating
        coachDao.addRating("coach1", 4.0f)
        
        val afterFirst = coachDao.getById("coach1")
        assertEquals("Average rating should be 4.0", 4.0f, afterFirst!!.averageRating!!, 0.01f)
        assertEquals("Total ratings should be 1", 1, afterFirst.totalRatings)
        
        // Add second rating
        coachDao.addRating("coach1", 5.0f)
        
        val afterSecond = coachDao.getById("coach1")
        assertEquals("Average rating should be 4.5", 4.5f, afterSecond!!.averageRating!!, 0.01f)
        assertEquals("Total ratings should be 2", 2, afterSecond.totalRatings)
    }
    
    @Test
    fun versionManagement() = runTest {
        val coaches = listOf(
            createTestCoach("coach1", "Old Coach", CoachingStyle.ENCOURAGING, version = 1),
            createTestCoach("coach2", "Current Coach", CoachingStyle.ANALYTICAL, version = 2),
            createTestCoach("coach3", "Another Old", CoachingStyle.CHALLENGING, version = 1)
        )
        
        coachDao.insertAll(coaches)
        
        val outdatedCoaches = coachDao.getOutdatedCoaches(2)
        assertEquals("Should have 2 outdated coaches", 2, outdatedCoaches.size)
        
        coachDao.updateVersion("coach1", 2)
        
        val stillOutdated = coachDao.getOutdatedCoaches(2)
        assertEquals("Should have 1 outdated coach after update", 1, stillOutdated.size)
    }
    
    @Test
    fun recommendationSystem() = runTest {
        val coaches = listOf(
            createTestCoach("coach1", "Perfect Match", CoachingStyle.ENCOURAGING, 
                experienceLevel = ExperienceLevel.BEGINNER, motivationStyle = MotivationStyle.GENTLE),
            createTestCoach("coach2", "Style Match", CoachingStyle.ENCOURAGING, 
                experienceLevel = ExperienceLevel.ADVANCED, motivationStyle = MotivationStyle.INTENSE),
            createTestCoach("coach3", "Motivation Match", CoachingStyle.CHALLENGING, 
                experienceLevel = ExperienceLevel.BEGINNER, motivationStyle = MotivationStyle.GENTLE),
            createTestCoach("coach4", "No Match", CoachingStyle.ANALYTICAL, 
                experienceLevel = ExperienceLevel.ELITE, motivationStyle = MotivationStyle.TACTICAL)
        )
        
        coachDao.insertAll(coaches)
        
        val recommendations = coachDao.getRecommendedCoaches(
            CoachingStyle.ENCOURAGING,
            MotivationStyle.GENTLE,
            ExperienceLevel.BEGINNER,
            3
        )
        
        assertEquals("Should have 3 recommendations", 3, recommendations.size)
        assertEquals("First should be perfect match", "Perfect Match", recommendations[0].name)
        // Order should be based on matching criteria count
    }
    
    @Test
    fun analyticsQueries() = runTest {
        val coaches = listOf(
            createTestCoach("coach1", "Encouraging 1", CoachingStyle.ENCOURAGING),
            createTestCoach("coach2", "Encouraging 2", CoachingStyle.ENCOURAGING),
            createTestCoach("coach3", "Analytical", CoachingStyle.ANALYTICAL),
            createTestCoach("coach4", "Challenging", CoachingStyle.CHALLENGING)
        )
        
        coachDao.insertAll(coaches)
        
        val styleDistribution = coachDao.getCoachingStyleDistribution()
        assertEquals("Should have 3 different styles", 3, styleDistribution.size)
        
        val encouragingCount = styleDistribution.find { it.coachingStyle == CoachingStyle.ENCOURAGING }
        assertEquals("Should have 2 encouraging coaches", 2, encouragingCount!!.count)
        
        val activeCount = coachDao.getActiveCoachCount()
        assertEquals("Should have 4 active coaches", 4, activeCount)
        
        val premiumCount = coachDao.getPremiumCoachCount()
        assertEquals("Should have 0 premium coaches", 0, premiumCount)
    }
    
    @Test
    fun bulkOperations() = runTest {
        val originalCoaches = listOf(
            createTestCoach("coach1", "Original 1", CoachingStyle.ENCOURAGING, usageCount = 10),
            createTestCoach("coach2", "Original 2", CoachingStyle.ANALYTICAL, usageCount = 5)
        )
        
        coachDao.insertAll(originalCoaches)
        
        val newCoaches = listOf(
            createTestCoach("coach1", "Updated 1", CoachingStyle.ENCOURAGING), // Updated content
            createTestCoach("coach3", "New Coach", CoachingStyle.CHALLENGING)  // New coach
        )
        
        coachDao.replaceAllCoaches(newCoaches)
        
        val coach1 = coachDao.getById("coach1")
        assertEquals("Name should be updated", "Updated 1", coach1!!.name)
        assertEquals("Usage count should be preserved", 10, coach1.usageCount)
        
        val coach3 = coachDao.getById("coach3")
        assertNotNull("New coach should be inserted", coach3)
        assertEquals("New coach usage should be 0", 0, coach3!!.usageCount)
        
        // Deactivate coaches not in the active list
        coachDao.deactivateCoachesNotIn(listOf("coach1", "coach3"))
        
        val coach2 = coachDao.getById("coach2")
        assertFalse("Coach2 should be deactivated", coach2!!.isActive)
    }
    
    @Test
    fun cleanupOperations() = runTest {
        val currentTime = System.currentTimeMillis()
        val oldTime = currentTime - (30 * 24 * 60 * 60 * 1000L) // 30 days ago
        
        val coaches = listOf(
            createTestCoach("coach1", "Active Coach", CoachingStyle.ENCOURAGING, isActive = true),
            createTestCoach("coach2", "Old Inactive", CoachingStyle.ANALYTICAL, 
                isActive = false, updatedAt = oldTime),
            createTestCoach("coach3", "Recent Inactive", CoachingStyle.CHALLENGING, 
                isActive = false, updatedAt = currentTime)
        )
        
        coachDao.insertAll(coaches)
        
        val deletedCount = coachDao.cleanupInactiveCoaches(currentTime - (7 * 24 * 60 * 60 * 1000L))
        assertEquals("Should delete 1 old inactive coach", 1, deletedCount)
        
        val remaining = coachDao.getAll()
        assertEquals("Should have 2 remaining coaches", 2, remaining.size)
        assertFalse("Should not contain old inactive coach", 
            remaining.any { it.name == "Old Inactive" })
    }
    
    private fun createTestCoach(
        id: String,
        name: String,
        coachingStyle: CoachingStyle,
        personalityTraits: List<String> = listOf("friendly", "supportive"),
        specializations: List<String> = listOf("general"),
        experienceLevel: ExperienceLevel = ExperienceLevel.INTERMEDIATE,
        motivationStyle: MotivationStyle = MotivationStyle.BALANCED,
        isActive: Boolean = true,
        isPremium: Boolean = false,
        version: Int = 1,
        usageCount: Int = 0,
        averageRating: Float? = null,
        totalRatings: Int = 0,
        updatedAt: Long = System.currentTimeMillis()
    ): CoachEntity {
        return CoachEntity(
            id = id,
            name = name,
            description = "Test coach description for $name",
            coachingStyle = coachingStyle,
            personalityTraits = personalityTraits,
            voiceCharacteristics = VoiceCharacteristics(
                voiceId = "test_voice_$id",
                voiceName = "$name Voice"
            ),
            specializations = specializations,
            experienceLevel = experienceLevel,
            motivationStyle = motivationStyle,
            isActive = isActive,
            isPremium = isPremium,
            version = version,
            usageCount = usageCount,
            averageRating = averageRating,
            totalRatings = totalRatings,
            updatedAt = updatedAt
        )
    }
}