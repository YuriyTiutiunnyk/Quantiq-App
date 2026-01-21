package com.example.quantiq.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quantiq.domain.usecase.DisableItemNotificationUseCase
import com.example.quantiq.domain.usecase.GetItemNotificationConfigUseCase
import com.example.quantiq.domain.usecase.UpsertItemNotificationConfigUseCase

/**
 * Represents ItemNotificationViewModelFactory.
 */
class ItemNotificationViewModelFactory(
    private val getItemNotificationConfigUseCase: GetItemNotificationConfigUseCase,
    private val upsertItemNotificationConfigUseCase: UpsertItemNotificationConfigUseCase,
    private val disableItemNotificationUseCase: DisableItemNotificationUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemNotificationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ItemNotificationViewModel(
                getItemNotificationConfigUseCase,
                upsertItemNotificationConfigUseCase,
                disableItemNotificationUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
