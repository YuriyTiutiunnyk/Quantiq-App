package com.example.quantiq.data.notification

import com.example.quantiq.domain.model.ItemNotificationConfig
import com.example.quantiq.domain.model.RepeatType
import com.example.quantiq.domain.model.ScheduleType

fun ItemNotificationConfigEntity.toDomain(): ItemNotificationConfig =
    ItemNotificationConfig(
        itemId = itemId,
        enabled = enabled,
        title = title,
        body = body,
        scheduleType = scheduleType.toScheduleType(),
        startAtEpochMillis = startAtEpochMillis,
        timeZoneId = timeZoneId,
        repeatType = repeatType.toRepeatType(),
        repeatIntervalMinutes = repeatIntervalMinutes,
        endAtEpochMillis = endAtEpochMillis,
        actions = NotificationJsonAdapter.decodeActions(actionsJson)
    )

fun ItemNotificationConfig.toEntity(): ItemNotificationConfigEntity =
    ItemNotificationConfigEntity(
        itemId = itemId,
        enabled = enabled,
        title = title,
        body = body,
        scheduleType = scheduleType.name,
        startAtEpochMillis = startAtEpochMillis,
        timeZoneId = timeZoneId,
        repeatType = repeatType.name,
        repeatIntervalMinutes = repeatIntervalMinutes,
        endAtEpochMillis = endAtEpochMillis,
        actionsJson = NotificationJsonAdapter.encodeActions(actions)
    )

private fun String.toScheduleType(): ScheduleType =
    runCatching { ScheduleType.valueOf(this) }.getOrDefault(ScheduleType.ONE_TIME)

private fun String.toRepeatType(): RepeatType =
    runCatching { RepeatType.valueOf(this) }.getOrDefault(RepeatType.NONE)
