package com.example.quantiq.domain.notification

import com.example.quantiq.domain.model.ItemNotificationConfig

/**
 * Defines the NotificationScheduler contract.
 */
interface NotificationScheduler {
    suspend fun schedule(config: ItemNotificationConfig, overrideStartAtMillis: Long? = null)
    suspend fun cancel(itemId: Long)
    suspend fun rescheduleAll()
    suspend fun scheduleSnooze(itemId: Long, minutes: Int)
}
