package com.example.quantiq.domain.usecase

import com.example.quantiq.domain.repository.CounterRepository

/**
 * Represents AddCounterUseCase.
 */
class AddCounterUseCase(
    private val repository: CounterRepository
) {
    suspend operator fun invoke(
        title: String,
        step: Int,
        value: Int = 0,
        isDefault: Boolean = false
    ): Long = repository.addCounter(title, step, value, isDefault)
}
