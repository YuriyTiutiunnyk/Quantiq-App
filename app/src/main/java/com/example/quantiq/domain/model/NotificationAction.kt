package com.example.quantiq.domain.model

data class NotificationAction(
    val label: String,
    val type: NotificationActionType,
    val payload: String? = null
)

enum class NotificationActionType {
    OPEN_ITEM,
    MARK_DONE,
    SNOOZE
}
