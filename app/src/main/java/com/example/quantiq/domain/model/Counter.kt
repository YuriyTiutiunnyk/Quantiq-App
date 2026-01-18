package com.example.quantiq.domain.model

data class Counter(
    val id: Long = 0,
    val title: String,
    val value: Int = 0,
    val step: Int = 1,
    val isLocked: Boolean = false
)
