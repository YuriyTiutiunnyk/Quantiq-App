package com.example.quantiq.data.notification

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "item_notification_configs")
data class ItemNotificationConfigEntity(
    @PrimaryKey val itemId: Long,
    val enabled: Boolean,
    val title: String,
    val body: String,
    val scheduleType: String,
    val startAtEpochMillis: Long,
    val timeZoneId: String,
    val repeatType: String,
    val repeatIntervalMinutes: Int?,
    val endAtEpochMillis: Long?,
    val actionsJson: String
)
