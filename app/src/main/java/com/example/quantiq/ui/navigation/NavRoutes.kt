package com.example.quantiq.ui.navigation

object NavArguments {
    const val COUNTER_ID = "counterId"
    const val GUIDELINE_ID = "guidelineId"
    const val NOTIFICATION_ITEM_ID = "notificationItemId"
}

object NavRoutes {
    private const val ROUTE_SEPARATOR = "/"
    private const val DETAILS_BASE = "details"
    private const val GUIDELINES_BASE = "guidelines"
    private const val GUIDELINE_DETAIL_BASE = "guideline"
    private const val NOTIFICATIONS_BASE = "notifications"
    private const val NOTIFICATION_DETAILS_BASE = "notification_details"
    private const val UPCOMING_SCHEDULE_BASE = "upcoming_schedule"

    const val LIST = "list"
    const val SETTINGS = "settings"
    const val COUNTER_DETAILS = "$DETAILS_BASE/{${NavArguments.COUNTER_ID}}"
    const val GUIDELINES = GUIDELINES_BASE
    const val GUIDELINE_DETAILS = "$GUIDELINE_DETAIL_BASE/{${NavArguments.GUIDELINE_ID}}"
    const val NOTIFICATIONS_SETTINGS = NOTIFICATIONS_BASE
    const val NOTIFICATION_DETAILS = "$NOTIFICATION_DETAILS_BASE/{${NavArguments.NOTIFICATION_ITEM_ID}}"
    const val UPCOMING_SCHEDULE = UPCOMING_SCHEDULE_BASE

    fun counterDetails(counterId: Long): String = DETAILS_BASE + ROUTE_SEPARATOR + counterId

    fun guidelineDetails(guidelineId: Int): String =
        GUIDELINE_DETAIL_BASE + ROUTE_SEPARATOR + guidelineId

    fun notificationDetails(itemId: Long): String =
        NOTIFICATION_DETAILS_BASE + ROUTE_SEPARATOR + itemId
}
