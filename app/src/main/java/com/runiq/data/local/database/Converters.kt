package com.runiq.data.local.database

import androidx.room.TypeConverter
import com.runiq.domain.model.CoachingMessage
import com.runiq.domain.model.RunSession
import com.runiq.domain.model.WorkoutType
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

/**
 * Type converters for Room database.
 * Handles conversion of complex types to/from database storage.
 */
class Converters {
    
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    
    private val coachingMessagesAdapter: JsonAdapter<List<CoachingMessage>> = moshi.adapter(
        Types.newParameterizedType(List::class.java, CoachingMessage::class.java)
    )
    
    private val runContextAdapter: JsonAdapter<CoachingMessage.RunContext> = moshi.adapter(
        CoachingMessage.RunContext::class.java
    )
    
    @TypeConverter
    fun fromCoachingMessages(value: List<CoachingMessage>): String {
        return coachingMessagesAdapter.toJson(value)
    }
    
    @TypeConverter
    fun toCoachingMessages(value: String): List<CoachingMessage> {
        return coachingMessagesAdapter.fromJson(value) ?: emptyList()
    }
    
    @TypeConverter
    fun fromWorkoutType(workoutType: WorkoutType): String {
        return workoutType.name
    }
    
    @TypeConverter
    fun toWorkoutType(workoutType: String): WorkoutType {
        return WorkoutType.valueOf(workoutType)
    }
    
    @TypeConverter
    fun fromSyncStatus(syncStatus: RunSession.SyncStatus): String {
        return syncStatus.name
    }
    
    @TypeConverter
    fun toSyncStatus(syncStatus: String): RunSession.SyncStatus {
        return RunSession.SyncStatus.valueOf(syncStatus)
    }
    
    @TypeConverter
    fun fromRunContext(runContext: CoachingMessage.RunContext?): String? {
        return runContext?.let { runContextAdapter.toJson(it) }
    }
    
    @TypeConverter
    fun toRunContext(runContext: String?): CoachingMessage.RunContext? {
        return runContext?.let { runContextAdapter.fromJson(it) }
    }
}