package com.example.quantiq.domain.usecase

import com.example.quantiq.domain.model.ItemNotificationConfig
import com.example.quantiq.domain.repository.ItemNotificationRepository
import kotlinx.coroutines.flow.Flow

class GetItemNotificationConfigUseCase(
    private val repository: ItemNotificationRepository
) {
    operator fun invoke(itemId: Long): Flow<ItemNotificationConfig?> =
        repository.observeConfig(itemId)
}
