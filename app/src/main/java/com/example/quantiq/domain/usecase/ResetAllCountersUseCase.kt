package com.example.quantiq.domain.usecase

import com.example.quantiq.domain.repository.CounterRepository

/**
 * Resets all counters to zero.
 */
class ResetAllCountersUseCase(
    private val repository: CounterRepository
) {
    suspend operator fun invoke() {
        repository.resetAllCounters()
    }
}
