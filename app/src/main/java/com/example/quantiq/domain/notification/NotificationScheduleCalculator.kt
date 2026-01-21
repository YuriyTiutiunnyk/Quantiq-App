package com.example.quantiq.domain.notification

import com.example.quantiq.domain.model.ItemNotificationConfig
import com.example.quantiq.domain.model.RepeatType
import com.example.quantiq.domain.model.ScheduleType
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

class NotificationScheduleCalculator {
    fun nextTriggerMillis(
        config: ItemNotificationConfig,
        fromEpochMillis: Long
    ): Long? {
        val zoneId = runCatching { ZoneId.of(config.timeZoneId) }.getOrDefault(ZoneId.systemDefault())
        val start = Instant.ofEpochMilli(config.startAtEpochMillis).atZone(zoneId)
        val from = Instant.ofEpochMilli(fromEpochMillis).atZone(zoneId)
        val endAt = config.endAtEpochMillis?.let { Instant.ofEpochMilli(it).atZone(zoneId) }

        val next = when (config.scheduleType) {
            ScheduleType.ONE_TIME -> start.takeIf { it.toInstant().toEpochMilli() > fromEpochMillis }
            ScheduleType.REPEATING -> when (config.repeatType) {
                RepeatType.NONE -> start.takeIf { it.toInstant().toEpochMilli() > fromEpochMillis }
                RepeatType.DAILY -> nextByIncrement(start, from) { it.plusDays(1) }
                RepeatType.WEEKLY -> nextByIncrement(start, from) { it.plusWeeks(1) }
                RepeatType.MONTHLY -> nextByIncrement(start, from) { it.plusMonths(1) }
                RepeatType.INTERVAL -> nextByInterval(start, fromEpochMillis, config.repeatIntervalMinutes)
            }
        }

        if (next == null) return null
        if (endAt != null && next.isAfter(endAt)) return null
        return next.toInstant().toEpochMilli()
    }

    fun upcomingOccurrences(
        config: ItemNotificationConfig,
        fromEpochMillis: Long,
        limit: Int
    ): List<Long> {
        val occurrences = mutableListOf<Long>()
        var cursor = fromEpochMillis
        repeat(limit) {
            val next = nextTriggerMillis(config, cursor) ?: return occurrences
            occurrences.add(next)
            cursor = next + 1
            if (config.scheduleType == ScheduleType.ONE_TIME) return occurrences
        }
        return occurrences
    }

    private fun nextByIncrement(
        start: ZonedDateTime,
        from: ZonedDateTime,
        step: (ZonedDateTime) -> ZonedDateTime
    ): ZonedDateTime {
        var candidate = start
        while (!candidate.isAfter(from)) {
            candidate = step(candidate)
        }
        return candidate
    }

    private fun nextByInterval(
        start: ZonedDateTime,
        fromEpochMillis: Long,
        intervalMinutes: Int?
    ): ZonedDateTime? {
        val interval = intervalMinutes?.takeIf { it > 0 } ?: return null
        val startMillis = start.toInstant().toEpochMilli()
        if (fromEpochMillis <= startMillis) return start
        val intervalMillis = TimeUnit.MINUTES.toMillis(interval.toLong())
        val elapsed = fromEpochMillis - startMillis
        val steps = (elapsed / intervalMillis) + 1
        val nextMillis = startMillis + (steps * intervalMillis)
        return Instant.ofEpochMilli(nextMillis).atZone(start.zone)
    }
}
