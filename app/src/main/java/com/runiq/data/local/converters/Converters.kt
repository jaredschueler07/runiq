package com.runiq.data.local.converters

import androidx.room.TypeConverter
import com.runiq.data.local.entities.*
import com.runiq.domain.model.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import timber.log.Timber

/**
 * Type converters for Room database to handle complex data types
 */
class Converters {
    
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    
    // WorkoutType converters
    @TypeConverter
    fun fromWorkoutType(workoutType: WorkoutType): String = workoutType.name
    
    @TypeConverter
    fun toWorkoutType(workoutType: String): WorkoutType = 
        WorkoutType.valueOf(workoutType)
    
    // SyncStatus converters
    @TypeConverter
    fun fromSyncStatus(syncStatus: SyncStatus): String = syncStatus.name
    
    @TypeConverter
    fun toSyncStatus(syncStatus: String): SyncStatus = 
        SyncStatus.valueOf(syncStatus)
    
    // CoachingStyle converters
    @TypeConverter
    fun fromCoachingStyle(style: CoachingStyle): String = style.name
    
    @TypeConverter
    fun toCoachingStyle(style: String): CoachingStyle = 
        CoachingStyle.valueOf(style)
    
    // ExperienceLevel converters
    @TypeConverter
    fun fromExperienceLevel(level: ExperienceLevel): String = level.name
    
    @TypeConverter
    fun toExperienceLevel(level: String): ExperienceLevel = 
        ExperienceLevel.valueOf(level)
    
    // MotivationStyle converters
    @TypeConverter
    fun fromMotivationStyle(style: MotivationStyle): String = style.name
    
    @TypeConverter
    fun toMotivationStyle(style: String): MotivationStyle = 
        MotivationStyle.valueOf(style)
    
    // TextCategory converters
    @TypeConverter
    fun fromTextCategory(category: TextCategory): String = category.name
    
    @TypeConverter
    fun toTextCategory(category: String): TextCategory = 
        TextCategory.valueOf(category)
    
    // EmotionalTone converters
    @TypeConverter
    fun fromEmotionalTone(tone: EmotionalTone): String = tone.name
    
    @TypeConverter
    fun toEmotionalTone(tone: String): EmotionalTone = 
        EmotionalTone.valueOf(tone)
    
    // HealthMetricType converters
    @TypeConverter
    fun fromHealthMetricType(type: HealthMetricType): String = type.name
    
    @TypeConverter
    fun toHealthMetricType(type: String): HealthMetricType = 
        HealthMetricType.valueOf(type)
    
    // DataSource converters
    @TypeConverter
    fun fromDataSource(source: DataSource): String = source.name
    
    @TypeConverter
    fun toDataSource(source: String): DataSource = 
        DataSource.valueOf(source)
    
    // List<String> converters
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return try {
            val adapter = moshi.adapter<List<String>>(
                Types.newParameterizedType(List::class.java, String::class.java)
            )
            adapter.toJson(value)
        } catch (e: Exception) {
            Timber.e(e, "Failed to convert string list to JSON")
            "[]"
        }
    }
    
    @TypeConverter
    fun toStringList(value: String): List<String> {
        return try {
            val adapter = moshi.adapter<List<String>>(
                Types.newParameterizedType(List::class.java, String::class.java)
            )
            adapter.fromJson(value) ?: emptyList()
        } catch (e: Exception) {
            Timber.e(e, "Failed to parse JSON to string list: $value")
            emptyList()
        }
    }
    
    // List<CoachingMessage> converters
    @TypeConverter
    fun fromCoachingMessages(value: List<CoachingMessage>): String {
        return try {
            val adapter = moshi.adapter<List<CoachingMessage>>(
                Types.newParameterizedType(List::class.java, CoachingMessage::class.java)
            )
            adapter.toJson(value)
        } catch (e: Exception) {
            Timber.e(e, "Failed to convert coaching messages to JSON")
            "[]"
        }
    }
    
    @TypeConverter
    fun toCoachingMessages(value: String): List<CoachingMessage> {
        return try {
            val adapter = moshi.adapter<List<CoachingMessage>>(
                Types.newParameterizedType(List::class.java, CoachingMessage::class.java)
            )
            adapter.fromJson(value) ?: emptyList()
        } catch (e: Exception) {
            Timber.e(e, "Failed to parse JSON to coaching messages: $value")
            emptyList()
        }
    }
    
    // VoiceCharacteristics converters
    @TypeConverter
    fun fromVoiceCharacteristics(value: VoiceCharacteristics): String {
        return try {
            val adapter = moshi.adapter(VoiceCharacteristics::class.java)
            adapter.toJson(value)
        } catch (e: Exception) {
            Timber.e(e, "Failed to convert voice characteristics to JSON")
            "{}"
        }
    }
    
    @TypeConverter
    fun toVoiceCharacteristics(value: String): VoiceCharacteristics {
        return try {
            val adapter = moshi.adapter(VoiceCharacteristics::class.java)
            adapter.fromJson(value) ?: VoiceCharacteristics(
                voiceId = "default",
                voiceName = "Default"
            )
        } catch (e: Exception) {
            Timber.e(e, "Failed to parse JSON to voice characteristics: $value")
            VoiceCharacteristics(
                voiceId = "default",
                voiceName = "Default"
            )
        }
    }
}