package com.example.quantiq.notifications

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.quantiq.domain.model.ItemNotificationConfig
import com.example.quantiq.domain.notification.NotificationScheduler
import com.example.quantiq.domain.notification.NotificationScheduleCalculator
import com.example.quantiq.domain.repository.ItemNotificationRepository
import java.util.concurrent.TimeUnit
import kotlin.math.max

/**
 * Represents LocalNotificationScheduler.
 */
class LocalNotificationScheduler(
    private val workManager: WorkManager,
    private val repository: ItemNotificationRepository
) : NotificationScheduler {
    private val scheduleCalculator = NotificationScheduleCalculator()

    override suspend fun schedule(config: ItemNotificationConfig, overrideStartAtMillis: Long?) {
        if (!config.enabled) {
            cancel(config.itemId)
            return
        }
        val now = System.currentTimeMillis()
        val triggerAt = overrideStartAtMillis ?: scheduleCalculator.nextTriggerMillis(config, now) ?: return
        val delay = max(0L, triggerAt - now)
        val request = OneTimeWorkRequestBuilder<ItemNotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    NotificationConstants.EXTRA_ITEM_ID to config.itemId
                )
            )
            .build()
        workManager.enqueueUniqueWork(
            uniqueWorkName(config.itemId),
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    override suspend fun cancel(itemId: Long) {
        workManager.cancelUniqueWork(uniqueWorkName(itemId))
    }

    override suspend fun rescheduleAll() {
        repository.getEnabledConfigs().forEach { config ->
            schedule(config)
        }
    }

    override suspend fun scheduleSnooze(itemId: Long, minutes: Int) {
        val config = repository.getConfig(itemId) ?: return
        val snoozeMillis = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(minutes.toLong())
        schedule(config.copy(enabled = true), overrideStartAtMillis = snoozeMillis)
    }

    private fun uniqueWorkName(itemId: Long): String =
        NotificationConstants.UNIQUE_WORK_PREFIX + itemId

    
}
