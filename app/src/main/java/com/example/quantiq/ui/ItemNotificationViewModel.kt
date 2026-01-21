package com.example.quantiq.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quantiq.domain.model.ItemNotificationConfig
import com.example.quantiq.domain.usecase.DisableItemNotificationUseCase
import com.example.quantiq.domain.usecase.GetItemNotificationConfigUseCase
import com.example.quantiq.domain.usecase.UpsertItemNotificationConfigUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Represents ItemNotificationViewModel.
 */
class ItemNotificationViewModel(
    private val getItemNotificationConfigUseCase: GetItemNotificationConfigUseCase,
    private val upsertItemNotificationConfigUseCase: UpsertItemNotificationConfigUseCase,
    private val disableItemNotificationUseCase: DisableItemNotificationUseCase
) : ViewModel() {
    private val itemId = MutableStateFlow<Long?>(null)

    val config: StateFlow<ItemNotificationConfig?> = itemId
        .flatMapLatest { id ->
            if (id == null) {
                MutableStateFlow(null)
            } else {
                getItemNotificationConfigUseCase(id)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun setItemId(id: Long) {
        if (itemId.value != id) {
            itemId.value = id
        }
    }

    fun save(config: ItemNotificationConfig) {
        viewModelScope.launch {
            upsertItemNotificationConfigUseCase(config)
        }
    }

    fun disable(itemId: Long) {
        viewModelScope.launch {
            disableItemNotificationUseCase(itemId)
        }
    }
}
