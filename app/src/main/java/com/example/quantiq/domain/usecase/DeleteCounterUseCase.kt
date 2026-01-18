package com.example.quantiq.domain.usecase

import com.example.quantiq.domain.repository.CounterRepository

class DeleteCounterUseCase(
    private val repository: CounterRepository
) {
    suspend operator fun invoke(counterId: Long) {
        repository.deleteCounter(counterId)
    }
}
