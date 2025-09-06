package com.runiq.domain.usecase

import com.runiq.core.util.Result
import com.runiq.domain.repository.RunRepository
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

/**
 * Use case for saving GPS track points during an active run
 */
class SaveGpsTrackPointUseCase @Inject constructor(
    private val runRepository: RunRepository
) : BaseUseCase<SaveGpsTrackPointUseCase.Params, Unit>(Dispatchers.IO) {

    override suspend fun execute(parameters: Params): Result<Unit> {
        // Validate accuracy threshold
        if (parameters.accuracy != null && parameters.accuracy > MAX_ACCURACY_THRESHOLD) {
            return Result.Error(IllegalArgumentException("GPS accuracy too low: ${parameters.accuracy}m"))
        }

        // Save the GPS point
        return runRepository.saveGpsTrackPoint(
            sessionId = parameters.sessionId,
            latitude = parameters.latitude,
            longitude = parameters.longitude,
            altitude = parameters.altitude,
            speed = parameters.speed,
            accuracy = parameters.accuracy,
            timestamp = parameters.timestamp
        )
    }

    data class Params(
        val sessionId: String,
        val latitude: Double,
        val longitude: Double,
        val altitude: Double? = null,
        val speed: Float? = null,
        val accuracy: Float? = null,
        val timestamp: Long = System.currentTimeMillis()
    )

    companion object {
        private const val MAX_ACCURACY_THRESHOLD = 50f // meters
    }
}