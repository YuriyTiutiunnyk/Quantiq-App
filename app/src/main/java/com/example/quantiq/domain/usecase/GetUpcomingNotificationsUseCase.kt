package com.example.quantiq.domain.usecase

import com.example.quantiq.domain.model.UpcomingNotification
import com.example.quantiq.domain.notification.NotificationScheduleCalculator
import com.example.quantiq.domain.repository.ItemNotificationRepository

/**
 * Represents GetUpcomingNotificationsUseCase.
 */
class GetUpcomingNotificationsUseCase(
    private val repository: ItemNotificationRepository,
    private val scheduleCalculator: NotificationScheduleCalculator = NotificationScheduleCalculator()
) {
    suspend operator fun invoke(
        limit: Int,
        filterItemIds: Set<Long>? = null,
        fromEpochMillis: Long = System.currentTimeMillis()
    ): List<UpcomingNotification> {
        if (limit <= 0) return emptyList()
        val enabledConfigs = repository.getEnabledConfigs()
            .asSequence()
            .filter { config -> filterItemIds?.contains(config.itemId) ?: true }
            .toList()

        val occurrences = enabledConfigs.flatMap { config ->
            val times = scheduleCalculator.upcomingOccurrences(config, fromEpochMillis, limit)
            times.map { time ->
                UpcomingNotification(
                    itemId = config.itemId,
                    scheduledAtEpochMillis = time,
                    title = config.title,
                    body = config.body
                )
            }
        }

        return occurrences
            .sortedBy { it.scheduledAtEpochMillis }
            .take(limit)
    }
}
