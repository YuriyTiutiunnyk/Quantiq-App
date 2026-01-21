package com.example.quantiq.ui.settings.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quantiq.domain.model.Counter
import com.example.quantiq.domain.usecase.ObserveCounterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

/**
 * Represents NotificationDetailsViewModel.
 */
class NotificationDetailsViewModel(
    private val observeCounterUseCase: ObserveCounterUseCase
) : ViewModel() {
    private val counterId = MutableStateFlow<Long?>(null)

    val counter: StateFlow<Counter?> = counterId
        .flatMapLatest { id ->
            if (id == null) {
                MutableStateFlow(null)
            } else {
                observeCounterUseCase(id)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun setCounterId(id: Long) {
        if (counterId.value != id) {
            counterId.value = id
        }
    }
}
