package com.example.quantiq.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quantiq.data.Counter
import com.example.quantiq.data.CounterDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CounterViewModel(private val dao: CounterDao) : ViewModel() {
    
    // In a real app, use Dependency Injection (Hilt/Koin)
    
    val counters: StateFlow<List<Counter>> = dao.getAllCounters()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isPro = MutableStateFlow(false)
    val isPro: StateFlow<Boolean> = _isPro

    fun togglePro() {
        _isPro.value = !_isPro.value
    }

    fun addCounter(title: String, step: Int = 1) {
        // Free limit logic
        if (!_isPro.value && counters.value.size >= 3) return

        viewModelScope.launch {
            dao.insert(Counter(title = title, step = step))
        }
    }

    fun increment(counter: Counter) {
        viewModelScope.launch {
            dao.update(counter.copy(value = counter.value + counter.step))
        }
    }

    fun decrement(counter: Counter) {
        viewModelScope.launch {
            dao.update(counter.copy(value = counter.value - counter.step))
        }
    }
    
    fun reset(counter: Counter) {
        viewModelScope.launch {
            dao.resetCounter(counter.id)
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch {
            dao.deleteById(id)
        }
    }
    
    fun updateDetails(counter: Counter, newTitle: String, newStep: Int) {
        viewModelScope.launch {
            dao.update(counter.copy(title = newTitle, step = newStep))
        }
    }
}
