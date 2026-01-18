package com.example.quantiq.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quantiq.data.CounterDao

class CounterViewModelFactory(private val dao: CounterDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CounterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CounterViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
