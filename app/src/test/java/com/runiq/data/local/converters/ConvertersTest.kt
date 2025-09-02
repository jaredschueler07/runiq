package com.runiq.data.local.converters

import com.runiq.data.local.entities.*
import com.runiq.domain.model.*
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

class ConvertersTest {
    
    private lateinit var converters: Converters
    
    @Before
    fun setup() {
        converters = Converters()
    }
    
    @Test
    fun workoutTypeConversion() {
        val workoutType = WorkoutType.INTERVAL_RUN
        
        val converted = converters.fromWorkoutType(workoutType)
        assertEquals("Should convert to string", "INTERVAL_RUN", converted)
        
        val backConverted = converters.toWorkoutType(converted)
        assertEquals("Should convert back to enum", WorkoutType.INTERVAL_RUN, backConverted)
    }
    
    @Test
    fun syncStatusConversion() {
        val syncStatus = SyncStatus.SYNCING
        
        val converted = converters.fromSyncStatus(syncStatus)
        assertEquals("Should convert to string", "SYNCING", converted)
        
        val backConverted = converters.toSyncStatus(converted)
        assertEquals("Should convert back to enum", SyncStatus.SYNCING, backConverted)
    }
    
    @Test
    fun coachingStyleConversion() {
        val style = CoachingStyle.ANALYTICAL
        
        val converted = converters.fromCoachingStyle(style)
        assertEquals("Should convert to string", "ANALYTICAL", converted)
        
        val backConverted = converters.toCoachingStyle(converted)
        assertEquals("Should convert back to enum", CoachingStyle.ANALYTICAL, backConverted)
    }
    
    @Test
    fun textCategoryConversion() {
        val category = TextCategory.PACE_GUIDANCE
        
        val converted = converters.fromTextCategory(category)
        assertEquals("Should convert to string", "PACE_GUIDANCE", converted)
        
        val backConverted = converters.toTextCategory(converted)
        assertEquals("Should convert back to enum", TextCategory.PACE_GUIDANCE, backConverted)
    }
    
    @Test
    fun healthMetricTypeConversion() {
        val metricType = HealthMetricType.HEART_RATE
        
        val converted = converters.fromHealthMetricType(metricType)
        assertEquals("Should convert to string", "HEART_RATE", converted)
        
        val backConverted = converters.toHealthMetricType(converted)
        assertEquals("Should convert back to enum", HealthMetricType.HEART_RATE, backConverted)
    }
    
    @Test
    fun stringListConversion() {
        val stringList = listOf("item1", "item2", "item3")
        
        val converted = converters.fromStringList(stringList)
        assertTrue("Should be valid JSON", converted.startsWith("["))
        assertTrue("Should contain items", converted.contains("item1"))
        
        val backConverted = converters.toStringList(converted)
        assertEquals("Should convert back to list", stringList, backConverted)
    }
    
    @Test
    fun emptyStringListConversion() {
        val emptyList = emptyList<String>()
        
        val converted = converters.fromStringList(emptyList)
        assertEquals("Should convert to empty JSON array", "[]", converted)
        
        val backConverted = converters.toStringList(converted)
        assertEquals("Should convert back to empty list", emptyList, backConverted)
    }
    
    @Test
    fun coachingMessagesConversion() {
        val messages = listOf(
            CoachingMessage(
                message = "Great pace!",
                category = TextCategory.ENCOURAGEMENT,
                isLLMGenerated = false,
                priority = CoachingMessage.Priority.NORMAL
            ),
            CoachingMessage(
                message = "Slow down a bit",
                category = TextCategory.PACE_GUIDANCE,
                isLLMGenerated = true,
                priority = CoachingMessage.Priority.HIGH,
                triggerConditions = listOf("pace:too_fast")
            )
        )
        
        val converted = converters.fromCoachingMessages(messages)
        assertTrue("Should be valid JSON", converted.startsWith("["))
        assertTrue("Should contain message content", converted.contains("Great pace!"))
        assertTrue("Should contain category", converted.contains("ENCOURAGEMENT"))
        
        val backConverted = converters.toCoachingMessages(converted)
        assertEquals("Should have same number of messages", 2, backConverted.size)
        assertEquals("First message should match", "Great pace!", backConverted[0].message)
        assertEquals("Second message should match", "Slow down a bit", backConverted[1].message)
        assertEquals("Categories should match", TextCategory.ENCOURAGEMENT, backConverted[0].category)
        assertEquals("Priority should match", CoachingMessage.Priority.HIGH, backConverted[1].priority)
    }
    
    @Test
    fun emptyCoachingMessagesConversion() {
        val emptyMessages = emptyList<CoachingMessage>()
        
        val converted = converters.fromCoachingMessages(emptyMessages)
        assertEquals("Should convert to empty JSON array", "[]", converted)
        
        val backConverted = converters.toCoachingMessages(converted)
        assertEquals("Should convert back to empty list", emptyMessages, backConverted)
    }
    
    @Test
    fun voiceCharacteristicsConversion() {
        val voiceCharacteristics = VoiceCharacteristics(
            voiceId = "voice_123",
            voiceName = "Test Voice",
            stability = 0.8f,
            similarityBoost = 0.7f,
            style = 0.1f,
            useSpeakerBoost = true
        )
        
        val converted = converters.fromVoiceCharacteristics(voiceCharacteristics)
        assertTrue("Should be valid JSON", converted.startsWith("{"))
        assertTrue("Should contain voice ID", converted.contains("voice_123"))
        assertTrue("Should contain voice name", converted.contains("Test Voice"))
        
        val backConverted = converters.toVoiceCharacteristics(converted)
        assertEquals("Voice ID should match", "voice_123", backConverted.voiceId)
        assertEquals("Voice name should match", "Test Voice", backConverted.voiceName)
        assertEquals("Stability should match", 0.8f, backConverted.stability, 0.01f)
        assertEquals("Similarity boost should match", 0.7f, backConverted.similarityBoost, 0.01f)
        assertEquals("Style should match", 0.1f, backConverted.style, 0.01f)
        assertEquals("Speaker boost should match", true, backConverted.useSpeakerBoost)
    }
    
    @Test
    fun invalidJsonHandling() {
        // Test invalid JSON for string list
        val invalidStringListJson = "{invalid json}"
        val stringListResult = converters.toStringList(invalidStringListJson)
        assertEquals("Should return empty list for invalid JSON", emptyList<String>(), stringListResult)
        
        // Test invalid JSON for coaching messages
        val invalidMessagesJson = "[invalid json"
        val messagesResult = converters.toCoachingMessages(invalidMessagesJson)
        assertEquals("Should return empty list for invalid JSON", emptyList<CoachingMessage>(), messagesResult)
        
        // Test invalid JSON for voice characteristics
        val invalidVoiceJson = "not json at all"
        val voiceResult = converters.toVoiceCharacteristics(invalidVoiceJson)
        assertEquals("Should return default voice for invalid JSON", "default", voiceResult.voiceId)
        assertEquals("Should return default name for invalid JSON", "Default", voiceResult.voiceName)
    }
    
    @Test
    fun nullAndEmptyStringHandling() {
        // Test empty string for string list
        val emptyStringResult = converters.toStringList("")
        assertEquals("Should handle empty string", emptyList<String>(), emptyStringResult)
        
        // Test empty string for coaching messages
        val emptyMessagesResult = converters.toCoachingMessages("")
        assertEquals("Should handle empty string", emptyList<CoachingMessage>(), emptyMessagesResult)
        
        // Test empty string for voice characteristics
        val emptyVoiceResult = converters.toVoiceCharacteristics("")
        assertEquals("Should return default for empty string", "default", emptyVoiceResult.voiceId)
    }
    
    @Test
    fun complexCoachingMessageConversion() {
        val complexMessage = CoachingMessage(
            message = "Your pace is {current_pace} min/km, target is {target_pace} min/km",
            timestamp = 1234567890L,
            category = TextCategory.PACE_GUIDANCE,
            isLLMGenerated = true,
            priority = CoachingMessage.Priority.HIGH,
            triggerConditions = listOf("pace:off_target", "phase:main", "distance:>1000")
        )
        
        val converted = converters.fromCoachingMessages(listOf(complexMessage))
        val backConverted = converters.toCoachingMessages(converted)
        
        assertEquals("Should have one message", 1, backConverted.size)
        val result = backConverted[0]
        
        assertEquals("Message should match", complexMessage.message, result.message)
        assertEquals("Timestamp should match", complexMessage.timestamp, result.timestamp)
        assertEquals("Category should match", complexMessage.category, result.category)
        assertEquals("LLM flag should match", complexMessage.isLLMGenerated, result.isLLMGenerated)
        assertEquals("Priority should match", complexMessage.priority, result.priority)
        assertEquals("Trigger conditions should match", complexMessage.triggerConditions, result.triggerConditions)
    }
    
    @Test
    fun allEnumConversions() {
        // Test all WorkoutType values
        WorkoutType.values().forEach { workoutType ->
            val converted = converters.fromWorkoutType(workoutType)
            val backConverted = converters.toWorkoutType(converted)
            assertEquals("WorkoutType $workoutType should round-trip", workoutType, backConverted)
        }
        
        // Test all SyncStatus values
        SyncStatus.values().forEach { syncStatus ->
            val converted = converters.fromSyncStatus(syncStatus)
            val backConverted = converters.toSyncStatus(converted)
            assertEquals("SyncStatus $syncStatus should round-trip", syncStatus, backConverted)
        }
        
        // Test all CoachingStyle values
        CoachingStyle.values().forEach { style ->
            val converted = converters.fromCoachingStyle(style)
            val backConverted = converters.toCoachingStyle(converted)
            assertEquals("CoachingStyle $style should round-trip", style, backConverted)
        }
        
        // Test all TextCategory values
        TextCategory.values().forEach { category ->
            val converted = converters.fromTextCategory(category)
            val backConverted = converters.toTextCategory(converted)
            assertEquals("TextCategory $category should round-trip", category, backConverted)
        }
        
        // Test all HealthMetricType values
        HealthMetricType.values().forEach { type ->
            val converted = converters.fromHealthMetricType(type)
            val backConverted = converters.toHealthMetricType(converted)
            assertEquals("HealthMetricType $type should round-trip", type, backConverted)
        }
    }
}