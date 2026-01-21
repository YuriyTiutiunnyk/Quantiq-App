package com.example.quantiq

import android.app.Application
import com.example.quantiq.di.AppContainer
import com.example.quantiq.notifications.NotificationChannels
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Represents QuantiqApplication.
 */
class QuantiqApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this, applicationScope)
        NotificationChannels.create(this)
        applicationScope.launch {
            appContainer.rescheduleAllEnabledNotificationsUseCase()
        }
    }
}
