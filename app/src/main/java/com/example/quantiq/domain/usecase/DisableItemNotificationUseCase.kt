package com.example.quantiq.domain.usecase

import com.example.quantiq.domain.notification.NotificationScheduler
import com.example.quantiq.domain.repository.ItemNotificationRepository

class DisableItemNotificationUseCase(
    private val repository: ItemNotificationRepository,
    private val scheduler: NotificationScheduler
) {
    suspend operator fun invoke(itemId: Long) {
        repository.disableConfig(itemId)
        scheduler.cancel(itemId)
    }
}
