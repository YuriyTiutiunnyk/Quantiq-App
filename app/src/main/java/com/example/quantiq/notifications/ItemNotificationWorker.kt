package com.example.quantiq.notifications

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.quantiq.R
import com.example.quantiq.QuantiqApplication
import com.example.quantiq.domain.model.NotificationActionType
import kotlinx.coroutines.flow.firstOrNull

/**
 * Represents ItemNotificationWorker.
 */
class ItemNotificationWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val itemId = inputData.getLong(NotificationConstants.EXTRA_ITEM_ID, -1L)
        if (itemId <= 0L) return Result.success()

        val container = (applicationContext as QuantiqApplication).appContainer
        val config = container.getItemNotificationConfigUseCase(itemId).firstOrNull()
        if (config == null || !config.enabled) return Result.success()

        val canNotify = if (android.os.Build.VERSION.SDK_INT >= 33) {
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        if (canNotify) {
            val notification = NotificationCompat.Builder(applicationContext, NotificationConstants.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(config.title)
                .setContentText(config.body)
                .setContentIntent(createContentIntent(itemId))
                .setAutoCancel(true)
                .apply {
                    config.actions.take(3).forEachIndexed { index, action ->
                        val pendingIntent = when (action.type) {
                            NotificationActionType.OPEN_ITEM -> createContentIntent(itemId)
                            NotificationActionType.MARK_DONE,
                            NotificationActionType.SNOOZE -> createActionIntent(itemId, action.type, action.payload, index)
                        }
                        addAction(R.drawable.ic_launcher_foreground, action.label, pendingIntent)
                    }
                }
                .build()

            with(NotificationManagerCompat.from(applicationContext)) {
                notify(itemId.hashCode(), notification)
            }
        }

        container.upsertItemNotificationConfigUseCase(config)
        return Result.success()
    }

    private fun createContentIntent(itemId: Long): PendingIntent {
        val intent = Intent(applicationContext, com.example.quantiq.ui.MainActivity::class.java).apply {
            putExtra(NotificationConstants.EXTRA_ITEM_ID, itemId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            applicationContext,
            itemId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createActionIntent(
        itemId: Long,
        type: NotificationActionType,
        payload: String?,
        requestCode: Int
    ): PendingIntent {
        val intent = Intent(applicationContext, NotificationActionReceiver::class.java).apply {
            putExtra(NotificationConstants.EXTRA_ITEM_ID, itemId)
            putExtra(NotificationConstants.EXTRA_ACTION_TYPE, type.name)
            putExtra(NotificationConstants.EXTRA_ACTION_PAYLOAD, payload)
        }
        return PendingIntent.getBroadcast(
            applicationContext,
            itemId.toInt() + requestCode + 1000,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
