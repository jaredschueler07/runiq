package com.runiq.domain.usecase

import com.runiq.core.util.Result
import com.runiq.data.repository.RunStatistics
import com.runiq.domain.repository.RunRepository
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

/**
 * Use case for ending an active run session
 */
class EndRunUseCase @Inject constructor(
    private val runRepository: RunRepository
) : BaseUseCase<EndRunUseCase.Params, RunStatistics>(Dispatchers.IO) {

    override suspend fun execute(parameters: Params): Result<RunStatistics> {
        // Get the run session to verify it exists
        val sessionResult = runRepository.getRunById(parameters.sessionId)
        if (sessionResult is Result.Error) {
            return sessionResult.mapData { RunStatistics("", 0f, 0L, 0f, 0f, null, 0f, 0, null) }
        }
        
        val session = (sessionResult as Result.Success).data
            ?: return Result.Error(IllegalArgumentException("Run session not found"))
        
        if (session.endTime != null) {
            return Result.Error(IllegalStateException("Run session is already ended"))
        }

        // End the run with provided metrics
        val endResult = runRepository.endRun(
            sessionId = parameters.sessionId,
            distance = parameters.distance,
            duration = parameters.duration,
            averagePace = parameters.averagePace,
            calories = parameters.calories,
            steps = parameters.steps
        )
        
        if (endResult is Result.Error) {
            return endResult.mapData { RunStatistics("", 0f, 0L, 0f, 0f, null, 0f, 0, null) }
        }

        // Calculate and return final statistics
        return runRepository.calculateRunStatistics(parameters.sessionId)
    }

    data class Params(
        val sessionId: String,
        val distance: Float,
        val duration: Long,
        val averagePace: Float,
        val calories: Int,
        val steps: Int?
    )
}