package com.runiq.di

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.runiq.data.local.database.RunIQDatabase
import com.runiq.domain.repository.RunRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class HiltInjectionTest {
    
    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    
    @Inject
    lateinit var runRepository: RunRepository
    
    @Inject
    lateinit var database: RunIQDatabase
    
    private lateinit var context: Context
    
    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        hiltRule.inject()
    }
    
    @Test
    fun testHiltInjection_repositoryIsInjected() {
        // Verify that the repository is properly injected
        assertNotNull("RunRepository should be injected", runRepository)
    }
    
    @Test
    fun testHiltInjection_databaseIsInjected() {
        // Verify that the database is properly injected
        assertNotNull("RunIQDatabase should be injected", database)
        
        // Verify database is properly configured
        assertTrue("Database should be open", database.isOpen)
    }
    
    @Test
    fun testHiltInjection_contextIsAvailable() {
        // Verify that context injection works
        assertNotNull("Context should be available", context)
        assertEquals("Package name should match", "com.runiq", context.packageName)
    }
}