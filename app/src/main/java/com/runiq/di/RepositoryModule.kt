package com.runiq.di

import com.runiq.data.repository.RunRepositoryImpl
import com.runiq.data.repository.CoachRepositoryImpl
import com.runiq.data.repository.MusicRepositoryImpl
import com.runiq.data.repository.UserRepositoryImpl
import com.runiq.data.repository.HealthRepositoryImpl
import com.runiq.domain.repository.RunRepository
import com.runiq.domain.repository.CoachRepository
import com.runiq.domain.repository.MusicRepository
import com.runiq.domain.repository.UserRepository
import com.runiq.domain.repository.HealthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindRunRepository(
        runRepositoryImpl: RunRepositoryImpl
    ): RunRepository
    
    @Binds
    @Singleton
    abstract fun bindCoachRepository(
        coachRepositoryImpl: CoachRepositoryImpl
    ): CoachRepository
    
    @Binds
    @Singleton
    abstract fun bindMusicRepository(
        musicRepositoryImpl: MusicRepositoryImpl
    ): MusicRepository
    
    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository
    
    @Binds
    @Singleton
    abstract fun bindHealthRepository(
        healthRepositoryImpl: HealthRepositoryImpl
    ): HealthRepository
}