package com.runiq.presentation.screens.run

import androidx.lifecycle.viewModelScope
import com.runiq.core.util.Result
import com.runiq.data.local.entities.GpsTrackPointEntity
import com.runiq.data.local.entities.RunSessionEntity
import com.runiq.data.repository.RunStatistics
import com.runiq.domain.model.WorkoutType
import com.runiq.domain.usecase.*
import com.runiq.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the run screen managing active run sessions
 */
@HiltViewModel
class RunViewModel @Inject constructor(
    private val startRunUseCase: StartRunUseCase,
    private val endRunUseCase: EndRunUseCase,
    private val observeActiveRunUseCase: ObserveActiveRunUseCase,
    private val saveGpsTrackPointUseCase: SaveGpsTrackPointUseCase,
    private val getRunHistoryUseCase: GetRunHistoryUseCase
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(RunUiState())
    val uiState: StateFlow<RunUiState> = _uiState.asStateFlow()

    private val _activeRun = MutableStateFlow<RunSessionEntity?>(null)
    val activeRun: StateFlow<RunSessionEntity?> = _activeRun.asStateFlow()

    private val _gpsPoints = MutableStateFlow<List<GpsTrackPointEntity>>(emptyList())
    val gpsPoints: StateFlow<List<GpsTrackPointEntity>> = _gpsPoints.asStateFlow()

    private val _runStatistics = MutableStateFlow<RunStatistics?>(null)
    val runStatistics: StateFlow<RunStatistics?> = _runStatistics.asStateFlow()

    private var currentUserId: String? = null

    fun setUserId(userId: String) {
        currentUserId = userId
        observeActiveRun(userId)
    }

    private fun observeActiveRun(userId: String) {
        viewModelScope.launch {
            observeActiveRunUseCase(userId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _activeRun.value = result.data
                        updateUiState { copy(isRunning = result.data != null && result.data.endTime == null) }
                    }
                    is Result.Error -> {
                        handleError(result.exception)
                    }
                    is Result.Loading -> {
                        // Handle loading if needed
                    }
                }
            }
        }
    }

    fun startRun(workoutType: WorkoutType, targetPace: Float? = null) {
        val userId = currentUserId ?: return

        launchWithErrorHandling {
            val result = executeWithLoading {
                startRunUseCase(
                    StartRunUseCase.Params(
                        userId = userId,
                        workoutType = workoutType,
                        targetPace = targetPace
                    )
                )
            }

            when (result) {
                is Result.Success -> {
                    updateUiState { 
                        copy(
                            isRunning = true,
                            currentSessionId = result.data,
                            workoutType = workoutType,
                            targetPace = targetPace
                        ) 
                    }
                }
                is Result.Error -> {
                    // Error is already handled in executeWithLoading
                }
                is Result.Loading -> {
                    // Loading is already handled in executeWithLoading
                }
            }
        }
    }

    fun endRun() {
        val sessionId = _uiState.value.currentSessionId ?: return

        launchWithErrorHandling {
            // Calculate metrics from current state
            val currentState = _uiState.value
            val result = executeWithLoading {
                endRunUseCase(
                    EndRunUseCase.Params(
                        sessionId = sessionId,
                        distance = currentState.totalDistance,
                        duration = currentState.elapsedTime,
                        averagePace = currentState.averagePace,
                        calories = currentState.calories,
                        steps = currentState.steps
                    )
                )
            }

            when (result) {
                is Result.Success -> {
                    _runStatistics.value = result.data
                    updateUiState { 
                        copy(
                            isRunning = false,
                            isRunCompleted = true
                        ) 
                    }
                }
                is Result.Error -> {
                    // Error is already handled in executeWithLoading
                }
                is Result.Loading -> {
                    // Loading is already handled in executeWithLoading
                }
            }
        }
    }

    fun saveGpsPoint(
        latitude: Double,
        longitude: Double,
        altitude: Double? = null,
        speed: Float? = null,
        accuracy: Float? = null
    ) {
        val sessionId = _uiState.value.currentSessionId ?: return

        launchWithErrorHandling {
            val result = saveGpsTrackPointUseCase(
                SaveGpsTrackPointUseCase.Params(
                    sessionId = sessionId,
                    latitude = latitude,
                    longitude = longitude,
                    altitude = altitude,
                    speed = speed,
                    accuracy = accuracy
                )
            )

            when (result) {
                is Result.Success -> {
                    // Update UI with new GPS point data
                    updateMetricsFromGpsPoint(latitude, longitude, speed)
                }
                is Result.Error -> {
                    handleError(result.exception)
                }
                is Result.Loading -> {
                    // Handle loading if needed
                }
            }
        }
    }

    fun pauseRun() {
        updateUiState { copy(isPaused = true) }
        // TODO: Implement pause logic with use case
    }

    fun resumeRun() {
        updateUiState { copy(isPaused = false) }
        // TODO: Implement resume logic with use case
    }

    fun resetRunState() {
        _uiState.value = RunUiState()
        _activeRun.value = null
        _gpsPoints.value = emptyList()
        _runStatistics.value = null
    }

    private fun updateMetricsFromGpsPoint(latitude: Double, longitude: Double, speed: Float?) {
        val currentState = _uiState.value
        val newGpsPoint = GpsTrackPointEntity(
            sessionId = currentState.currentSessionId ?: "",
            latitude = latitude,
            longitude = longitude,
            speed = speed,
            timestamp = System.currentTimeMillis(),
            sequenceNumber = _gpsPoints.value.size
        )

        // Add to GPS points list
        _gpsPoints.value = _gpsPoints.value + newGpsPoint

        // Calculate updated metrics
        val totalDistance = calculateTotalDistance(_gpsPoints.value)
        val currentPace = speed?.let { if (it > 0) 1000f / (it * 60f) else 0f } ?: 0f
        val elapsedTime = System.currentTimeMillis() - (currentState.startTime ?: System.currentTimeMillis())

        updateUiState {
            copy(
                totalDistance = totalDistance,
                currentPace = currentPace,
                elapsedTime = elapsedTime,
                averagePace = if (elapsedTime > 0) totalDistance / (elapsedTime / 60000f) else 0f,
                currentSpeed = speed ?: 0f
            )
        }
    }

    private fun calculateTotalDistance(points: List<GpsTrackPointEntity>): Float {
        if (points.size < 2) return 0f

        var totalDistance = 0f
        for (i in 1 until points.size) {
            val prev = points[i - 1]
            val current = points[i]
            totalDistance += calculateDistance(
                prev.latitude, prev.longitude,
                current.latitude, current.longitude
            )
        }
        return totalDistance
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val R = 6371000 // Earth's radius in meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
                kotlin.math.cos(Math.toRadians(lat1)) * kotlin.math.cos(Math.toRadians(lat2)) *
                kotlin.math.sin(dLon / 2) * kotlin.math.sin(dLon / 2)
        val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
        return (R * c).toFloat()
    }

    private fun updateUiState(update: RunUiState.() -> RunUiState) {
        _uiState.value = _uiState.value.update()
    }
}

/**
 * UI state for the run screen
 */
data class RunUiState(
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val isRunCompleted: Boolean = false,
    val currentSessionId: String? = null,
    val workoutType: WorkoutType? = null,
    val targetPace: Float? = null,
    val startTime: Long? = null,
    val elapsedTime: Long = 0L,
    val totalDistance: Float = 0f,
    val currentPace: Float = 0f,
    val averagePace: Float = 0f,
    val currentSpeed: Float = 0f,
    val calories: Int = 0,
    val steps: Int? = null,
    val heartRate: Int? = null,
    val averageHeartRate: Int? = null
)