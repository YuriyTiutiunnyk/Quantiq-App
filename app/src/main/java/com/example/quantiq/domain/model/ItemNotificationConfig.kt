package com.example.quantiq.domain.model

/**
 * Represents ItemNotificationConfig.
 */
data class ItemNotificationConfig(
    val itemId: Long,
    val enabled: Boolean,
    val title: String,
    val body: String,
    val scheduleType: ScheduleType,
    val startAtEpochMillis: Long,
    val timeZoneId: String,
    val repeatType: RepeatType,
    val repeatIntervalMinutes: Int?,
    val endAtEpochMillis: Long?,
    val actions: List<NotificationAction>
)

/**
 * Enumerates ScheduleType values.
 */
enum class ScheduleType {
    ONE_TIME,
    REPEATING
}

/**
 * Enumerates RepeatType values.
 */
enum class RepeatType {
    NONE,
    DAILY,
    WEEKLY,
    MONTHLY,
    INTERVAL
}
