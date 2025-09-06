package com.runiq.di

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
// TODO: Add back when health services are implemented
// import com.runiq.domain.manager.HealthConnectManager
// import com.runiq.services.health.HealthConnectManagerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HealthModule {
    
    @Provides
    @Singleton
    fun provideHealthConnectClient(
        @ApplicationContext context: Context
    ): HealthConnectClient {
        return HealthConnectClient.getOrCreate(context)
    }
}

// TODO: Add back when health services are implemented
/*
@Module
@InstallIn(SingletonComponent::class)
abstract class HealthBindsModule {
    
    @Binds
    @Singleton
    abstract fun bindHealthConnectManager(
        healthConnectManagerImpl: HealthConnectManagerImpl
    ): HealthConnectManager
}
*/