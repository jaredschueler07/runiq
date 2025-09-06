package com.runiq.di

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.qualifiers.ApplicationContext
// TODO: Add back when remote services are implemented
// import com.runiq.data.remote.service.FirestoreService
// import com.runiq.data.remote.service.FirestoreServiceImpl
// import com.runiq.data.remote.service.FirebaseAnalyticsService
// import com.runiq.data.remote.service.FirebaseAnalyticsServiceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
    
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance().apply {
            // Configure Firestore settings
            firestoreSettings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build()
        }
    }
    
    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }
    
    @Provides
    @Singleton
    fun provideFirebaseAnalytics(@ApplicationContext context: Context): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(context)
    }
}

// TODO: Add back when remote services are implemented
/*
@Module
@InstallIn(SingletonComponent::class)
abstract class FirebaseBindsModule {
    
    @Binds
    @Singleton
    abstract fun bindFirestoreService(
        firestoreServiceImpl: FirestoreServiceImpl
    ): FirestoreService
    
    @Binds
    @Singleton
    abstract fun bindFirebaseAnalyticsService(
        firebaseAnalyticsServiceImpl: FirebaseAnalyticsServiceImpl
    ): FirebaseAnalyticsService
}
*/