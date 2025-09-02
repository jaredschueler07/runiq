package com.runiq.base

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.runiq.data.local.database.RunIQDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException

/**
 * Base class for testing Room DAOs.
 * Provides an in-memory database for isolated testing.
 */
@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
abstract class BaseDaoTest : BaseUnitTest() {
    
    protected lateinit var database: RunIQDatabase
    
    @Before
    override fun setUp() {
        super.setUp()
        
        // Create an in-memory database for testing
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RunIQDatabase::class.java
        )
            .allowMainThreadQueries() // Only for testing
            .build()
    }
    
    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        database.close()
    }
}