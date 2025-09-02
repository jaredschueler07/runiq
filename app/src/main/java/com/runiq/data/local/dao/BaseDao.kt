package com.runiq.data.local.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Base DAO interface providing common CRUD operations for all entities.
 * Follows Room best practices with suspend functions and Flow for reactive data.
 */
interface BaseDao<T> {

    /**
     * Insert a single entity
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: T): Long

    /**
     * Insert multiple entities
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<T>): List<Long>

    /**
     * Update an entity
     */
    @Update
    suspend fun update(entity: T): Int

    /**
     * Update multiple entities
     */
    @Update
    suspend fun updateAll(entities: List<T>): Int

    /**
     * Delete an entity
     */
    @Delete
    suspend fun delete(entity: T): Int

    /**
     * Delete multiple entities
     */
    @Delete
    suspend fun deleteAll(entities: List<T>): Int
}

/**
 * Extended base DAO with additional common operations
 */
abstract class BaseEntityDao<T> : BaseDao<T> {

    /**
     * Get all entities as a Flow for reactive updates
     */
    abstract fun observeAll(): Flow<List<T>>

    /**
     * Get all entities as a one-time operation
     */
    abstract suspend fun getAll(): List<T>

    /**
     * Get count of all entities
     */
    abstract suspend fun getCount(): Int

    /**
     * Delete all entities
     */
    abstract suspend fun deleteAll(): Int

    /**
     * Check if table is empty
     */
    suspend fun isEmpty(): Boolean = getCount() == 0

    /**
     * Check if table has data
     */
    suspend fun isNotEmpty(): Boolean = !isEmpty()
}

/**
 * Base DAO for entities with ID-based operations
 */
abstract class BaseIdDao<T, ID> : BaseEntityDao<T>() {

    /**
     * Get entity by ID
     */
    abstract suspend fun getById(id: ID): T?

    /**
     * Observe entity by ID
     */
    abstract fun observeById(id: ID): Flow<T?>

    /**
     * Delete entity by ID
     */
    abstract suspend fun deleteById(id: ID): Int

    /**
     * Check if entity exists by ID
     */
    abstract suspend fun existsById(id: ID): Boolean

    /**
     * Get multiple entities by IDs
     */
    abstract suspend fun getByIds(ids: List<ID>): List<T>

    /**
     * Delete multiple entities by IDs
     */
    abstract suspend fun deleteByIds(ids: List<ID>): Int
}