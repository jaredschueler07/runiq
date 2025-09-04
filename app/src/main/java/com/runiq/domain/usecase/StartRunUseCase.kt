package com.runiq.domain.usecase

import com.runiq.core.util.Result
import com.runiq.domain.model.WorkoutType
import com.runiq.domain.repository.RunRepository
import com.runiq.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

/**
 * Use case for starting a new run session
 */
class StartRunUseCase @Inject constructor(
    private val runRepository: RunRepository,
    private val userRepository: UserRepository
) : BaseUseCase<StartRunUseCase.Params, String>(Dispatchers.IO) {

    override suspend fun execute(parameters: Params): Result<String> {
        // Check for existing active run
        val activeRunResult = runRepository.getActiveRun(parameters.userId)
        if (activeRunResult is Result.Error) {
            return activeRunResult
        }
        
        if (activeRunResult is Result.Success && activeRunResult.data != null) {
            return Result.Error(IllegalStateException("There is already an active run session"))
        }

        // Get user preferences to determine coach
        val userPrefsResult = userRepository.getUserPreferences()
        if (userPrefsResult is Result.Error) {
            return userPrefsResult.mapData { "" } // Transform error type
        }

        val coachId = if (userPrefsResult is Result.Success) {
            userPrefsResult.data.selectedCoachId ?: "default-coach"
        } else {
            "default-coach"
        }

        // Start the run session
        return runRepository.startRun(
            userId = parameters.userId,
            workoutType = parameters.workoutType,
            coachId = coachId,
            targetPace = parameters.targetPace
        )
    }

    data class Params(
        val userId: String,
        val workoutType: WorkoutType,
        val targetPace: Float? = null
    )
}