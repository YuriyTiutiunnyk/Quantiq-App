package com.example.quantiq.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quantiq.billing.BillingManager
import com.example.quantiq.data.BackupManager
import com.example.quantiq.domain.usecase.AddCounterUseCase
import com.example.quantiq.domain.usecase.DeleteCounterUseCase
import com.example.quantiq.domain.usecase.ObserveCountersUseCase
import com.example.quantiq.domain.usecase.ResetCounterUseCase
import com.example.quantiq.domain.usecase.UpdateCounterDetailsUseCase
import com.example.quantiq.domain.usecase.UpdateCounterValueUseCase

/**
 * Represents MainViewModelFactory.
 */
class MainViewModelFactory(
    private val observeCountersUseCase: ObserveCountersUseCase,
    private val addCounterUseCase: AddCounterUseCase,
    private val updateCounterValueUseCase: UpdateCounterValueUseCase,
    private val updateCounterDetailsUseCase: UpdateCounterDetailsUseCase,
    private val deleteCounterUseCase: DeleteCounterUseCase,
    private val resetCounterUseCase: ResetCounterUseCase,
    private val billingManager: BillingManager,
    private val backupManager: BackupManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(
                observeCountersUseCase,
                addCounterUseCase,
                updateCounterValueUseCase,
                updateCounterDetailsUseCase,
                deleteCounterUseCase,
                resetCounterUseCase,
                billingManager,
                backupManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
