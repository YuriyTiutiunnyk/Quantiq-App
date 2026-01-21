package com.example.quantiq.domain.repository

import com.example.quantiq.domain.model.Counter
import kotlinx.coroutines.flow.Flow

/**
 * Defines the CounterRepository contract.
 */
interface CounterRepository {
    fun observeCounters(): Flow<List<Counter>>
    fun observeCounter(id: Long): Flow<Counter?>
    suspend fun addCounter(title: String, step: Int, value: Int = 0)
    suspend fun updateCounter(counter: Counter)
    suspend fun updateCounterValue(counter: Counter, delta: Int)
    suspend fun deleteCounter(id: Long)
    suspend fun resetCounter(id: Long)
}
