package com.example.quantiq.domain.usecase

import com.example.quantiq.domain.repository.CounterRepository

/**
 * Represents DeleteCounterUseCase.
 */
class DeleteCounterUseCase(
    private val repository: CounterRepository
) {
    suspend operator fun invoke(counterId: Long) {
        val counter = repository.getCounter(counterId) ?: return
        val counterCount = repository.getCounterCount()
        if (counter.isDefault || counterCount <= 1) {
            return
        }
        repository.deleteCounter(counterId)
    }
}
