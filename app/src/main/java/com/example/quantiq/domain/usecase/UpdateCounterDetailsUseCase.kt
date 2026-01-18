package com.example.quantiq.domain.usecase

import com.example.quantiq.domain.model.Counter
import com.example.quantiq.domain.repository.CounterRepository

class UpdateCounterDetailsUseCase(
    private val repository: CounterRepository
) {
    suspend operator fun invoke(counter: Counter, title: String, step: Int) {
        repository.updateCounter(counter.copy(title = title, step = step))
    }
}
