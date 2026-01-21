package com.example.quantiq.domain.usecase

import com.example.quantiq.domain.model.ItemNotificationConfig
import com.example.quantiq.domain.notification.NotificationScheduler
import com.example.quantiq.domain.repository.ItemNotificationRepository

/**
 * Represents UpsertItemNotificationConfigUseCase.
 */
class UpsertItemNotificationConfigUseCase(
    private val repository: ItemNotificationRepository,
    private val scheduler: NotificationScheduler
) {
    suspend operator fun invoke(config: ItemNotificationConfig) {
        repository.upsertConfig(config)
        if (config.enabled) {
            scheduler.schedule(config)
        } else {
            scheduler.cancel(config.itemId)
        }
    }
}
