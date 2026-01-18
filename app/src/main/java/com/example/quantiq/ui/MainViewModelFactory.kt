package com.example.quantiq.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quantiq.billing.BillingManager
import com.example.quantiq.data.BackupManager
import com.example.quantiq.data.CounterDao

class MainViewModelFactory(
    private val dao: CounterDao,
    private val billingManager: BillingManager,
    private val backupManager: BackupManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(dao, billingManager, backupManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
