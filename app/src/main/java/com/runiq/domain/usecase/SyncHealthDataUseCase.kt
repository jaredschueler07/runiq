package com.runiq.domain.usecase

import com.runiq.core.util.Result
import com.runiq.domain.repository.HealthRepository
import com.runiq.domain.repository.RunRepository
import com.runiq.domain.model.SyncStatus
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

/**
 * Use case for syncing run data with Health Connect
 */
class SyncHealthDataUseCase @Inject constructor(
    private val runRepository: RunRepository,
    private val healthRepository: HealthRepository
) : BaseUseCase<String, Unit>(Dispatchers.IO) {

    override suspend fun execute(parameters: String): Result<Unit> {
        val userId = parameters

        // Check if Health Connect is available
        val isAvailableResult = healthRepository.isHealthConnectAvailable()
        if (isAvailableResult is Result.Error) {
            return isAvailableResult.mapData { Unit }
        }

        if (isAvailableResult is Result.Success && !isAvailableResult.data) {
            return Result.Error(IllegalStateException("Health Connect is not available"))
        }

        // Check permissions
        val permissionsResult = healthRepository.checkHealthConnectPermissions()
        if (permissionsResult is Result.Error) {
            return permissionsResult.mapData { Unit }
        }

        // Get unsynced sessions
        val unsyncedResult = runRepository.getUnsyncedSessions()
        if (unsyncedResult is Result.Error) {
            return unsyncedResult.mapData { Unit }
        }

        val unsyncedSessions = (unsyncedResult as Result.Success).data
        var successCount = 0
        var errorCount = 0

        // Sync each session
        for (session in unsyncedSessions) {
            if (session.userId == userId && session.endTime != null) {
                try {
                    // Update sync status to syncing
                    runRepository.updateSyncStatus(session.sessionId, SyncStatus.PENDING)

                    // Get GPS track for the session
                    val gpsTrackResult = runRepository.getGpsTrack(session.sessionId)
                    if (gpsTrackResult is Result.Success) {
                        // Write to Health Connect (this will return error since not implemented)
                        val writeResult = healthRepository.writeRunSessionToHealthConnect(
                            sessionId = session.sessionId,
                            startTime = session.startTime,
                            endTime = session.endTime!!,
                            distance = session.distance,
                            calories = session.calories,
                            heartRateData = emptyList(),
                            gpsTrack = gpsTrackResult.data
                        )

                        if (writeResult is Result.Success) {
                            runRepository.updateSyncStatus(session.sessionId, SyncStatus.SYNCED)
                            successCount++
                        } else {
                            val errorMsg = (writeResult as Result.Error).exception.message
                            runRepository.updateSyncStatus(
                                session.sessionId, 
                                SyncStatus.FAILED, 
                                errorMsg
                            )
                            errorCount++
                        }
                    } else {
                        runRepository.updateSyncStatus(
                            session.sessionId, 
                            SyncStatus.FAILED, 
                            "Failed to get GPS track"
                        )
                        errorCount++
                    }
                } catch (e: Exception) {
                    runRepository.updateSyncStatus(
                        session.sessionId, 
                        SyncStatus.FAILED, 
                        e.message
                    )
                    errorCount++
                }
            }
        }

        return if (errorCount == 0) {
            Result.Success(Unit)
        } else {
            Result.Error(RuntimeException("Sync completed with $successCount successes and $errorCount failures"))
        }
    }
}