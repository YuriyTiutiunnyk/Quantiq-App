package com.example.quantiq.mvi

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

interface UiState
interface UiIntent
interface UiEffect

abstract class MviViewModel<S : UiState, I : UiIntent, E : UiEffect>(initialState: S) : androidx.lifecycle.ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _effect = Channel<E>()
    val effect = _effect.receiveAsFlow()

    protected val currentState: S
        get() = _state.value

    abstract fun dispatch(intent: I)

    protected fun setState(reduce: S.() -> S) {
        _state.value = currentState.reduce()
    }

    protected suspend fun setEffect(builder: () -> E) {
        _effect.send(builder())
    }
}
