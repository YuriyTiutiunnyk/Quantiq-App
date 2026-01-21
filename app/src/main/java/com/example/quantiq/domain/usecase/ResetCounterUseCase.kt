package com.example.quantiq.domain.usecase

import com.example.quantiq.domain.repository.CounterRepository

/**
 * Represents ResetCounterUseCase.
 */
class ResetCounterUseCase(
    private val repository: CounterRepository
) {
    suspend operator fun invoke(counterId: Long) {
        repository.resetCounter(counterId)
    }
}
