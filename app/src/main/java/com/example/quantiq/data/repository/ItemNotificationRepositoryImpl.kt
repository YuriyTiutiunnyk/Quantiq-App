package com.example.quantiq.data.repository

import com.example.quantiq.data.notification.ItemNotificationConfigDao
import com.example.quantiq.data.notification.toDomain
import com.example.quantiq.data.notification.toEntity
import com.example.quantiq.domain.model.ItemNotificationConfig
import com.example.quantiq.domain.repository.ItemNotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ItemNotificationRepositoryImpl(
    private val dao: ItemNotificationConfigDao
) : ItemNotificationRepository {
    override fun observeConfig(itemId: Long): Flow<ItemNotificationConfig?> =
        dao.observeByItemId(itemId).map { entity -> entity?.toDomain() }

    override suspend fun getConfig(itemId: Long): ItemNotificationConfig? =
        dao.getByItemId(itemId)?.toDomain()

    override suspend fun upsertConfig(config: ItemNotificationConfig) {
        dao.upsert(config.toEntity())
    }

    override suspend fun disableConfig(itemId: Long) {
        dao.disable(itemId)
    }

    override suspend fun deleteConfig(itemId: Long) {
        dao.delete(itemId)
    }

    override suspend fun getEnabledConfigs(): List<ItemNotificationConfig> =
        dao.getEnabledConfigs().map { it.toDomain() }
}
