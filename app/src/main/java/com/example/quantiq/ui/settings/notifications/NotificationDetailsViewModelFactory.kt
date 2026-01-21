package com.example.quantiq.ui.settings.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quantiq.domain.usecase.ObserveCounterUseCase

/**
 * Represents NotificationDetailsViewModelFactory.
 */
class NotificationDetailsViewModelFactory(
    private val observeCounterUseCase: ObserveCounterUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotificationDetailsViewModel(observeCounterUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
