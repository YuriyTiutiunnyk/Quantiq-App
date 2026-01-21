package com.example.quantiq.domain.usecase

import com.example.quantiq.domain.model.ItemNotificationConfig
import com.example.quantiq.domain.model.RepeatType
import com.example.quantiq.domain.model.ScheduleType
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Represents GetUpcomingNotificationsUseCaseTest.
 */
class GetUpcomingNotificationsUseCaseTest {
    @Test
    fun `upcoming notifications are sorted and filtered`() = runBlocking {
        val now = 1_700_000_000_000
        val configSoon = sampleConfig(
            itemId = 2,
            startAt = now + 15 * 60 * 1000,
            scheduleType = ScheduleType.ONE_TIME
        )
        val configLater = sampleConfig(
            itemId = 1,
            startAt = now + 60 * 60 * 1000,
            scheduleType = ScheduleType.ONE_TIME
        )
        val configRepeat = sampleConfig(
            itemId = 3,
            startAt = now - 25 * 60 * 1000,
            scheduleType = ScheduleType.REPEATING,
            repeatType = RepeatType.INTERVAL,
            repeatIntervalMinutes = 30
        )
        val repository = FakeItemNotificationRepository(listOf(configSoon, configLater, configRepeat))
        val useCase = GetUpcomingNotificationsUseCase(repository)

        val upcoming = useCase(limit = 3, fromEpochMillis = now)

        assertEquals(listOf(3L, 2L, 1L), upcoming.map { it.itemId })

        val filtered = useCase(limit = 3, filterItemIds = setOf(2L), fromEpochMillis = now)
        assertEquals(listOf(2L), filtered.map { it.itemId })
    }

    private fun sampleConfig(
        itemId: Long,
        startAt: Long,
        scheduleType: ScheduleType,
        repeatType: RepeatType = RepeatType.NONE,
        repeatIntervalMinutes: Int? = null
    ): ItemNotificationConfig =
        ItemNotificationConfig(
            itemId = itemId,
            enabled = true,
            title = "Title $itemId",
            body = "Body $itemId",
            scheduleType = scheduleType,
            startAtEpochMillis = startAt,
            timeZoneId = "UTC",
            repeatType = repeatType,
            repeatIntervalMinutes = repeatIntervalMinutes,
            endAtEpochMillis = null,
            actions = emptyList()
        )
}
