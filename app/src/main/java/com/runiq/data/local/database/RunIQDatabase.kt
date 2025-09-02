package com.runiq.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import android.content.Context
import com.runiq.data.local.dao.RunSessionDao
import com.runiq.data.local.dao.GpsTrackDao
import com.runiq.data.local.dao.CoachDao
import com.runiq.data.local.dao.CoachTextLineDao
import com.runiq.data.local.dao.HealthMetricCacheDao
import com.runiq.data.local.entities.RunSession
import com.runiq.data.local.entities.GpsTrackPoint
import com.runiq.data.local.entities.Coach
import com.runiq.data.local.entities.CoachTextLine
import com.runiq.data.local.entities.HealthMetricCache
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.Types
import timber.log.Timber

/**
 * Main Room database for RunIQ app
 * Contains all entities and provides DAOs for data access
 */
@Database(
    entities = [
        RunSession::class,
        GpsTrackPoint::class,
        Coach::class,
        CoachTextLine::class,
        HealthMetricCache::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(com.runiq.data.local.database.TypeConverters::class)
abstract class RunIQDatabase : RoomDatabase() {
    
    abstract fun runSessionDao(): RunSessionDao
    abstract fun gpsTrackDao(): GpsTrackDao
    abstract fun coachDao(): CoachDao
    abstract fun coachTextLineDao(): CoachTextLineDao
    abstract fun healthMetricCacheDao(): HealthMetricCacheDao
}

/**
 * Type converters for complex data types in Room database
 */
class TypeConverters {
    
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    // String List Converter
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        val adapter = moshi.adapter<List<String>>(
            Types.newParameterizedType(List::class.java, String::class.java)
        )
        return adapter.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val adapter = moshi.adapter<List<String>>(
            Types.newParameterizedType(List::class.java, String::class.java)
        )
        return adapter.fromJson(value) ?: emptyList()
    }

    // String Set Converter
    @TypeConverter
    fun fromStringSet(value: Set<String>): String {
        val adapter = moshi.adapter<Set<String>>(
            Types.newParameterizedType(Set::class.java, String::class.java)
        )
        return adapter.toJson(value)
    }

    @TypeConverter
    fun toStringSet(value: String): Set<String> {
        val adapter = moshi.adapter<Set<String>>(
            Types.newParameterizedType(Set::class.java, String::class.java)
        )
        return adapter.fromJson(value) ?: emptySet()
    }

    // Float List Converter (for GPS coordinates, heart rate data, etc.)
    @TypeConverter
    fun fromFloatList(value: List<Float>): String {
        val adapter = moshi.adapter<List<Float>>(
            Types.newParameterizedType(List::class.java, Float::class.java)
        )
        return adapter.toJson(value)
    }

    @TypeConverter
    fun toFloatList(value: String): List<Float> {
        val adapter = moshi.adapter<List<Float>>(
            Types.newParameterizedType(List::class.java, Float::class.java)
        )
        return adapter.fromJson(value) ?: emptyList()
    }

    // Int List Converter (for heart rate zones, step counts, etc.)
    @TypeConverter
    fun fromIntList(value: List<Int>): String {
        val adapter = moshi.adapter<List<Int>>(
            Types.newParameterizedType(List::class.java, Int::class.java)
        )
        return adapter.toJson(value)
    }

    @TypeConverter
    fun toIntList(value: String): List<Int> {
        val adapter = moshi.adapter<List<Int>>(
            Types.newParameterizedType(List::class.java, Int::class.java)
        )
        return adapter.fromJson(value) ?: emptyList()
    }

    // Map<String, Any> Converter (for flexible data storage)
    @TypeConverter
    fun fromStringAnyMap(value: Map<String, Any>): String {
        val adapter = moshi.adapter<Map<String, Any>>(
            Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java)
        )
        return adapter.toJson(value)
    }

    @TypeConverter
    fun toStringAnyMap(value: String): Map<String, Any> {
        val adapter = moshi.adapter<Map<String, Any>>(
            Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java)
        )
        return adapter.fromJson(value) ?: emptyMap()
    }
}