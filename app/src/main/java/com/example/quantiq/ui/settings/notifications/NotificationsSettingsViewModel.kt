package com.example.quantiq.ui.settings.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quantiq.domain.notification.NotificationScheduleCalculator
import com.example.quantiq.domain.usecase.DisableAllNotificationsUseCase
import com.example.quantiq.domain.usecase.ObserveAllNotificationConfigsUseCase
import com.example.quantiq.domain.usecase.ObserveCountersUseCase
import com.example.quantiq.domain.usecase.SetNotificationEnabledUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NotificationSettingsItem(
    val itemId: Long,
    val itemTitle: String,
    val notificationTitle: String,
    val notificationBody: String,
    val nextTriggerMillis: Long?,
    val enabled: Boolean
)

data class NotificationsSettingsState(
    val items: List<NotificationSettingsItem> = emptyList(),
    val allEnabled: Boolean = false
)

class NotificationsSettingsViewModel(
    observeCountersUseCase: ObserveCountersUseCase,
    observeAllNotificationConfigsUseCase: ObserveAllNotificationConfigsUseCase,
    private val setNotificationEnabledUseCase: SetNotificationEnabledUseCase,
    private val disableAllNotificationsUseCase: DisableAllNotificationsUseCase,
    private val scheduleCalculator: NotificationScheduleCalculator = NotificationScheduleCalculator()
) : ViewModel() {
    private val _state = MutableStateFlow(NotificationsSettingsState())
    val state: StateFlow<NotificationsSettingsState> = _state

    private val configsFlow = observeAllNotificationConfigsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    private val countersFlow = observeCountersUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch {
            combine(countersFlow, configsFlow) { counters, configs ->
                val configMap = configs.associateBy { it.itemId }
                val now = System.currentTimeMillis()
                val items = counters.map { counter ->
                    val config = configMap[counter.id]
                    val enabled = config?.enabled ?: false
                    val next = if (enabled && config != null) {
                        scheduleCalculator.nextTriggerMillis(config, now)
                    } else {
                        null
                    }
                    NotificationSettingsItem(
                        itemId = counter.id,
                        itemTitle = counter.title.ifBlank { "Item #${counter.id}" },
                        notificationTitle = config?.title?.ifBlank { counter.title } ?: counter.title,
                        notificationBody = config?.body.orEmpty(),
                        nextTriggerMillis = next,
                        enabled = enabled
                    )
                }.sortedWith(
                    compareBy<NotificationSettingsItem> { it.nextTriggerMillis ?: Long.MAX_VALUE }
                        .thenBy { it.itemTitle.lowercase() }
                )
                val allEnabled = items.isNotEmpty() && items.all { it.enabled }
                NotificationsSettingsState(items = items, allEnabled = allEnabled)
            }.collect { newState ->
                _state.value = newState
            }
        }
    }

    fun toggleItem(itemId: Long, itemTitle: String, enabled: Boolean) {
        viewModelScope.launch {
            setNotificationEnabledUseCase(itemId, itemTitle, enabled)
        }
    }

    fun setAllEnabled(enabled: Boolean) {
        if (!enabled) {
            disableAll()
            return
        }
        val currentItems = _state.value.items
        viewModelScope.launch {
            currentItems.forEach { item ->
                if (!item.enabled) {
                    setNotificationEnabledUseCase(item.itemId, item.itemTitle, true)
                }
            }
        }
    }

    fun disableAll() {
        viewModelScope.launch {
            disableAllNotificationsUseCase()
        }
    }
}
