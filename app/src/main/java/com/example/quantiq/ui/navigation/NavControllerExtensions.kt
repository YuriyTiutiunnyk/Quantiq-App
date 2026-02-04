package com.example.quantiq.ui.navigation

import androidx.navigation.NavController

fun NavController.navigateToRootTab(route: String) {
    navigate(route) {
        popUpTo(graph.id) {
            inclusive = true
        }
        launchSingleTop = true
    }
}
