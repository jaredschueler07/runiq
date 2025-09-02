package com.runiq.domain.model

import androidx.annotation.Keep

/**
 * Represents the synchronization status of data across different sources
 */
@Keep
enum class SyncStatus {
    PENDING,    // Waiting to be synced
    SYNCING,    // Currently being synced
    SYNCED,     // Successfully synced
    FAILED      // Sync failed, needs retry
}