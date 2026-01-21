package com.example.quantiq.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import android.view.View
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.quantiq.QuantiqApplication
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
import com.example.quantiq.ui.theme.QuantiqTheme
import com.example.quantiq.notifications.NotificationConstants

class MainActivity : ComponentActivity() {
    private val pendingNotificationItemId = mutableStateOf<Long?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer = (application as QuantiqApplication).appContainer
        appContainer.billingManager.startConnection()
        pendingNotificationItemId.value = extractNotificationItemId(intent)
        val mainViewModelFactory = MainViewModelFactory(
            appContainer.observeCountersUseCase,
            appContainer.addCounterUseCase,
            appContainer.updateCounterValueUseCase,
            appContainer.updateCounterDetailsUseCase,
            appContainer.deleteCounterUseCase,
            appContainer.resetCounterUseCase,
            appContainer.billingManager,
            appContainer.backupManager
        )
        val notificationViewModelFactory = ItemNotificationViewModelFactory(
            appContainer.getItemNotificationConfigUseCase,
            appContainer.upsertItemNotificationConfigUseCase,
            appContainer.disableItemNotificationUseCase
        )
        val notificationsSettingsViewModelFactory = NotificationsSettingsViewModelFactory(
            appContainer.observeCountersUseCase,
            appContainer.observeAllNotificationConfigsUseCase,
            appContainer.setNotificationEnabledUseCase,
            appContainer.disableAllNotificationsUseCase
        )
        val notificationDetailsViewModelFactory = NotificationDetailsViewModelFactory(
            appContainer.observeCounterUseCase
        )
        val upcomingScheduleViewModelFactory = UpcomingScheduleViewModelFactory(
            appContainer.observeCountersUseCase,
            appContainer.getUpcomingNotificationsUseCase
        )

        setContent {
            val configuration = LocalConfiguration.current
            val layoutDirection =
                if (configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                    LayoutDirection.Rtl
                } else {
                    LayoutDirection.Ltr
                }

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                QuantiqTheme {
                    val navController = rememberNavController()
                    val mainViewModel: MainViewModel = viewModel(factory = mainViewModelFactory)
                    val targetItemId = pendingNotificationItemId.value

                    LaunchedEffect(targetItemId) {
                        if (targetItemId != null) {
                            navController.navigate(NavRoutes.counterDetails(targetItemId)) {
                                launchSingleTop = true
                            }
                            pendingNotificationItemId.value = null
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
                                navArgument(NavArguments.NOTIFICATION_ITEM_ID) {
                                    type = NavType.LongType
                                }
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
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        extractNotificationItemId(intent)?.let { pendingNotificationItemId.value = it }
    }

    private fun extractNotificationItemId(intent: Intent?): Long? {
        val id = intent?.getLongExtra(NotificationConstants.EXTRA_ITEM_ID, -1L) ?: -1L
        return id.takeIf { it > 0L }
    }
}
