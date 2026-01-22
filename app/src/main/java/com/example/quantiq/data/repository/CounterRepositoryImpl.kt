package com.example.quantiq.data.repository

import com.example.quantiq.data.CounterDao
import com.example.quantiq.data.CounterEntity
import com.example.quantiq.data.mapper.toDomain
import com.example.quantiq.data.mapper.toEntity
import com.example.quantiq.domain.model.Counter
import com.example.quantiq.domain.repository.CounterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Represents CounterRepositoryImpl.
 */
class CounterRepositoryImpl(
    private val counterDao: CounterDao
) : CounterRepository {
    override fun observeCounters(): Flow<List<Counter>> =
        counterDao.getAllCounters().map { counters -> counters.map { it.toDomain() } }

    override fun observeCounter(id: Long): Flow<Counter?> =
        counterDao.observeCounterById(id).map { counter -> counter?.toDomain() }

    override suspend fun addCounter(
        title: String,
        step: Int,
        value: Int,
        isDefault: Boolean
    ): Long =
        counterDao.insert(
            CounterEntity(
                title = title,
                value = value,
                step = step,
                isDefault = isDefault
            )
        )
    }

    override suspend fun updateCounter(counter: Counter) {
        counterDao.update(counter.toEntity())
    }

    override suspend fun updateCounterValue(counter: Counter, delta: Int) {
        val newValue = counter.value + delta
        counterDao.update(counter.copy(value = newValue).toEntity())
    }

    override suspend fun deleteCounter(id: Long) {
        counterDao.deleteById(id)
    }

    override suspend fun resetCounter(id: Long) {
        counterDao.resetCounter(id)
    }

    override suspend fun getCounter(id: Long): Counter? =
        counterDao.getCounterById(id)?.toDomain()

    override suspend fun getDefaultCounter(): Counter? =
        counterDao.getDefaultCounter()?.toDomain()

    override suspend fun getCounterCount(): Int = counterDao.getCounterCount()
}
