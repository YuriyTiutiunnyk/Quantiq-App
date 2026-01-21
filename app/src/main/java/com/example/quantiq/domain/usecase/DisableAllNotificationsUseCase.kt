package com.example.quantiq.domain.usecase

import com.example.quantiq.domain.notification.NotificationScheduler
import com.example.quantiq.domain.repository.ItemNotificationRepository

/**
 * Represents DisableAllNotificationsUseCase.
 */
class DisableAllNotificationsUseCase(
    private val repository: ItemNotificationRepository,
    private val scheduler: NotificationScheduler
) {
    suspend operator fun invoke() {
        val enabledConfigs = repository.getEnabledConfigs()
        repository.disableAllConfigs()
        enabledConfigs.forEach { config ->
            scheduler.cancel(config.itemId)
        }
    }
}
