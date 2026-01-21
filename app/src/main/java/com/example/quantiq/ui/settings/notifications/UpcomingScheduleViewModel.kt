package com.example.quantiq.ui.settings.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quantiq.domain.model.Counter
import com.example.quantiq.domain.usecase.GetUpcomingNotificationsUseCase
import com.example.quantiq.domain.usecase.ObserveCountersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Enumerates UpcomingFilterMode values.
 */
enum class UpcomingFilterMode {
    ALL,
    SELECTED
}

/**
 * Represents UpcomingNotificationUi.
 */
data class UpcomingNotificationUi(
    val itemId: Long,
    val itemTitle: String,
    val notificationTitle: String,
    val notificationBody: String,
    val scheduledAtEpochMillis: Long
)

/**
 * Represents UpcomingScheduleState.
 */
data class UpcomingScheduleState(
    val counters: List<Counter> = emptyList(),
    val filterMode: UpcomingFilterMode = UpcomingFilterMode.ALL,
    val selectedItemIds: Set<Long> = emptySet(),
    val upcoming: List<UpcomingNotificationUi> = emptyList()
)

/**
 * Represents UpcomingScheduleViewModel.
 */
class UpcomingScheduleViewModel(
    observeCountersUseCase: ObserveCountersUseCase,
    private val getUpcomingNotificationsUseCase: GetUpcomingNotificationsUseCase
) : ViewModel() {
    private val countersFlow = observeCountersUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    private val filterMode = MutableStateFlow(UpcomingFilterMode.ALL)
    private val selectedItemIds = MutableStateFlow<Set<Long>>(emptySet())
    private val _state = MutableStateFlow(UpcomingScheduleState())
    val state: StateFlow<UpcomingScheduleState> = _state

    init {
        viewModelScope.launch {
            countersFlow.collect { counters ->
                _state.update { it.copy(counters = counters) }
            }
        }
        viewModelScope.launch {
            combine(filterMode, selectedItemIds, countersFlow) { mode, selected, counters ->
                Triple(mode, selected, counters)
            }.collect { (mode, selected, counters) ->
                val filter = if (mode == UpcomingFilterMode.SELECTED) selected else null
                val upcoming = getUpcomingNotificationsUseCase(
                    limit = 50,
                    filterItemIds = filter
                )
                val counterMap = counters.associateBy { it.id }
                val upcomingUi = upcoming.map { occurrence ->
                    val title = counterMap[occurrence.itemId]?.title
                        ?.ifBlank { "Item #${occurrence.itemId}" }
                        ?: "Item #${occurrence.itemId}"
                    UpcomingNotificationUi(
                        itemId = occurrence.itemId,
                        itemTitle = title,
                        notificationTitle = occurrence.title,
                        notificationBody = occurrence.body,
                        scheduledAtEpochMillis = occurrence.scheduledAtEpochMillis
                    )
                }
                _state.update {
                    it.copy(
                        filterMode = mode,
                        selectedItemIds = selected,
                        upcoming = upcomingUi
                    )
                }
            }
        }
    }

    fun setFilterMode(mode: UpcomingFilterMode) {
        filterMode.value = mode
    }

    fun toggleItemSelection(itemId: Long) {
        selectedItemIds.update { current ->
            if (current.contains(itemId)) current - itemId else current + itemId
        }
    }
}
