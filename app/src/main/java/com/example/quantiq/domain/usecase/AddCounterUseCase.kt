package com.example.quantiq.domain.usecase

import com.example.quantiq.domain.repository.CounterRepository

class AddCounterUseCase(
    private val repository: CounterRepository
) {
    suspend operator fun invoke(title: String, step: Int, value: Int = 0) {
        repository.addCounter(title, step, value)
    }
}
