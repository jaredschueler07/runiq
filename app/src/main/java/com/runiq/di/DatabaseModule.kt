package com.runiq.di

import android.content.Context
import com.runiq.data.local.database.RunIQDatabase
import com.runiq.data.local.dao.*
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
    
    @Provides
    @Singleton
    fun provideRunIQDatabase(@ApplicationContext context: Context): RunIQDatabase {
        return RunIQDatabase.getInstance(context)
    }
    
    @Provides
    fun provideRunSessionDao(database: RunIQDatabase): RunSessionDao {
        return database.runSessionDao()
    }
    
    @Provides
    fun provideGpsTrackDao(database: RunIQDatabase): GpsTrackDao {
        return database.gpsTrackDao()
    }
    
    @Provides
    fun provideCoachDao(database: RunIQDatabase): CoachDao {
        return database.coachDao()
    }
    
    @Provides
    fun provideCoachTextLineDao(database: RunIQDatabase): CoachTextLineDao {
        return database.coachTextLineDao()
    }
    
    @Provides
    fun provideHealthMetricDao(database: RunIQDatabase): HealthMetricDao {
        return database.healthMetricDao()
    }
}