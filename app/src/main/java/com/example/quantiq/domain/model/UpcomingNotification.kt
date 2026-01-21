package com.example.quantiq.domain.model

/**
 * Represents UpcomingNotification.
 */
data class UpcomingNotification(
    val itemId: Long,
    val scheduledAtEpochMillis: Long,
    val title: String,
    val body: String
)
