package com.runiq.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.runiq.data.local.entities.HealthMetricCache
import kotlinx.coroutines.flow.Flow

/**
 * DAO for HealthMetricCache entity operations
 */
@Dao
abstract class HealthMetricCacheDao : BaseIdDao<HealthMetricCache, String>() {

    @Query("SELECT * FROM health_metric_cache ORDER BY date DESC, lastUpdated DESC")
    abstract override fun observeAll(): Flow<List<HealthMetricCache>>

    @Query("SELECT * FROM health_metric_cache ORDER BY date DESC, lastUpdated DESC")
    abstract override suspend fun getAll(): List<HealthMetricCache>

    @Query("SELECT COUNT(*) FROM health_metric_cache")
    abstract override suspend fun getCount(): Int

    @Query("DELETE FROM health_metric_cache")
    abstract override suspend fun deleteAll(): Int

    @Query("SELECT * FROM health_metric_cache WHERE id = :id")
    abstract override suspend fun getById(id: String): HealthMetricCache?

    @Query("SELECT * FROM health_metric_cache WHERE id = :id")
    abstract override fun observeById(id: String): Flow<HealthMetricCache?>

    @Query("DELETE FROM health_metric_cache WHERE id = :id")
    abstract override suspend fun deleteById(id: String): Int

    @Query("SELECT EXISTS(SELECT 1 FROM health_metric_cache WHERE id = :id)")
    abstract override suspend fun existsById(id: String): Boolean

    @Query("SELECT * FROM health_metric_cache WHERE id IN (:ids)")
    abstract override suspend fun getByIds(ids: List<String>): List<HealthMetricCache>

    @Query("DELETE FROM health_metric_cache WHERE id IN (:ids)")
    abstract override suspend fun deleteByIds(ids: List<String>): Int

    // Custom queries specific to HealthMetricCache
    
    @Query("SELECT * FROM health_metric_cache WHERE userId = :userId ORDER BY date DESC, lastUpdated DESC")
    abstract fun observeByUserId(userId: String): Flow<List<HealthMetricCache>>

    @Query("SELECT * FROM health_metric_cache WHERE userId = :userId AND metricType = :metricType ORDER BY date DESC")
    abstract suspend fun getByUserIdAndType(userId: String, metricType: String): List<HealthMetricCache>

    @Query("SELECT * FROM health_metric_cache WHERE userId = :userId AND metricType = :metricType ORDER BY date DESC")
    abstract fun observeByUserIdAndType(userId: String, metricType: String): Flow<List<HealthMetricCache>>

    @Query("SELECT * FROM health_metric_cache WHERE userId = :userId AND date = :date ORDER BY metricType ASC")
    abstract suspend fun getByUserIdAndDate(userId: String, date: String): List<HealthMetricCache>

    @Query("SELECT * FROM health_metric_cache WHERE userId = :userId AND date >= :startDate AND date <= :endDate ORDER BY date DESC, metricType ASC")
    abstract suspend fun getByUserIdInDateRange(
        userId: String, 
        startDate: String, 
        endDate: String
    ): List<HealthMetricCache>

    @Query("SELECT * FROM health_metric_cache WHERE userId = :userId AND metricType = :metricType AND date = :date LIMIT 1")
    abstract suspend fun getByUserIdTypeAndDate(
        userId: String, 
        metricType: String, 
        date: String
    ): HealthMetricCache?

    @Query("SELECT AVG(value) FROM health_metric_cache WHERE userId = :userId AND metricType = :metricType AND date >= :startDate AND date <= :endDate")
    abstract suspend fun getAverageValueInDateRange(
        userId: String,
        metricType: String,
        startDate: String,
        endDate: String
    ): Float?

    @Query("SELECT SUM(value) FROM health_metric_cache WHERE userId = :userId AND metricType = :metricType AND date >= :startDate AND date <= :endDate")
    abstract suspend fun getSumValueInDateRange(
        userId: String,
        metricType: String,
        startDate: String,
        endDate: String
    ): Float?

    @Query("SELECT MAX(value) FROM health_metric_cache WHERE userId = :userId AND metricType = :metricType AND date >= :startDate AND date <= :endDate")
    abstract suspend fun getMaxValueInDateRange(
        userId: String,
        metricType: String,
        startDate: String,
        endDate: String
    ): Float?

    @Query("SELECT * FROM health_metric_cache WHERE syncedToCloud = 0 ORDER BY lastUpdated ASC")
    abstract suspend fun getUnsyncedMetrics(): List<HealthMetricCache>

    @Query("UPDATE health_metric_cache SET syncedToCloud = 1 WHERE id = :id")
    abstract suspend fun markAsSynced(id: String)

    @Query("UPDATE health_metric_cache SET value = :value, lastUpdated = :timestamp WHERE userId = :userId AND metricType = :metricType AND date = :date")
    abstract suspend fun updateValue(
        userId: String,
        metricType: String,
        date: String,
        value: Float,
        timestamp: Long
    )

    @Query("DELETE FROM health_metric_cache WHERE userId = :userId AND date < :cutoffDate")
    abstract suspend fun deleteOldMetrics(userId: String, cutoffDate: String): Int

    @Query("SELECT DISTINCT metricType FROM health_metric_cache WHERE userId = :userId ORDER BY metricType ASC")
    abstract suspend fun getAvailableMetricTypes(userId: String): List<String>

    @Query("SELECT DISTINCT date FROM health_metric_cache WHERE userId = :userId AND metricType = :metricType ORDER BY date DESC")
    abstract suspend fun getAvailableDates(userId: String, metricType: String): List<String>
}