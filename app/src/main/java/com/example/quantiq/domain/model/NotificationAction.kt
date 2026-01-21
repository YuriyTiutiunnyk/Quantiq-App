package com.example.quantiq.domain.model

/**
 * Represents NotificationAction.
 */
data class NotificationAction(
    val label: String,
    val type: NotificationActionType,
    val payload: String? = null
)

/**
 * Enumerates NotificationActionType values.
 */
enum class NotificationActionType {
    OPEN_ITEM,
    MARK_DONE,
    SNOOZE
}
