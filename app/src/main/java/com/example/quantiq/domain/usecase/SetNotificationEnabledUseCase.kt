package com.example.quantiq.domain.usecase

import com.example.quantiq.domain.model.ItemNotificationConfig
import com.example.quantiq.domain.model.RepeatType
import com.example.quantiq.domain.model.ScheduleType
import com.example.quantiq.domain.notification.NotificationScheduler
import com.example.quantiq.domain.repository.ItemNotificationRepository
import java.time.ZoneId
import java.util.concurrent.TimeUnit

class SetNotificationEnabledUseCase(
    private val repository: ItemNotificationRepository,
    private val scheduler: NotificationScheduler
) {
    suspend operator fun invoke(itemId: Long, itemTitle: String, enabled: Boolean) {
        val existing = repository.getConfig(itemId)
        val config = existing ?: defaultConfig(itemId, itemTitle)
        val updated = config.copy(enabled = enabled, title = config.title.ifBlank { itemTitle })
        repository.upsertConfig(updated)
        if (enabled) {
            scheduler.schedule(updated)
        } else {
            scheduler.cancel(itemId)
        }
    }

    private fun defaultConfig(itemId: Long, itemTitle: String): ItemNotificationConfig {
        val now = System.currentTimeMillis()
        return ItemNotificationConfig(
            itemId = itemId,
            enabled = false,
            title = itemTitle,
            body = "",
            scheduleType = ScheduleType.ONE_TIME,
            startAtEpochMillis = now + TimeUnit.HOURS.toMillis(1),
            timeZoneId = ZoneId.systemDefault().id,
            repeatType = RepeatType.NONE,
            repeatIntervalMinutes = null,
            endAtEpochMillis = null,
            actions = emptyList()
        )
    }
}
