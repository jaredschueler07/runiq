package com.runiq.testing.base

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.runiq.data.local.database.RunIQDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Base class for all DAO tests providing in-memory database setup
 */
@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
abstract class BaseDaoTest : BaseUnitTest() {
    
    protected lateinit var database: RunIQDatabase
    private lateinit var context: Context
    
    @Before
    override fun setup() {
        super.setup()
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(
            context,
            RunIQDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        
        onDatabaseCreated()
    }
    
    @After
    override fun tearDown() {
        database.close()
        super.tearDown()
    }
    
    /**
     * Override to perform additional setup after database is created
     */
    protected open fun onDatabaseCreated() {}
    
    /**
     * Helper to insert test data and verify it was inserted
     */
    protected suspend fun <T> insertAndVerify(
        insertAction: suspend () -> Unit,
        verifyAction: suspend () -> T?
    ): T {
        insertAction()
        val result = verifyAction()
        assert(result != null) { "Failed to insert and retrieve test data" }
        return result!!
    }
    
    /**
     * Helper to test batch operations
     */
    protected suspend fun <T> testBatchOperation(
        items: List<T>,
        insertAction: suspend (List<T>) -> Unit,
        retrieveAction: suspend () -> List<T>
    ) {
        insertAction(items)
        val retrieved = retrieveAction()
        assert(retrieved.size == items.size) {
            "Expected ${items.size} items but got ${retrieved.size}"
        }
    }
}