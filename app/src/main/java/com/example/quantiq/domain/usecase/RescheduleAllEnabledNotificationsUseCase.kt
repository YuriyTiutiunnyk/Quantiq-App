package com.example.quantiq.domain.usecase

import com.example.quantiq.domain.notification.NotificationScheduler

/**
 * Represents RescheduleAllEnabledNotificationsUseCase.
 */
class RescheduleAllEnabledNotificationsUseCase(
    private val scheduler: NotificationScheduler
) {
    suspend operator fun invoke() {
        scheduler.rescheduleAll()
    }
}
