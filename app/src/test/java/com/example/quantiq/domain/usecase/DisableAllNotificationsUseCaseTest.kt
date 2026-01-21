package com.example.quantiq.domain.usecase

import com.example.quantiq.domain.model.ItemNotificationConfig
import com.example.quantiq.domain.model.RepeatType
import com.example.quantiq.domain.model.ScheduleType
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class DisableAllNotificationsUseCaseTest {
    @Test
    fun `disable all cancels enabled configs and updates repository`() = runBlocking {
        val config1 = sampleConfig(itemId = 1, enabled = true)
        val config2 = sampleConfig(itemId = 2, enabled = false)
        val config3 = sampleConfig(itemId = 3, enabled = true)
        val repository = FakeItemNotificationRepository(listOf(config1, config2, config3))
        val scheduler = FakeNotificationScheduler()
        val useCase = DisableAllNotificationsUseCase(repository, scheduler)

        useCase()

        val allConfigs = repository.getAllConfigs()
        assertEquals(3, allConfigs.size)
        assertEquals(listOf(1L, 3L), scheduler.cancelled.sorted())
        assertEquals(false, allConfigs.first { it.itemId == 1L }.enabled)
        assertEquals(false, allConfigs.first { it.itemId == 2L }.enabled)
        assertEquals(false, allConfigs.first { it.itemId == 3L }.enabled)
    }

    private fun sampleConfig(itemId: Long, enabled: Boolean): ItemNotificationConfig =
        ItemNotificationConfig(
            itemId = itemId,
            enabled = enabled,
            title = "Title $itemId",
            body = "Body $itemId",
            scheduleType = ScheduleType.ONE_TIME,
            startAtEpochMillis = 1_700_000_000_000,
            timeZoneId = "UTC",
            repeatType = RepeatType.NONE,
            repeatIntervalMinutes = null,
            endAtEpochMillis = null,
            actions = emptyList()
        )
}
