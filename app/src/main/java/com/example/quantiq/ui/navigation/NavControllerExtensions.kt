package com.example.quantiq.ui.navigation

import androidx.navigation.NavController

fun NavController.navigateToRootTab(route: String) {
    navigate(route) {
        popUpTo(graph.startDestinationId) {
            inclusive = true
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
