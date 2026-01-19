package com.example.quantiq.ui.navigation

object NavArguments {
    const val COUNTER_ID = "counterId"
    const val GUIDELINE_ID = "guidelineId"
}

object NavRoutes {
    private const val ROUTE_SEPARATOR = "/"
    private const val DETAILS_BASE = "details"
    private const val GUIDELINES_BASE = "guidelines"
    private const val GUIDELINE_DETAIL_BASE = "guideline"

    const val LIST = "list"
    const val SETTINGS = "settings"
    const val COUNTER_DETAILS = "$DETAILS_BASE/{${NavArguments.COUNTER_ID}}"
    const val GUIDELINES = GUIDELINES_BASE
    const val GUIDELINE_DETAILS = "$GUIDELINE_DETAIL_BASE/{${NavArguments.GUIDELINE_ID}}"

    fun counterDetails(counterId: Long): String = DETAILS_BASE + ROUTE_SEPARATOR + counterId

    fun guidelineDetails(guidelineId: Int): String =
        GUIDELINE_DETAIL_BASE + ROUTE_SEPARATOR + guidelineId
}
