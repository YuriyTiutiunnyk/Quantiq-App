package com.example.quantiq

import android.app.Application
import com.example.quantiq.di.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class QuantiqApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this, applicationScope)
    }
}
