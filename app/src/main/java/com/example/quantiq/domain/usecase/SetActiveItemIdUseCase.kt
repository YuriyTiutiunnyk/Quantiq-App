package com.example.quantiq.domain.usecase

import com.example.quantiq.domain.repository.ActiveItemRepository

/**
 * Represents SetActiveItemIdUseCase.
 */
class SetActiveItemIdUseCase(
    private val repository: ActiveItemRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.setActiveItemId(id)
    }
}
