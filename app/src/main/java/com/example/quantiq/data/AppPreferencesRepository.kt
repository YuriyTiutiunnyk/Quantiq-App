package com.example.quantiq.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.quantiq.domain.repository.ActiveItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "quantiq_preferences")

/**
 * Stores simple app preferences in DataStore.
 */
class AppPreferencesRepository(
    private val context: Context
) : ActiveItemRepository {
    companion object {
        private val ACTIVE_ITEM_ID = longPreferencesKey("active_item_id")
    }

    override val activeItemId: Flow<Long?> = context.dataStore.data.map { preferences ->
        preferences[ACTIVE_ITEM_ID]
    }

    override suspend fun setActiveItemId(id: Long) {
        context.dataStore.edit { preferences ->
            preferences[ACTIVE_ITEM_ID] = id
        }
    }

    override suspend fun getActiveItemId(): Long? =
        context.dataStore.data.first()[ACTIVE_ITEM_ID]
}
