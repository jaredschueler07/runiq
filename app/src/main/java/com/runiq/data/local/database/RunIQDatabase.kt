package com.runiq.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.runiq.data.local.dao.GpsTrackDao
import com.runiq.data.local.dao.RunSessionDao
import com.runiq.domain.model.Coach
import com.runiq.domain.model.CoachingMessage
import com.runiq.domain.model.GpsTrackPoint
import com.runiq.domain.model.RunSession

/**
 * Room database for the RunIQ app.
 * Manages local storage of run sessions, GPS tracks, coaches, and coaching messages.
 */
@Database(
    entities = [
        RunSession::class,
        GpsTrackPoint::class,
        Coach::class,
        CoachingMessage::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class RunIQDatabase : RoomDatabase() {
    
    abstract fun runSessionDao(): RunSessionDao
    abstract fun gpsTrackDao(): GpsTrackDao
    
    companion object {
        @Volatile 
        private var INSTANCE: RunIQDatabase? = null
        
        fun getInstance(context: Context): RunIQDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    RunIQDatabase::class.java,
                    "runiq_database"
                )
                .addCallback(DatabaseCallback())
                .build().also { INSTANCE = it }
            }
        }
        
        /**
         * Creates an in-memory database for testing.
         */
        fun createInMemoryDatabase(context: Context): RunIQDatabase {
            return Room.inMemoryDatabaseBuilder(
                context.applicationContext,
                RunIQDatabase::class.java
            )
            .allowMainThreadQueries() // Only for testing
            .build()
        }
    }
}