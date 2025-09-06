package com.runiq.data.local.database

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Database callback for handling database creation and migration events.
 */
class DatabaseCallback : RoomDatabase.Callback() {
    
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        Timber.d("RunIQ Database created")
        
        // Populate initial data if needed
        CoroutineScope(Dispatchers.IO).launch {
            // Could populate default coaches here
        }
    }
    
    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        Timber.d("RunIQ Database opened")
    }
}