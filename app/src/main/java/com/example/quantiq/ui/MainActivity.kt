package com.example.quantiq.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import com.example.quantiq.QuantiqApplication
import com.example.quantiq.ui.settings.notifications.NotificationDetailsViewModelFactory
import com.example.quantiq.ui.settings.notifications.NotificationsSettingsViewModel
import com.example.quantiq.ui.settings.notifications.NotificationsSettingsViewModelFactory
import com.example.quantiq.ui.settings.notifications.UpcomingScheduleViewModel
import com.example.quantiq.ui.settings.notifications.UpcomingScheduleViewModelFactory
import com.example.quantiq.ui.theme.QuantiqTheme
import com.example.quantiq.notifications.NotificationConstants

/**
 * Hosts the Compose entry point, wiring the app container dependencies into [AppRoot].
 */
/**
 * Represents MainActivity.
 */
class MainActivity : ComponentActivity() {
    private val pendingNotificationItemId = mutableStateOf<Long?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer = (application as QuantiqApplication).appContainer
        appContainer.billingManager.startConnection()
        pendingNotificationItemId.value = extractNotificationItemId(intent)
        val mainViewModelFactory = MainViewModelFactory(
            appContainer.observeCountersUseCase,
            appContainer.addCounterUseCase,
            appContainer.updateCounterValueUseCase,
            appContainer.updateCounterDetailsUseCase,
            appContainer.deleteCounterUseCase,
            appContainer.resetCounterUseCase,
            appContainer.billingManager,
            appContainer.backupManager
        )
        val notificationViewModelFactory = ItemNotificationViewModelFactory(
            appContainer.getItemNotificationConfigUseCase,
            appContainer.upsertItemNotificationConfigUseCase,
            appContainer.disableItemNotificationUseCase
        )
        val notificationsSettingsViewModelFactory = NotificationsSettingsViewModelFactory(
            appContainer.observeCountersUseCase,
            appContainer.observeAllNotificationConfigsUseCase,
            appContainer.setNotificationEnabledUseCase,
            appContainer.disableAllNotificationsUseCase
        )
        val notificationDetailsViewModelFactory = NotificationDetailsViewModelFactory(
            appContainer.observeCounterUseCase
        )
        val upcomingScheduleViewModelFactory = UpcomingScheduleViewModelFactory(
            appContainer.observeCountersUseCase,
            appContainer.getUpcomingNotificationsUseCase
        )

        setContent {
            QuantiqTheme {
                AppRoot(
                    mainViewModelFactory = mainViewModelFactory,
                    notificationViewModelFactory = notificationViewModelFactory,
                    notificationsSettingsViewModelFactory = notificationsSettingsViewModelFactory,
                    notificationDetailsViewModelFactory = notificationDetailsViewModelFactory,
                    upcomingScheduleViewModelFactory = upcomingScheduleViewModelFactory,
                    initialNotificationItemId = pendingNotificationItemId.value,
                    onNotificationItemConsumed = { pendingNotificationItemId.value = null }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        extractNotificationItemId(intent)?.let { pendingNotificationItemId.value = it }
    }

    private fun extractNotificationItemId(intent: Intent?): Long? {
        val id = intent?.getLongExtra(NotificationConstants.EXTRA_ITEM_ID, -1L) ?: -1L
        return id.takeIf { it > 0L }
    }
}
