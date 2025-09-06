package com.runiq.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.runiq.data.local.converters.Converters
import com.runiq.data.local.dao.*
import com.runiq.data.local.entities.*

/**
 * Main database for RunIQ app
 * Manages all local data storage for runs, GPS tracking, coaching, and health metrics
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
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class RunIQDatabase : RoomDatabase() {

    // DAO access methods
    abstract fun runSessionDao(): RunSessionDao
    abstract fun gpsTrackDao(): GpsTrackDao
    abstract fun coachDao(): CoachDao
    abstract fun coachTextLineDao(): CoachTextLineDao
    abstract fun healthMetricDao(): HealthMetricDao

    companion object {
        const val DATABASE_NAME = "runiq_database"
    }
}