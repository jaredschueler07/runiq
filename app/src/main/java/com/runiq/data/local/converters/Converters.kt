package com.runiq.data.local.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.runiq.data.local.entities.CoachingStyle
import com.runiq.data.local.entities.ExperienceLevel
import com.runiq.data.local.entities.MotivationStyle
import com.runiq.data.local.entities.EmotionalTone
import com.runiq.domain.model.CoachingMessage
import com.runiq.domain.model.VoiceCharacteristics
import com.runiq.domain.model.WorkoutType
import com.runiq.domain.model.SyncStatus
import com.runiq.domain.model.TextCategory

/**
 * Type converters for Room database to handle complex data types
 * This class is marked with @ProvidedTypeConverter, meaning we will provide an instance
 * of it to the Room database builder. This helps resolve KSP issues with complex types
 * by breaking the direct dependency chain during code generation.
 */
@ProvidedTypeConverter
class Converters(private val moshi: Moshi) {
    
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
    
    // TextCategory converters
    @TypeConverter
    fun fromTextCategory(textCategory: TextCategory): String {
        return textCategory.name
    }
    
    @TypeConverter
    fun toTextCategory(textCategory: String): TextCategory {
        return TextCategory.valueOf(textCategory)
    }
    
    // EmotionalTone converters
    @TypeConverter
    fun fromEmotionalTone(emotionalTone: EmotionalTone): String {
        return emotionalTone.name
    }
    
    @TypeConverter
    fun toEmotionalTone(emotionalTone: String): EmotionalTone {
        return EmotionalTone.valueOf(emotionalTone)
    }
    
    // List<String> converters for conditions, tags, and template variables
    @TypeConverter
    fun fromStringList(list: List<String>?): String? {
        return list?.let {
            val adapter: JsonAdapter<List<String>> = moshi.adapter(Types.newParameterizedType(List::class.java, String::class.java))
            adapter.toJson(it)
        }
    }

    @TypeConverter
    fun toStringList(json: String?): List<String>? {
        return json?.let {
            val adapter: JsonAdapter<List<String>> = moshi.adapter(Types.newParameterizedType(List::class.java, String::class.java))
            adapter.fromJson(it)
        }
    }

    // VoiceCharacteristics converters
    @TypeConverter
    fun fromVoiceCharacteristics(voice: VoiceCharacteristics?): String? {
        return voice?.let {
            val adapter: JsonAdapter<VoiceCharacteristics> = moshi.adapter(VoiceCharacteristics::class.java)
            adapter.toJson(it)
        }
    }

    @TypeConverter
    fun toVoiceCharacteristics(value: String?): VoiceCharacteristics? {
        return value?.let {
            val adapter: JsonAdapter<VoiceCharacteristics> = moshi.adapter(VoiceCharacteristics::class.java)
            adapter.fromJson(it)
        }
    }

    // CoachingMessage list converters
    @TypeConverter
    fun fromCoachingMessageList(messages: List<CoachingMessage>?): String? {
        return messages?.let {
            val adapter: JsonAdapter<List<CoachingMessage>> = moshi.adapter(Types.newParameterizedType(List::class.java, CoachingMessage::class.java))
            adapter.toJson(it)
        }
    }

    @TypeConverter
    fun toCoachingMessageList(value: String?): List<CoachingMessage>? {
        return value?.let {
            val adapter: JsonAdapter<List<CoachingMessage>> = moshi.adapter(Types.newParameterizedType(List::class.java, CoachingMessage::class.java))
            adapter.fromJson(it)
        }
    }

}