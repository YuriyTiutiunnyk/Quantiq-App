package com.example.quantiq.domain.usecase

import com.example.quantiq.domain.model.Counter
import com.example.quantiq.domain.repository.CounterRepository

/**
 * Represents UpdateCounterValueUseCase.
 */
class UpdateCounterValueUseCase(
    private val repository: CounterRepository
) {
    suspend operator fun invoke(counter: Counter, delta: Int) {
        repository.updateCounterValue(counter, delta)
    }
}
