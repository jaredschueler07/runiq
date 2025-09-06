package com.runiq.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
// TODO: Add back when manager interfaces and implementations are created
// import com.runiq.domain.manager.AudioManager
// import com.runiq.domain.manager.NotificationManager
// import com.runiq.domain.manager.PermissionManager
// import com.runiq.services.audio.AudioManagerImpl
// import com.runiq.services.notification.NotificationManagerImpl
// import com.runiq.services.permission.PermissionManagerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainDispatcher

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
    
    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
    
    @Provides
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
    
    @Provides
    @MainDispatcher
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
    
    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        
        return EncryptedSharedPreferences.create(
            context,
            "runiq_secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AppBindsModule {
    
    // TODO: Add back when manager interfaces and implementations are created
    /*
    @Binds
    @Singleton
    abstract fun bindAudioManager(
        audioManagerImpl: AudioManagerImpl
    ): AudioManager
    
    @Binds
    @Singleton
    abstract fun bindNotificationManager(
        notificationManagerImpl: NotificationManagerImpl
    ): NotificationManager
    
    @Binds
    @Singleton
    abstract fun bindPermissionManager(
        permissionManagerImpl: PermissionManagerImpl
    ): PermissionManager
    */
}