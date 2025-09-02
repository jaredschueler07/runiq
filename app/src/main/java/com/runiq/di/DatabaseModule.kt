package com.runiq.di

import android.content.Context
import androidx.room.Room
import com.runiq.data.local.database.RunIQDatabase
import com.runiq.data.local.database.TypeConverters
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing database-related dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provides the main RunIQ database instance
     */
    @Provides
    @Singleton
    fun provideRunIQDatabase(
        @ApplicationContext context: Context
    ): RunIQDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            RunIQDatabase::class.java,
            "runiq_database"
        )
        .addTypeConverter(TypeConverters())
        .fallbackToDestructiveMigration() // TODO: Add proper migrations for production
        .build()
    }

    /**
     * Provides RunSessionDao
     */
    @Provides
    fun provideRunSessionDao(database: RunIQDatabase) = database.runSessionDao()

    /**
     * Provides GpsTrackDao
     */
    @Provides
    fun provideGpsTrackDao(database: RunIQDatabase) = database.gpsTrackDao()

    /**
     * Provides CoachDao
     */
    @Provides
    fun provideCoachDao(database: RunIQDatabase) = database.coachDao()

    /**
     * Provides CoachTextLineDao
     */
    @Provides
    fun provideCoachTextLineDao(database: RunIQDatabase) = database.coachTextLineDao()

    /**
     * Provides HealthMetricCacheDao
     */
    @Provides
    fun provideHealthMetricCacheDao(database: RunIQDatabase) = database.healthMetricCacheDao()
}