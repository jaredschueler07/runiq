package com.runiq.data.local.dao

import androidx.room.*
import com.runiq.data.local.entities.DataSource
import com.runiq.data.local.entities.HealthMetricCacheEntity
import com.runiq.data.local.entities.HealthMetricType
import com.runiq.domain.model.SyncStatus
import kotlinx.coroutines.flow.Flow

/**
 * DAO for HealthMetricCache entity with comprehensive health data operations
 */
@Dao
interface HealthMetricDao {
    
    // Basic CRUD operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(metric: HealthMetricCacheEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(metrics: List<HealthMetricCacheEntity>): List<Long>
    
    @Update
    suspend fun update(metric: HealthMetricCacheEntity): Int
    
    @Delete
    suspend fun delete(metric: HealthMetricCacheEntity): Int
    
    @Query("DELETE FROM health_metric_cache WHERE id = :id")
    suspend fun deleteById(id: Long): Int
    
    @Query("DELETE FROM health_metric_cache WHERE session_id = :sessionId")
    suspend fun deleteBySession(sessionId: String): Int
    
    // Basic queries
    @Query("SELECT * FROM health_metric_cache WHERE id = :id")
    suspend fun getById(id: Long): HealthMetricCacheEntity?
    
    @Query("""
        SELECT * FROM health_metric_cache 
        WHERE session_id = :sessionId 
        ORDER BY timestamp ASC
    """)
    suspend fun getBySession(sessionId: String): List<HealthMetricCacheEntity>
    
    @Query("""
        SELECT * FROM health_metric_cache 
        WHERE session_id = :sessionId 
        ORDER BY timestamp ASC
    """)
    fun observeBySession(sessionId: String): Flow<List<HealthMetricCacheEntity>>
    
    // Metric type filtering
    @Query("""
        SELECT * FROM health_metric_cache 
        WHERE session_id = :sessionId 
        AND metric_type = :metricType 
        ORDER BY timestamp ASC
    """)
    suspend fun getBySessionAndType(
        sessionId: String, 
        metricType: HealthMetricType
    ): List<HealthMetricCacheEntity>
    
    @Query("""
        SELECT * FROM health_metric_cache 
        WHERE session_id = :sessionId 
        AND metric_type = :metricType 
        ORDER BY timestamp ASC
    """)
    fun observeBySessionAndType(
        sessionId: String, 
        metricType: HealthMetricType
    ): Flow<List<HealthMetricCacheEntity>>
    
    @Query("""
        SELECT * FROM health_metric_cache 
        WHERE session_id = :sessionId 
        AND metric_type IN (:metricTypes) 
        ORDER BY metric_type ASC, timestamp ASC
    """)
    suspend fun getBySessionAndTypes(
        sessionId: String, 
        metricTypes: List<HealthMetricType>
    ): List<HealthMetricCacheEntity>
    
    // Time-based queries
    @Query("""
        SELECT * FROM health_metric_cache 
        WHERE session_id = :sessionId 
        AND metric_type = :metricType 
        AND timestamp BETWEEN :startTime AND :endTime 
        ORDER BY timestamp ASC
    """)
    suspend fun getByTimeRange(
        sessionId: String,
        metricType: HealthMetricType,
        startTime: Long,
        endTime: Long
    ): List<HealthMetricCacheEntity>
    
    @Query("""
        SELECT * FROM health_metric_cache 
        WHERE session_id = :sessionId 
        AND metric_type = :metricType 
        AND timestamp >= :sinceTimestamp 
        ORDER BY timestamp ASC
    """)
    suspend fun getRecentMetrics(
        sessionId: String,
        metricType: HealthMetricType,
        sinceTimestamp: Long
    ): List<HealthMetricCacheEntity>
    
    @Query("""
        SELECT * FROM health_metric_cache 
        WHERE session_id = :sessionId 
        AND metric_type = :metricType 
        AND timestamp >= :sinceTimestamp 
        ORDER BY timestamp ASC
    """)
    fun observeRecentMetrics(
        sessionId: String,
        metricType: HealthMetricType,
        sinceTimestamp: Long
    ): Flow<List<HealthMetricCacheEntity>>
    
    // Data source filtering
    @Query("""
        SELECT * FROM health_metric_cache 
        WHERE session_id = :sessionId 
        AND source = :source 
        ORDER BY timestamp ASC
    """)
    suspend fun getBySource(sessionId: String, source: DataSource): List<HealthMetricCacheEntity>
    
    @Query("""
        SELECT * FROM health_metric_cache 
        WHERE session_id = :sessionId 
        AND metric_type = :metricType 
        AND source IN (:sources) 
        ORDER BY timestamp ASC
    """)
    suspend fun getByPreferredSources(
        sessionId: String,
        metricType: HealthMetricType,
        sources: List<DataSource>
    ): List<HealthMetricCacheEntity>
    
    // Latest values
    @Query("""
        SELECT * FROM health_metric_cache 
        WHERE session_id = :sessionId 
        AND metric_type = :metricType 
        ORDER BY timestamp DESC 
        LIMIT 1
    """)
    suspend fun getLatestValue(sessionId: String, metricType: HealthMetricType): HealthMetricCacheEntity?
    
    @Query("""
        SELECT * FROM health_metric_cache 
        WHERE session_id = :sessionId 
        AND metric_type = :metricType 
        ORDER BY timestamp DESC 
        LIMIT 1
    """)
    fun observeLatestValue(sessionId: String, metricType: HealthMetricType): Flow<HealthMetricCacheEntity?>
    
    @Query("""
        SELECT * FROM health_metric_cache 
        WHERE session_id = :sessionId 
        AND metric_type = :metricType 
        ORDER BY timestamp DESC 
        LIMIT :count
    """)
    suspend fun getLatestValues(
        sessionId: String, 
        metricType: HealthMetricType, 
        count: Int
    ): List<HealthMetricCacheEntity>
    
    // Statistics and aggregations
    @Query("""
        SELECT AVG(value) FROM health_metric_cache 
        WHERE session_id = :sessionId 
        AND metric_type = :metricType
    """)
    suspend fun getAverageValue(sessionId: String, metricType: HealthMetricType): Double?
    
    @Query("""
        SELECT MIN(value) as min_value, MAX(value) as max_value 
        FROM health_metric_cache 
        WHERE session_id = :sessionId 
        AND metric_type = :metricType
    """)
    suspend fun getValueRange(sessionId: String, metricType: HealthMetricType): ValueRange?
    
    @Query("""
        SELECT COUNT(*) FROM health_metric_cache 
        WHERE session_id = :sessionId 
        AND metric_type = :metricType
    """)
    suspend fun getMetricCount(sessionId: String, metricType: HealthMetricType): Int
    
    @Query("""
        SELECT value FROM health_metric_cache 
        WHERE session_id = :sessionId 
        AND metric_type = :metricType 
        ORDER BY timestamp ASC
    """)
    suspend fun getValueSeries(sessionId: String, metricType: HealthMetricType): List<Double>
    
    // Accuracy and quality filtering
    @Query("""
        SELECT * FROM health_metric_cache 
        WHERE session_id = :sessionId 
        AND metric_type = :metricType 
        AND accuracy IS NOT NULL 
        AND accuracy >= :minAccuracy 
        ORDER BY timestamp ASC
    """)
    suspend fun getAccurateMetrics(
        sessionId: String,
        metricType: HealthMetricType,
        minAccuracy: Float
    ): List<HealthMetricCacheEntity>
    
    @Query("""
        SELECT * FROM health_metric_cache 
        WHERE session_id = :sessionId 
        AND metric_type = :metricType 
        ORDER BY CASE WHEN accuracy IS NULL THEN 1 ELSE 0 END, accuracy DESC, timestamp ASC
    """)
    suspend fun getMetricsByAccuracy(
        sessionId: String,
        metricType: HealthMetricType
    ): List<HealthMetricCacheEntity>
    
    // Sync status operations
    @Query("SELECT * FROM health_metric_cache WHERE sync_status = :status")
    suspend fun getBySyncStatus(status: SyncStatus): List<HealthMetricCacheEntity>
    
    @Query("SELECT * FROM health_metric_cache WHERE sync_status IN ('PENDING', 'FAILED')")
    suspend fun getPendingSyncMetrics(): List<HealthMetricCacheEntity>
    
    @Query("""
        UPDATE health_metric_cache 
        SET sync_status = :status, updated_at = :timestamp 
        WHERE id = :id
    """)
    suspend fun updateSyncStatus(
        id: Long, 
        status: SyncStatus, 
        timestamp: Long = System.currentTimeMillis()
    ): Int
    
    @Query("UPDATE health_metric_cache SET health_connect_id = :healthConnectId WHERE id = :id")
    suspend fun updateHealthConnectId(id: Long, healthConnectId: String?): Int
    
    // Device-specific queries
    @Query("""
        SELECT * FROM health_metric_cache 
        WHERE session_id = :sessionId 
        AND device_id = :deviceId 
        ORDER BY timestamp ASC
    """)
    suspend fun getByDevice(sessionId: String, deviceId: String): List<HealthMetricCacheEntity>
    
    @Query("SELECT DISTINCT device_id FROM health_metric_cache WHERE session_id = :sessionId AND device_id IS NOT NULL")
    suspend fun getDevicesForSession(sessionId: String): List<String>
    
    // Sampling and optimization
    @Query("""
        SELECT * FROM health_metric_cache 
        WHERE session_id = :sessionId 
        AND metric_type = :metricType 
        AND id % :interval = 0 
        ORDER BY timestamp ASC
    """)
    suspend fun getSampledMetrics(
        sessionId: String,
        metricType: HealthMetricType,
        interval: Int
    ): List<HealthMetricCacheEntity>
    
    // Heart rate specific queries (common use case)
    @Query("""
        SELECT * FROM health_metric_cache 
        WHERE session_id = :sessionId 
        AND metric_type = 'HEART_RATE' 
        ORDER BY timestamp ASC
    """)
    suspend fun getHeartRateData(sessionId: String): List<HealthMetricCacheEntity>
    
    @Query("""
        SELECT AVG(value) FROM health_metric_cache 
        WHERE session_id = :sessionId 
        AND metric_type = 'HEART_RATE'
    """)
    suspend fun getAverageHeartRate(sessionId: String): Double?
    
    @Query("""
        SELECT MAX(value) FROM health_metric_cache 
        WHERE session_id = :sessionId 
        AND metric_type = 'HEART_RATE'
    """)
    suspend fun getMaxHeartRate(sessionId: String): Double?
    
    // Batch operations
    @Transaction
    suspend fun replaceSessionMetrics(sessionId: String, metrics: List<HealthMetricCacheEntity>) {
        deleteBySession(sessionId)
        insertAll(metrics)
    }
    
    @Transaction
    suspend fun mergeMetrics(newMetrics: List<HealthMetricCacheEntity>) {
        // Insert new metrics, replacing any with the same session, type, and timestamp
        insertAll(newMetrics)
    }
    
    // Cleanup operations
    @Query("DELETE FROM health_metric_cache WHERE timestamp < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long): Int
    
    @Query("""
        DELETE FROM health_metric_cache 
        WHERE sync_status = 'SYNCED' 
        AND updated_at < :timestamp
    """)
    suspend fun cleanupSyncedMetrics(timestamp: Long): Int
    
    @Query("""
        DELETE FROM health_metric_cache 
        WHERE accuracy IS NOT NULL 
        AND accuracy < :minAccuracy
    """)
    suspend fun deleteInaccurateMetrics(minAccuracy: Float): Int
    
    // Analytics queries
    @Query("""
        SELECT metric_type, COUNT(*) as count 
        FROM health_metric_cache 
        WHERE session_id = :sessionId 
        GROUP BY metric_type
    """)
    suspend fun getMetricTypeDistribution(sessionId: String): List<MetricTypeCount>
    
    @Query("""
        SELECT source, COUNT(*) as count 
        FROM health_metric_cache 
        WHERE session_id = :sessionId 
        GROUP BY source
    """)
    suspend fun getSourceDistribution(sessionId: String): List<SourceCount>
    
    @Query("""
        SELECT AVG(accuracy) FROM health_metric_cache 
        WHERE session_id = :sessionId 
        AND accuracy IS NOT NULL
    """)
    suspend fun getAverageAccuracy(sessionId: String): Float?
}

// Data classes for complex query results
data class ValueRange(
    @ColumnInfo(name = "min_value")
    val minValue: Double,
    @ColumnInfo(name = "max_value")
    val maxValue: Double
)

data class MetricTypeCount(
    @ColumnInfo(name = "metric_type")
    val metricType: HealthMetricType,
    val count: Int
)

data class SourceCount(
    val source: DataSource,
    val count: Int
)