package com.example.quantiq.di

import android.content.Context
import androidx.work.WorkManager
import androidx.room.Room
import com.example.quantiq.billing.BillingManager
import com.example.quantiq.data.BackupManager
import com.example.quantiq.data.QuantiqDatabase
import com.example.quantiq.data.repository.ItemNotificationRepositoryImpl
import com.example.quantiq.data.repository.CounterRepositoryImpl
import com.example.quantiq.domain.notification.NotificationScheduler
import com.example.quantiq.domain.repository.CounterRepository
import com.example.quantiq.domain.repository.ItemNotificationRepository
import com.example.quantiq.domain.usecase.AddCounterUseCase
import com.example.quantiq.domain.usecase.DeleteCounterUseCase
import com.example.quantiq.domain.usecase.DisableItemNotificationUseCase
import com.example.quantiq.domain.usecase.GetItemNotificationConfigUseCase
import com.example.quantiq.domain.usecase.ObserveCountersUseCase
import com.example.quantiq.domain.usecase.RescheduleAllEnabledNotificationsUseCase
import com.example.quantiq.domain.usecase.ResetCounterUseCase
import com.example.quantiq.domain.usecase.UpsertItemNotificationConfigUseCase
import com.example.quantiq.domain.usecase.UpdateCounterDetailsUseCase
import com.example.quantiq.domain.usecase.UpdateCounterValueUseCase
import com.example.quantiq.notifications.LocalNotificationScheduler
import kotlinx.coroutines.CoroutineScope

class AppContainer(
    private val appContext: Context,
    private val applicationScope: CoroutineScope
) {
    private val database: QuantiqDatabase by lazy {
        Room.databaseBuilder(
            appContext,
            QuantiqDatabase::class.java,
            "quantiq.db"
        ).addMigrations(QuantiqDatabase.MIGRATION_1_2).build()
    }

    private val repository: CounterRepository by lazy {
        CounterRepositoryImpl(database.counterDao())
    }

    private val itemNotificationRepository: ItemNotificationRepository by lazy {
        ItemNotificationRepositoryImpl(database.itemNotificationConfigDao())
    }

    private val workManager: WorkManager by lazy {
        WorkManager.getInstance(appContext)
    }

    val notificationScheduler: NotificationScheduler by lazy {
        LocalNotificationScheduler(workManager, itemNotificationRepository)
    }

    val observeCountersUseCase: ObserveCountersUseCase by lazy { ObserveCountersUseCase(repository) }
    val addCounterUseCase: AddCounterUseCase by lazy { AddCounterUseCase(repository) }
    val updateCounterValueUseCase: UpdateCounterValueUseCase by lazy { UpdateCounterValueUseCase(repository) }
    val updateCounterDetailsUseCase: UpdateCounterDetailsUseCase by lazy { UpdateCounterDetailsUseCase(repository) }
    val deleteCounterUseCase: DeleteCounterUseCase by lazy { DeleteCounterUseCase(repository) }
    val resetCounterUseCase: ResetCounterUseCase by lazy { ResetCounterUseCase(repository) }

    val getItemNotificationConfigUseCase: GetItemNotificationConfigUseCase by lazy {
        GetItemNotificationConfigUseCase(itemNotificationRepository)
    }
    val upsertItemNotificationConfigUseCase: UpsertItemNotificationConfigUseCase by lazy {
        UpsertItemNotificationConfigUseCase(itemNotificationRepository, notificationScheduler)
    }
    val disableItemNotificationUseCase: DisableItemNotificationUseCase by lazy {
        DisableItemNotificationUseCase(itemNotificationRepository, notificationScheduler)
    }
    val rescheduleAllEnabledNotificationsUseCase: RescheduleAllEnabledNotificationsUseCase by lazy {
        RescheduleAllEnabledNotificationsUseCase(notificationScheduler)
    }

    val billingManager: BillingManager by lazy { BillingManager(appContext, applicationScope) }
    val backupManager: BackupManager by lazy { BackupManager(appContext) }
}
