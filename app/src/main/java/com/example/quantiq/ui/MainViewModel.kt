package com.example.quantiq.ui

import androidx.lifecycle.viewModelScope
import com.example.quantiq.billing.BillingManager
import com.example.quantiq.data.BackupManager
import com.example.quantiq.data.Counter
import com.example.quantiq.data.CounterDao
import com.example.quantiq.mvi.MviViewModel
import com.example.quantiq.mvi.UiEffect
import com.example.quantiq.mvi.UiIntent
import com.example.quantiq.mvi.UiState
import kotlinx.coroutines.launch

data class MainState(
    val counters: List<Counter> = emptyList(),
    val isLoading: Boolean = false,
    val isPro: Boolean = false,
    val error: String? = null
) : UiState

sealed class MainIntent : UiIntent {
    data class LoadCounters(val forceRefresh: Boolean = false) : MainIntent()
    data class AddCounter(val title: String) : MainIntent()
    data class UpdateCounter(val counter: Counter, val delta: Int) : MainIntent() // Unified increment/decrement
    data class DeleteCounter(val id: Long) : MainIntent()
    object PurchasePro : MainIntent()
    data class ExportData(val uri: android.net.Uri) : MainIntent()
    data class ImportData(val uri: android.net.Uri) : MainIntent()
}

sealed class MainEffect : UiEffect {
    data class ShowToast(val message: String) : MainEffect()
    object NavigateToSettings : MainEffect()
    object LaunchBilling : MainEffect()
}

class MainViewModel(
    private val dao: CounterDao,
    private val billingManager: BillingManager,
    private val backupManager: BackupManager
) : MviViewModel<MainState, MainIntent, MainEffect>(MainState()) {

    init {
        viewModelScope.launch {
            dao.getAllCounters().collect { counters ->
                setState { copy(counters = counters) }
            }
        }
        
        viewModelScope.launch {
            billingManager.isPro.collect { isPro ->
                setState { copy(isPro = isPro) }
            }
        }
    }

    override fun dispatch(intent: MainIntent) {
        when (intent) {
            is MainIntent.LoadCounters -> { /* handled by init flow */ }
            
            is MainIntent.AddCounter -> {
                if (!currentState.isPro && currentState.counters.size >= 3) {
                    viewModelScope.launch { setEffect { MainEffect.NavigateToSettings } }
                    return
                }
                viewModelScope.launch {
                    dao.insert(Counter(title = intent.title))
                }
            }
            
            is MainIntent.UpdateCounter -> {
                viewModelScope.launch {
                    val newValue = intent.counter.value + intent.delta
                    dao.update(intent.counter.copy(value = newValue))
                }
            }
            
            is MainIntent.DeleteCounter -> {
                viewModelScope.launch {
                    dao.deleteById(intent.id)
                }
            }
            
            is MainIntent.PurchasePro -> {
                viewModelScope.launch { setEffect { MainEffect.LaunchBilling } }
            }
            
            is MainIntent.ExportData -> {
                viewModelScope.launch {
                    backupManager.exportData(intent.uri, currentState.counters)
                        .onSuccess { setEffect { MainEffect.ShowToast("Export Successful") } }
                        .onFailure { setEffect { MainEffect.ShowToast("Export Failed: ${it.message}") } }
                }
            }
            
            is MainIntent.ImportData -> {
                 viewModelScope.launch {
                    backupManager.importData(intent.uri)
                        .onSuccess { counters ->
                            // Merge strategy: Add as new
                            counters.forEach { dao.insert(it.copy(id = 0)) }
                            setEffect { MainEffect.ShowToast("Imported ${counters.size} counters") }
                        }
                        .onFailure { setEffect { MainEffect.ShowToast("Import Failed: ${it.message}") } }
                }
            }
        }
    }
}
