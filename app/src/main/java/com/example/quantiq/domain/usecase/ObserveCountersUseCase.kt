package com.example.quantiq.domain.usecase

import com.example.quantiq.domain.model.Counter
import com.example.quantiq.domain.repository.CounterRepository
import kotlinx.coroutines.flow.Flow

class ObserveCountersUseCase(
    private val repository: CounterRepository
) {
    operator fun invoke(): Flow<List<Counter>> = repository.observeCounters()
}
