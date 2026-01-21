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
