package com.runiq.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.runiq.domain.manager.LocationManager
import com.runiq.domain.manager.GpsTracker
import com.runiq.services.location.LocationManagerImpl
import com.runiq.services.location.GpsTrackerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {
    
    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(
        @ApplicationContext context: Context
    ): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationBindsModule {
    
    @Binds
    @Singleton
    abstract fun bindLocationManager(
        locationManagerImpl: LocationManagerImpl
    ): LocationManager
    
    @Binds
    @Singleton
    abstract fun bindGpsTracker(
        gpsTrackerImpl: GpsTrackerImpl
    ): GpsTracker
}