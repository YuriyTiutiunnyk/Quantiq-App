package com.example.quantiq.data.notification

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemNotificationConfigDao {
    @Query("SELECT * FROM item_notification_configs WHERE itemId = :itemId LIMIT 1")
    fun observeByItemId(itemId: Long): Flow<ItemNotificationConfigEntity?>

    @Query("SELECT * FROM item_notification_configs WHERE itemId = :itemId LIMIT 1")
    suspend fun getByItemId(itemId: Long): ItemNotificationConfigEntity?

    @Query("SELECT * FROM item_notification_configs WHERE enabled = 1")
    suspend fun getEnabledConfigs(): List<ItemNotificationConfigEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: ItemNotificationConfigEntity)

    @Query("UPDATE item_notification_configs SET enabled = 0 WHERE itemId = :itemId")
    suspend fun disable(itemId: Long)

    @Query("DELETE FROM item_notification_configs WHERE itemId = :itemId")
    suspend fun delete(itemId: Long)
}
