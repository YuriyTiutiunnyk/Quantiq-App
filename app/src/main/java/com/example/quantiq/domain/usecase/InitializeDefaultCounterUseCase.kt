package com.example.quantiq.domain.usecase

import com.example.quantiq.domain.repository.ActiveItemRepository
import com.example.quantiq.domain.repository.CounterRepository

/**
 * Ensures a default counter exists and sets it as the active item when needed.
 */
class InitializeDefaultCounterUseCase(
    private val counterRepository: CounterRepository,
    private val activeItemRepository: ActiveItemRepository
) {
    suspend operator fun invoke(defaultTitle: String) {
        val defaultCounter = counterRepository.getDefaultCounter()
        val activeItemId = activeItemRepository.getActiveItemId()

        if (defaultCounter == null) {
            val defaultId = counterRepository.addCounter(
                title = defaultTitle,
                step = 1,
                value = 0,
                isDefault = true
            )
            activeItemRepository.setActiveItemId(defaultId)
            return
        }

        val activeCounter = activeItemId?.let { counterRepository.getCounter(it) }
        if (activeCounter == null) {
            activeItemRepository.setActiveItemId(defaultCounter.id)
        }
    }
}
