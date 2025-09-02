package com.runiq.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.runiq.data.local.database.RunIQDatabase
import com.runiq.domain.repository.RunRepository
import com.squareup.moshi.Moshi
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.CoroutineScope
import okhttp3.OkHttpClient
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class DependencyInjectionTest {
    
    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    
    @Inject
    lateinit var runRepository: RunRepository
    
    @Inject
    lateinit var database: RunIQDatabase
    
    @Inject
    lateinit var moshi: Moshi
    
    @Inject
    lateinit var okHttpClient: OkHttpClient
    
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    
    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope
    
    @Before
    fun setup() {
        hiltRule.inject()
    }
    
    @Test
    fun testRepositoryInjection() {
        assertNotNull("RunRepository should be injected", runRepository)
    }
    
    @Test
    fun testDatabaseInjection() {
        assertNotNull("Database should be injected", database)
        assertTrue("Database should be open", database.isOpen)
    }
    
    @Test
    fun testNetworkComponentsInjection() {
        assertNotNull("Moshi should be injected", moshi)
        assertNotNull("OkHttpClient should be injected", okHttpClient)
        
        // Verify OkHttp configuration
        assertEquals("Connection timeout should be 30 seconds", 
            30000, okHttpClient.connectTimeoutMillis)
    }
    
    @Test
    fun testApplicationLevelDependencies() {
        assertNotNull("SharedPreferences should be injected", sharedPreferences)
        assertNotNull("Application scope should be injected", applicationScope)
    }
    
    @Test
    fun testMoshiConfiguration() {
        // Test that Moshi can handle Kotlin classes
        val adapter = moshi.adapter(TestData::class.java)
        val testData = TestData("test", 42)
        
        val json = adapter.toJson(testData)
        val parsed = adapter.fromJson(json)
        
        assertEquals(testData, parsed)
    }
    
    data class TestData(val name: String, val value: Int)
}