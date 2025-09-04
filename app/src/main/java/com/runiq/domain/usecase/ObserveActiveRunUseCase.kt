package com.runiq.domain.usecase

import com.runiq.data.local.entities.RunSessionEntity
import com.runiq.domain.repository.RunRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for observing the currently active run session
 */
class ObserveActiveRunUseCase @Inject constructor(
    private val runRepository: RunRepository
) : FlowUseCase<String, RunSessionEntity?>(Dispatchers.IO) {

    override fun execute(parameters: String): Flow<com.runiq.core.util.Result<RunSessionEntity?>> {
        return runRepository.observeActiveRun(parameters)
    }
}