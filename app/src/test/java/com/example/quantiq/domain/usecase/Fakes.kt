package com.example.quantiq.domain.usecase

import com.example.quantiq.domain.model.ItemNotificationConfig
import com.example.quantiq.domain.notification.NotificationScheduler
import com.example.quantiq.domain.repository.ItemNotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeItemNotificationRepository(
    initialConfigs: List<ItemNotificationConfig> = emptyList()
) : ItemNotificationRepository {
    private val configs = MutableStateFlow(initialConfigs.associateBy { it.itemId })

    override fun observeConfig(itemId: Long): Flow<ItemNotificationConfig?> =
        MutableStateFlow(configs.value[itemId])

    override fun observeAllConfigs(): Flow<List<ItemNotificationConfig>> =
        MutableStateFlow(configs.value.values.toList())

    override suspend fun getConfig(itemId: Long): ItemNotificationConfig? =
        configs.value[itemId]

    override suspend fun getAllConfigs(): List<ItemNotificationConfig> =
        configs.value.values.toList()

    override suspend fun upsertConfig(config: ItemNotificationConfig) {
        configs.value = configs.value.toMutableMap().apply { put(config.itemId, config) }
    }

    override suspend fun disableConfig(itemId: Long) {
        configs.value[itemId]?.let { existing ->
            upsertConfig(existing.copy(enabled = false))
        }
    }

    override suspend fun disableAllConfigs() {
        configs.value = configs.value.mapValues { it.value.copy(enabled = false) }
    }

    override suspend fun deleteConfig(itemId: Long) {
        configs.value = configs.value.toMutableMap().apply { remove(itemId) }
    }

    override suspend fun getEnabledConfigs(): List<ItemNotificationConfig> =
        configs.value.values.filter { it.enabled }
}

class FakeNotificationScheduler : NotificationScheduler {
    val scheduled = mutableListOf<ItemNotificationConfig>()
    val cancelled = mutableListOf<Long>()

    override suspend fun schedule(config: ItemNotificationConfig, overrideStartAtMillis: Long?) {
        scheduled.add(config)
    }

    override suspend fun cancel(itemId: Long) {
        cancelled.add(itemId)
    }

    override suspend fun rescheduleAll() = Unit

    override suspend fun scheduleSnooze(itemId: Long, minutes: Int) = Unit
}
