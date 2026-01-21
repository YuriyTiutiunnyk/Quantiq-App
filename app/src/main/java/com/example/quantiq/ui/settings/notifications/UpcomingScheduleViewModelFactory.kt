package com.example.quantiq.ui.settings.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quantiq.domain.usecase.GetUpcomingNotificationsUseCase
import com.example.quantiq.domain.usecase.ObserveCountersUseCase

/**
 * Represents UpcomingScheduleViewModelFactory.
 */
class UpcomingScheduleViewModelFactory(
    private val observeCountersUseCase: ObserveCountersUseCase,
    private val getUpcomingNotificationsUseCase: GetUpcomingNotificationsUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UpcomingScheduleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UpcomingScheduleViewModel(
                observeCountersUseCase,
                getUpcomingNotificationsUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
