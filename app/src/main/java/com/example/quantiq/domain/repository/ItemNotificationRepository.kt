package com.example.quantiq.domain.repository

import com.example.quantiq.domain.model.ItemNotificationConfig
import kotlinx.coroutines.flow.Flow

/**
 * Defines the ItemNotificationRepository contract.
 */
interface ItemNotificationRepository {
    fun observeConfig(itemId: Long): Flow<ItemNotificationConfig?>
    fun observeAllConfigs(): Flow<List<ItemNotificationConfig>>
    suspend fun getConfig(itemId: Long): ItemNotificationConfig?
    suspend fun getAllConfigs(): List<ItemNotificationConfig>
    suspend fun upsertConfig(config: ItemNotificationConfig)
    suspend fun disableConfig(itemId: Long)
    suspend fun disableAllConfigs()
    suspend fun deleteConfig(itemId: Long)
    suspend fun getEnabledConfigs(): List<ItemNotificationConfig>
}
