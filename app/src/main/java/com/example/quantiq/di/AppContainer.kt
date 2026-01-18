package com.example.quantiq.di

import android.content.Context
import androidx.room.Room
import com.example.quantiq.billing.BillingManager
import com.example.quantiq.data.BackupManager
import com.example.quantiq.data.QuantiqDatabase
import com.example.quantiq.data.repository.CounterRepositoryImpl
import com.example.quantiq.domain.repository.CounterRepository
import com.example.quantiq.domain.usecase.AddCounterUseCase
import com.example.quantiq.domain.usecase.DeleteCounterUseCase
import com.example.quantiq.domain.usecase.ObserveCountersUseCase
import com.example.quantiq.domain.usecase.ResetCounterUseCase
import com.example.quantiq.domain.usecase.UpdateCounterDetailsUseCase
import com.example.quantiq.domain.usecase.UpdateCounterValueUseCase
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
        ).build()
    }

    private val repository: CounterRepository by lazy {
        CounterRepositoryImpl(database.counterDao())
    }

    val observeCountersUseCase: ObserveCountersUseCase by lazy { ObserveCountersUseCase(repository) }
    val addCounterUseCase: AddCounterUseCase by lazy { AddCounterUseCase(repository) }
    val updateCounterValueUseCase: UpdateCounterValueUseCase by lazy { UpdateCounterValueUseCase(repository) }
    val updateCounterDetailsUseCase: UpdateCounterDetailsUseCase by lazy { UpdateCounterDetailsUseCase(repository) }
    val deleteCounterUseCase: DeleteCounterUseCase by lazy { DeleteCounterUseCase(repository) }
    val resetCounterUseCase: ResetCounterUseCase by lazy { ResetCounterUseCase(repository) }

    val billingManager: BillingManager by lazy { BillingManager(appContext, applicationScope) }
    val backupManager: BackupManager by lazy { BackupManager(appContext) }
}
