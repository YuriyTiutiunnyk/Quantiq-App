package com.example.quantiq.domain.usecase

import com.example.quantiq.domain.model.Counter
import com.example.quantiq.domain.repository.CounterRepository
import kotlinx.coroutines.flow.Flow

class ObserveCounterUseCase(
    private val repository: CounterRepository
) {
    operator fun invoke(id: Long): Flow<Counter?> = repository.observeCounter(id)
}
