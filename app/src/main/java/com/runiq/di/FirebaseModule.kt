package com.runiq.di

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
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
        return Firebase.auth
    }
    
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return Firebase.firestore.apply {
            // Configure Firestore settings
            firestoreSettings = firestoreSettings.toBuilder()
                .setPersistenceEnabled(true)
                .setCacheSizeBytes(FirebaseFirestore.CACHE_SIZE_UNLIMITED)
                .build()
        }
    }
    
    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return Firebase.storage
    }
    
    @Provides
    @Singleton
    fun provideFirebaseAnalytics(): FirebaseAnalytics {
        return Firebase.analytics
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