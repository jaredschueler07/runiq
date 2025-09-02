package com.runiq.di

import com.runiq.BuildConfig
import com.runiq.data.remote.api.GeminiApiService
import com.runiq.data.remote.api.ElevenLabsApiService
import com.runiq.data.remote.api.SpotifyApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Qualifiers for different API endpoints
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GeminiRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ElevenLabsRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SpotifyRetrofit

/**
 * Hilt module for providing network-related dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Provides Moshi JSON adapter
     */
    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    /**
     * Provides HTTP logging interceptor
     */
    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor { message ->
            Timber.tag("HTTP").d(message)
        }.apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    /**
     * Provides base OkHttp client with common configuration
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Provides Gemini API Retrofit instance
     */
    @Provides
    @Singleton
    @GeminiRetrofit
    fun provideGeminiRetrofit(
        moshi: Moshi,
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(
                okHttpClient.newBuilder()
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .addHeader("Content-Type", "application/json")
                            .build()
                        chain.proceed(request)
                    }
                    .build()
            )
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    /**
     * Provides ElevenLabs API Retrofit instance
     */
    @Provides
    @Singleton
    @ElevenLabsRetrofit
    fun provideElevenLabsRetrofit(
        moshi: Moshi,
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.elevenlabs.io/")
            .client(
                okHttpClient.newBuilder()
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .addHeader("Content-Type", "application/json")
                            .build()
                        chain.proceed(request)
                    }
                    .build()
            )
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    /**
     * Provides Spotify API Retrofit instance
     */
    @Provides
    @Singleton
    @SpotifyRetrofit
    fun provideSpotifyRetrofit(
        moshi: Moshi,
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.spotify.com/")
            .client(
                okHttpClient.newBuilder()
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .addHeader("Content-Type", "application/json")
                            .build()
                        chain.proceed(request)
                    }
                    .build()
            )
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    /**
     * Provides Gemini API service
     */
    @Provides
    @Singleton
    fun provideGeminiApiService(@GeminiRetrofit retrofit: Retrofit): GeminiApiService {
        return retrofit.create(GeminiApiService::class.java)
    }

    /**
     * Provides ElevenLabs API service
     */
    @Provides
    @Singleton
    fun provideElevenLabsApiService(@ElevenLabsRetrofit retrofit: Retrofit): ElevenLabsApiService {
        return retrofit.create(ElevenLabsApiService::class.java)
    }

    /**
     * Provides Spotify API service
     */
    @Provides
    @Singleton
    fun provideSpotifyApiService(@SpotifyRetrofit retrofit: Retrofit): SpotifyApiService {
        return retrofit.create(SpotifyApiService::class.java)
    }
}