package com.example.quantiq.domain.usecase

import com.example.quantiq.domain.model.ItemNotificationConfig
import com.example.quantiq.domain.repository.ItemNotificationRepository
import kotlinx.coroutines.flow.Flow

class ObserveAllNotificationConfigsUseCase(
    private val repository: ItemNotificationRepository
) {
    operator fun invoke(): Flow<List<ItemNotificationConfig>> = repository.observeAllConfigs()
}
