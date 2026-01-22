package com.example.quantiq.domain.usecase

import com.example.quantiq.domain.repository.ActiveItemRepository
import kotlinx.coroutines.flow.Flow

/**
 * Represents ObserveActiveItemIdUseCase.
 */
class ObserveActiveItemIdUseCase(
    private val repository: ActiveItemRepository
) {
    operator fun invoke(): Flow<Long?> = repository.activeItemId
}
