package com.runiq.domain.usecase

import com.runiq.domain.model.RunSession
import com.runiq.domain.model.WorkoutType
import com.runiq.domain.repository.RunRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use case for starting a new run session.
 * Encapsulates the business logic for run initialization.
 */
class StartRunUseCase @Inject constructor(
    private val runRepository: RunRepository
) {
    
    /**
     * Starts a new run session with validation.
     */
    suspend operator fun invoke(
        userId: String,
        workoutType: WorkoutType,
        coachId: String
    ): Result<RunSession> {
        // Validate inputs
        if (userId.isBlank()) {
            return Result.failure(IllegalArgumentException("User ID cannot be blank"))
        }
        
        if (coachId.isBlank()) {
            return Result.failure(IllegalArgumentException("Coach ID cannot be blank"))
        }
        
        // Check if user already has an active run
        val activeRun = runRepository.observeActiveRun(userId)
            .first()
        
        if (activeRun != null) {
            return Result.failure(IllegalStateException("User already has an active run session"))
        }
        
        // Start the run
        return runRepository.startRun(userId, workoutType, coachId)
    }
}