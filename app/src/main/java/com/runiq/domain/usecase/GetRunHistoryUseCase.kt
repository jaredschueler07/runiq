package com.runiq.domain.usecase

import com.runiq.core.util.Result
import com.runiq.data.local.entities.RunSessionEntity
import com.runiq.domain.repository.RunRepository
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

/**
 * Use case for getting paginated run history
 */
class GetRunHistoryUseCase @Inject constructor(
    private val runRepository: RunRepository
) : BaseUseCase<GetRunHistoryUseCase.Params, List<RunSessionEntity>>(Dispatchers.IO) {

    override suspend fun execute(parameters: Params): Result<List<RunSessionEntity>> {
        return runRepository.getRunHistory(
            userId = parameters.userId,
            limit = parameters.limit,
            offset = parameters.offset
        )
    }

    data class Params(
        val userId: String,
        val limit: Int = DEFAULT_PAGE_SIZE,
        val offset: Int = 0
    )

    companion object {
        private const val DEFAULT_PAGE_SIZE = 20
    }
}