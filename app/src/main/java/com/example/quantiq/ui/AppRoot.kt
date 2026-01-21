package com.example.quantiq.ui

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.quantiq.ui.navigation.NavArguments
import com.example.quantiq.ui.navigation.NavRoutes
import com.example.quantiq.ui.screens.DetailScreen
import com.example.quantiq.ui.screens.GuidelineDetailScreen
import com.example.quantiq.ui.screens.GuidelinesScreen
import com.example.quantiq.ui.screens.ListScreen
import com.example.quantiq.ui.screens.SettingsScreen
import com.example.quantiq.ui.settings.notifications.NotificationDetailsScreen
import com.example.quantiq.ui.settings.notifications.NotificationDetailsViewModel
import com.example.quantiq.ui.settings.notifications.NotificationDetailsViewModelFactory
import com.example.quantiq.ui.settings.notifications.NotificationsSettingsScreen
import com.example.quantiq.ui.settings.notifications.NotificationsSettingsViewModel
import com.example.quantiq.ui.settings.notifications.NotificationsSettingsViewModelFactory
import com.example.quantiq.ui.settings.notifications.UpcomingScheduleScreen
import com.example.quantiq.ui.settings.notifications.UpcomingScheduleViewModel
import com.example.quantiq.ui.settings.notifications.UpcomingScheduleViewModelFactory

/**
 * The root composable that wires navigation, ViewModels, and layout direction for the app.
 */
@Composable
fun AppRoot(
    mainViewModelFactory: MainViewModelFactory,
    notificationViewModelFactory: ItemNotificationViewModelFactory,
    notificationsSettingsViewModelFactory: NotificationsSettingsViewModelFactory,
    notificationDetailsViewModelFactory: NotificationDetailsViewModelFactory,
    upcomingScheduleViewModelFactory: UpcomingScheduleViewModelFactory,
    initialNotificationItemId: Long?,
    onNotificationItemConsumed: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val layoutDirection =
        if (configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            LayoutDirection.Rtl
        } else {
            LayoutDirection.Ltr
        }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        val navController = rememberNavController()
        val mainViewModel: MainViewModel = viewModel(factory = mainViewModelFactory)

        LaunchedEffect(initialNotificationItemId) {
            if (initialNotificationItemId != null) {
                navController.navigate(NavRoutes.counterDetails(initialNotificationItemId)) {
                    launchSingleTop = true
                }
                onNotificationItemConsumed()
            }
        }

        NavHost(navController = navController, startDestination = NavRoutes.LIST) {
            composable(NavRoutes.LIST) {
                ListScreen(viewModel = mainViewModel, navController = navController)
            }
            composable(
                route = NavRoutes.COUNTER_DETAILS,
                arguments = listOf(
                    navArgument(NavArguments.COUNTER_ID) { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val counterId = backStackEntry.arguments
                    ?.getLong(NavArguments.COUNTER_ID)
                    ?: 0L
                val notificationViewModel: ItemNotificationViewModel = viewModel(
                    factory = notificationViewModelFactory,
                    key = "notification_$counterId"
                )
                DetailScreen(
                    counterId = counterId,
                    viewModel = mainViewModel,
                    notificationViewModel = notificationViewModel,
                    navController = navController
                )
            }
            composable(NavRoutes.SETTINGS) {
                SettingsScreen(navController = navController)
            }
            composable(NavRoutes.NOTIFICATIONS_SETTINGS) {
                val settingsViewModel: NotificationsSettingsViewModel = viewModel(
                    factory = notificationsSettingsViewModelFactory
                )
                NotificationsSettingsScreen(
                    navController = navController,
                    viewModel = settingsViewModel
                )
            }
            composable(
                route = NavRoutes.NOTIFICATION_DETAILS,
                arguments = listOf(
                    navArgument(NavArguments.NOTIFICATION_ITEM_ID) { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val itemId = backStackEntry.arguments
                    ?.getLong(NavArguments.NOTIFICATION_ITEM_ID)
                    ?: 0L
                val notificationViewModel: ItemNotificationViewModel = viewModel(
                    factory = notificationViewModelFactory,
                    key = "notification_details_$itemId"
                )
                val detailsViewModel: NotificationDetailsViewModel = viewModel(
                    factory = notificationDetailsViewModelFactory,
                    key = "notification_counter_$itemId"
                )
                NotificationDetailsScreen(
                    itemId = itemId,
                    navController = navController,
                    notificationViewModel = notificationViewModel,
                    detailsViewModel = detailsViewModel
                )
            }
            composable(NavRoutes.UPCOMING_SCHEDULE) {
                val upcomingViewModel: UpcomingScheduleViewModel = viewModel(
                    factory = upcomingScheduleViewModelFactory
                )
                UpcomingScheduleScreen(
                    navController = navController,
                    viewModel = upcomingViewModel
                )
            }
            composable(NavRoutes.GUIDELINES) {
                GuidelinesScreen(navController = navController)
            }
            composable(
                route = NavRoutes.GUIDELINE_DETAILS,
                arguments = listOf(
                    navArgument(NavArguments.GUIDELINE_ID) { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val guidelineId = backStackEntry.arguments
                    ?.getInt(NavArguments.GUIDELINE_ID)
                    ?: 0
                GuidelineDetailScreen(
                    categoryId = guidelineId,
                    navController = navController
                )
            }
        }
    }
}
