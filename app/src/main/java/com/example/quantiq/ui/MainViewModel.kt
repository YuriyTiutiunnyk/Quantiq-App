package com.example.quantiq.ui

import androidx.lifecycle.viewModelScope
import com.example.quantiq.billing.BillingManager
import com.example.quantiq.data.BackupManager
import com.example.quantiq.domain.model.Counter
import com.example.quantiq.domain.usecase.AddCounterUseCase
import com.example.quantiq.domain.usecase.DeleteCounterUseCase
import com.example.quantiq.domain.usecase.InitializeDefaultCounterUseCase
import com.example.quantiq.domain.usecase.ObserveCountersUseCase
import com.example.quantiq.domain.usecase.ObserveActiveItemIdUseCase
import com.example.quantiq.domain.usecase.ResetCounterUseCase
import com.example.quantiq.domain.usecase.SetActiveItemIdUseCase
import com.example.quantiq.domain.usecase.UpdateCounterDetailsUseCase
import com.example.quantiq.domain.usecase.UpdateCounterValueUseCase
import com.example.quantiq.mvi.MviViewModel
import com.example.quantiq.mvi.UiEffect
import com.example.quantiq.mvi.UiIntent
import com.example.quantiq.mvi.UiState
import kotlinx.coroutines.launch

/**
 * Represents MainState.
 */
data class MainState(
    val counters: List<Counter> = emptyList(),
    val activeItemId: Long? = null,
    val isLoading: Boolean = false,
    val isPro: Boolean = false,
    val error: String? = null
) : UiState

/**
 * Represents MainIntent.
 */
sealed class MainIntent : UiIntent {
    /**
     * Represents LoadCounters.
     */
    data class LoadCounters(val forceRefresh: Boolean = false) : MainIntent()

    /**
     * Represents AddCounter.
     */
    data class AddCounter(val title: String, val step: Int = 1) : MainIntent()

    /**
     * Represents UpdateCounterValue.
     */
    data class UpdateCounterValue(val counter: Counter, val delta: Int) : MainIntent()

    /**
     * Represents UpdateCounterDetails.
     */
    data class UpdateCounterDetails(val counter: Counter, val title: String, val step: Int) : MainIntent()

    /**
     * Represents DeleteCounter.
     */
    data class DeleteCounter(val id: Long) : MainIntent()

    /**
     * Represents ResetCounter.
     */
    data class ResetCounter(val id: Long) : MainIntent()

    /**
     * Represents InitializeDefaultCounter.
     */
    data class InitializeDefaultCounter(val defaultTitle: String) : MainIntent()

    /**
     * Represents SetActiveCounter.
     */
    data class SetActiveCounter(val id: Long) : MainIntent()

    /**
     * Singleton PurchasePro definition.
     */
    object PurchasePro : MainIntent()

    /**
     * Represents ExportData.
     */
    data class ExportData(val uri: android.net.Uri) : MainIntent()

    /**
     * Represents ImportData.
     */
    data class ImportData(val uri: android.net.Uri) : MainIntent()
}

/**
 * Represents MainEffect.
 */
sealed class MainEffect : UiEffect {
    /**
     * Represents ShowToast.
     */
    data class ShowToast(val message: String) : MainEffect()

    /**
     * Singleton NavigateToSettings definition.
     */
    object NavigateToSettings : MainEffect()

    /**
     * Singleton LaunchBilling definition.
     */
    object LaunchBilling : MainEffect()
}

/**
 * Represents MainViewModel.
 */
class MainViewModel(
    private val observeCountersUseCase: ObserveCountersUseCase,
    private val addCounterUseCase: AddCounterUseCase,
    private val updateCounterValueUseCase: UpdateCounterValueUseCase,
    private val updateCounterDetailsUseCase: UpdateCounterDetailsUseCase,
    private val deleteCounterUseCase: DeleteCounterUseCase,
    private val resetCounterUseCase: ResetCounterUseCase,
    private val observeActiveItemIdUseCase: ObserveActiveItemIdUseCase,
    private val setActiveItemIdUseCase: SetActiveItemIdUseCase,
    private val initializeDefaultCounterUseCase: InitializeDefaultCounterUseCase,
    private val billingManager: BillingManager,
    private val backupManager: BackupManager
) : MviViewModel<MainState, MainIntent, MainEffect>(MainState()) {

    init {
        viewModelScope.launch {
            observeCountersUseCase().collect { counters ->
                setState { copy(counters = counters) }
            }
        }

        viewModelScope.launch {
            observeActiveItemIdUseCase().collect { activeItemId ->
                setState { copy(activeItemId = activeItemId) }
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
                    addCounterUseCase(intent.title, intent.step)
                }
            }
            
            is MainIntent.UpdateCounterValue -> {
                viewModelScope.launch {
                    updateCounterValueUseCase(intent.counter, intent.delta)
                }
            }
            
            is MainIntent.UpdateCounterDetails -> {
                viewModelScope.launch {
                    updateCounterDetailsUseCase(intent.counter, intent.title, intent.step)
                }
            }
            
            is MainIntent.DeleteCounter -> {
                viewModelScope.launch {
                    deleteCounterUseCase(intent.id)
                    if (intent.id == currentState.activeItemId) {
                        val defaultCounter = currentState.counters.firstOrNull { it.isDefault }
                        defaultCounter?.let { setActiveItemIdUseCase(it.id) }
                    }
                }
            }
            
            is MainIntent.ResetCounter -> {
                viewModelScope.launch {
                    resetCounterUseCase(intent.id)
                }
            }

            is MainIntent.InitializeDefaultCounter -> {
                viewModelScope.launch {
                    initializeDefaultCounterUseCase(intent.defaultTitle)
                }
            }

            is MainIntent.SetActiveCounter -> {
                viewModelScope.launch {
                    setActiveItemIdUseCase(intent.id)
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
                            counters.forEach { counter ->
                                addCounterUseCase(counter.title, counter.step, counter.value)
                            }
                            setEffect { MainEffect.ShowToast("Imported ${counters.size} counters") }
                        }
                        .onFailure { setEffect { MainEffect.ShowToast("Import Failed: ${it.message}") } }
                }
            }
        }
    }
}
