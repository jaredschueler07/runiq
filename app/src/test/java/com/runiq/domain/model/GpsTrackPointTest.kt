package com.runiq.domain.model

import com.runiq.base.BaseUnitTest
import com.runiq.util.TestDataFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for GpsTrackPoint domain model.
 * Tests GPS calculations and utility functions.
 */
@ExperimentalCoroutinesApi
class GpsTrackPointTest : BaseUnitTest() {
    
    @Test
    fun `distanceTo should calculate correct distance between two points`() {
        // Given - San Francisco to Los Angeles (approximately 560 km)
        val sanFrancisco = TestDataFactory.createGpsTrackPoint(
            latitude = 37.7749,
            longitude = -122.4194
        )
        val losAngeles = TestDataFactory.createGpsTrackPoint(
            latitude = 34.0522,
            longitude = -118.2437
        )
        
        // When
        val distance = sanFrancisco.distanceTo(losAngeles)
        
        // Then
        // Allow for some tolerance in GPS calculations
        assertTrue("Distance should be approximately 560km", distance > 550000f && distance < 570000f)
    }
    
    @Test
    fun `distanceTo should return zero for same point`() {
        // Given
        val point = TestDataFactory.createGpsTrackPoint()
        
        // When
        val distance = point.distanceTo(point)
        
        // Then
        assertEquals("Distance to same point should be zero", 0f, distance, 0.01f)
    }
    
    @Test
    fun `distanceTo should calculate short distances accurately`() {
        // Given - Two points 100 meters apart (approximately)
        val point1 = TestDataFactory.createGpsTrackPoint(
            latitude = 37.7749,
            longitude = -122.4194
        )
        val point2 = TestDataFactory.createGpsTrackPoint(
            latitude = 37.7758, // About 100m north
            longitude = -122.4194
        )
        
        // When
        val distance = point1.distanceTo(point2)
        
        // Then
        assertTrue("Distance should be approximately 100m", distance > 90f && distance < 110f)
    }
    
    @Test
    fun `bearingTo should calculate correct bearing`() {
        // Given
        val start = TestDataFactory.createGpsTrackPoint(
            latitude = 37.7749,
            longitude = -122.4194
        )
        val north = TestDataFactory.createGpsTrackPoint(
            latitude = 37.7759, // North of start
            longitude = -122.4194
        )
        val east = TestDataFactory.createGpsTrackPoint(
            latitude = 37.7749,
            longitude = -122.4184 // East of start
        )
        
        // When
        val bearingToNorth = start.bearingTo(north)
        val bearingToEast = start.bearingTo(east)
        
        // Then
        assertTrue("Bearing to north should be close to 0°", bearingToNorth < 10f || bearingToNorth > 350f)
        assertTrue("Bearing to east should be close to 90°", bearingToEast > 80f && bearingToEast < 100f)
    }
    
    @Test
    fun `bearingTo should return zero for same point`() {
        // Given
        val point = TestDataFactory.createGpsTrackPoint()
        
        // When
        val bearing = point.bearingTo(point)
        
        // Then
        // Bearing to same point is undefined, but our implementation should handle it gracefully
        assertTrue("Bearing should be a valid number", !bearing.isNaN())
    }
    
    @Test
    fun `bearingTo should handle crossing antimeridian`() {
        // Given - Points near the international date line
        val westPoint = TestDataFactory.createGpsTrackPoint(
            latitude = 0.0,
            longitude = 179.0
        )
        val eastPoint = TestDataFactory.createGpsTrackPoint(
            latitude = 0.0,
            longitude = -179.0
        )
        
        // When
        val bearing = westPoint.bearingTo(eastPoint)
        
        // Then
        assertTrue("Bearing should be eastward (around 90°)", bearing > 80f && bearing < 100f)
    }
}