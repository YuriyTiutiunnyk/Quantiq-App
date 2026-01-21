package com.example.quantiq.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.quantiq.QuantiqApplication
import com.example.quantiq.domain.model.NotificationActionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Represents NotificationActionReceiver.
 */
class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val itemId = intent.getLongExtra(NotificationConstants.EXTRA_ITEM_ID, -1L)
        val actionType = intent.getStringExtra(NotificationConstants.EXTRA_ACTION_TYPE)
            ?.let { runCatching { NotificationActionType.valueOf(it) }.getOrNull() }
            ?: return
        if (itemId <= 0L) return

        val pendingResult = goAsync()
        val appContainer = (context.applicationContext as QuantiqApplication).appContainer
        CoroutineScope(Dispatchers.IO).launch {
            when (actionType) {
                NotificationActionType.MARK_DONE -> appContainer.resetCounterUseCase(itemId)
                NotificationActionType.SNOOZE -> {
                    val minutes = intent.getStringExtra(NotificationConstants.EXTRA_ACTION_PAYLOAD)
                        ?.toIntOrNull()
                        ?: 10
                    appContainer.notificationScheduler.scheduleSnooze(itemId, minutes)
                }
                NotificationActionType.OPEN_ITEM -> Unit
            }
            pendingResult.finish()
        }
    }
}
