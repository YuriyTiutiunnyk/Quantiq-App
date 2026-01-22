package com.example.quantiq.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Defines the ActiveItemRepository contract.
 */
interface ActiveItemRepository {
    val activeItemId: Flow<Long?>
    suspend fun setActiveItemId(id: Long)
    suspend fun getActiveItemId(): Long?
}
