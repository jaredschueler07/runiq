package com.runiq.di

import android.content.Context
import androidx.room.Room
import com.runiq.data.local.database.RunIQDatabase
import com.runiq.data.local.dao.RunSessionDao
import com.runiq.data.local.dao.GpsTrackDao
import com.runiq.data.local.dao.CoachDao
import com.runiq.data.local.dao.CoachTextLineDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideRunIQDatabase(@ApplicationContext context: Context): RunIQDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            RunIQDatabase::class.java,
            "runiq_database"
        )
        .fallbackToDestructiveMigration() // For development - remove in production
        .build()
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
}