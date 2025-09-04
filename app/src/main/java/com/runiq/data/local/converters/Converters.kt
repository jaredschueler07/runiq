package com.runiq.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.runiq.data.local.entities.*
import com.runiq.domain.model.*
import java.lang.reflect.Type

/**
 * Type converters for Room database to handle complex data types
 */
class Converters {
    private val gson = Gson()
    
    // WorkoutType converters
    @TypeConverter
    fun fromWorkoutType(workoutType: WorkoutType): String {
        return workoutType.name
    }
    
    @TypeConverter
    fun toWorkoutType(workoutType: String): WorkoutType {
        return WorkoutType.valueOf(workoutType)
    }
    
    // SyncStatus converters
    @TypeConverter
    fun fromSyncStatus(syncStatus: SyncStatus): String {
        return syncStatus.name
    }
    
    @TypeConverter
    fun toSyncStatus(syncStatus: String): SyncStatus {
        return SyncStatus.valueOf(syncStatus)
    }
    
    // CoachingStyle converters
    @TypeConverter
    fun fromCoachingStyle(coachingStyle: CoachingStyle): String {
        return coachingStyle.name
    }
    
    @TypeConverter
    fun toCoachingStyle(coachingStyle: String): CoachingStyle {
        return CoachingStyle.valueOf(coachingStyle)
    }
    
    // ExperienceLevel converters
    @TypeConverter
    fun fromExperienceLevel(experienceLevel: ExperienceLevel): String {
        return experienceLevel.name
    }
    
    @TypeConverter
    fun toExperienceLevel(experienceLevel: String): ExperienceLevel {
        return ExperienceLevel.valueOf(experienceLevel)
    }
    
    // MotivationStyle converters
    @TypeConverter
    fun fromMotivationStyle(motivationStyle: MotivationStyle): String {
        return motivationStyle.name
    }
    
    @TypeConverter
    fun toMotivationStyle(motivationStyle: String): MotivationStyle {
        return MotivationStyle.valueOf(motivationStyle)
    }
    
    // VoiceCharacteristics converter
    @TypeConverter
    fun fromVoiceCharacteristics(voice: VoiceCharacteristics?): String? {
        return voice?.let { gson.toJson(it) }
    }
    
    @TypeConverter
    fun toVoiceCharacteristics(json: String?): VoiceCharacteristics? {
        return json?.let { 
            gson.fromJson(it, VoiceCharacteristics::class.java)
        }
    }
    
    // List<String> converters for specializations and phrases
    @TypeConverter
    fun fromStringList(list: List<String>?): String? {
        return list?.let { gson.toJson(it) }
    }
    
    @TypeConverter
    fun toStringList(json: String?): List<String>? {
        return json?.let {
            val type: Type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(it, type)
        }
    }
    
    // List<CoachingMessage> converters
    @TypeConverter
    fun fromCoachingMessageList(messages: List<CoachingMessage>?): String? {
        return messages?.let { gson.toJson(it) }
    }
    
    @TypeConverter
    fun toCoachingMessageList(json: String?): List<CoachingMessage>? {
        return json?.let {
            val type: Type = object : TypeToken<List<CoachingMessage>>() {}.type
            gson.fromJson(it, type)
        }
    }
    
    // Map<String, String> converter for performance metrics
    @TypeConverter
    fun fromStringMap(map: Map<String, String>?): String? {
        return map?.let { gson.toJson(it) }
    }
    
    @TypeConverter
    fun toStringMap(json: String?): Map<String, String>? {
        return json?.let {
            val type: Type = object : TypeToken<Map<String, String>>() {}.type
            gson.fromJson(it, type)
        }
    }
    
    // IntRange converter (for cadence range)
    @TypeConverter
    fun fromIntRange(range: IntRange?): String? {
        return range?.let { "${it.first}-${it.last}" }
    }
    
    @TypeConverter
    fun toIntRange(rangeString: String?): IntRange? {
        return rangeString?.let {
            val parts = it.split("-")
            if (parts.size == 2) {
                IntRange(parts[0].toInt(), parts[1].toInt())
            } else null
        }
    }
    
    // FloatRange converter (for heart rate zones)
    @TypeConverter
    fun fromFloatRange(range: ClosedFloatingPointRange<Float>?): String? {
        return range?.let { "${it.start}-${it.endInclusive}" }
    }
    
    @TypeConverter
    fun toFloatRange(rangeString: String?): ClosedFloatingPointRange<Float>? {
        return rangeString?.let {
            val parts = it.split("-")
            if (parts.size == 2) {
                parts[0].toFloat()..parts[1].toFloat()
            } else null
        }
    }
    
    // MessagePriority converter
    @TypeConverter
    fun fromMessagePriority(priority: MessagePriority): String {
        return priority.name
    }
    
    @TypeConverter
    fun toMessagePriority(priority: String): MessagePriority {
        return MessagePriority.valueOf(priority)
    }
    
    // MessageTrigger converter
    @TypeConverter
    fun fromMessageTrigger(trigger: MessageTrigger): String {
        return trigger.name
    }
    
    @TypeConverter
    fun toMessageTrigger(trigger: String): MessageTrigger {
        return MessageTrigger.valueOf(trigger)
    }
    
    // List<MessageCondition> converter
    @TypeConverter
    fun fromMessageConditionList(conditions: List<MessageCondition>?): String? {
        return conditions?.let { gson.toJson(it) }
    }
    
    @TypeConverter
    fun toMessageConditionList(json: String?): List<MessageCondition>? {
        return json?.let {
            val type: Type = object : TypeToken<List<MessageCondition>>() {}.type
            gson.fromJson(it, type)
        }
    }
}