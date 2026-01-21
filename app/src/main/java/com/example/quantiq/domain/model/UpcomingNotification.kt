package com.example.quantiq.domain.model

data class UpcomingNotification(
    val itemId: Long,
    val scheduledAtEpochMillis: Long,
    val title: String,
    val body: String
)
