package com.example.quantiq.ui.settings.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quantiq.domain.usecase.DisableAllNotificationsUseCase
import com.example.quantiq.domain.usecase.ObserveAllNotificationConfigsUseCase
import com.example.quantiq.domain.usecase.ObserveCountersUseCase
import com.example.quantiq.domain.usecase.SetNotificationEnabledUseCase

/**
 * Represents NotificationsSettingsViewModelFactory.
 */
class NotificationsSettingsViewModelFactory(
    private val observeCountersUseCase: ObserveCountersUseCase,
    private val observeAllNotificationConfigsUseCase: ObserveAllNotificationConfigsUseCase,
    private val setNotificationEnabledUseCase: SetNotificationEnabledUseCase,
    private val disableAllNotificationsUseCase: DisableAllNotificationsUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationsSettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotificationsSettingsViewModel(
                observeCountersUseCase,
                observeAllNotificationConfigsUseCase,
                setNotificationEnabledUseCase,
                disableAllNotificationsUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
