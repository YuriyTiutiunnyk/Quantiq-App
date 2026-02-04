package com.example.quantiq.ui

import android.view.View
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.quantiq.R
import com.example.quantiq.ui.components.BottomBarItem
import com.example.quantiq.ui.components.BottomBarItemPosition
import com.example.quantiq.ui.components.ConvexBottomBar
import com.example.quantiq.ui.navigation.NavArguments
import com.example.quantiq.ui.navigation.NavRoutes
import com.example.quantiq.ui.screens.ActiveItemScreen
import com.example.quantiq.ui.screens.DetailScreen
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
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

/**
 * The root composable that wires navigation, ViewModels, and layout direction for the app.
 */
@OptIn(ExperimentalAnimationApi::class)
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
        val navController = rememberAnimatedNavController()
        val mainViewModel: MainViewModel = viewModel(factory = mainViewModelFactory)
        val activeTabs = listOf(NavRoutes.LIST, NavRoutes.ACTIVE, NavRoutes.SETTINGS)
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val showBottomBar = currentRoute in activeTabs
        val defaultTitle = stringResource(R.string.default_item_title)

        LaunchedEffect(initialNotificationItemId) {
            if (initialNotificationItemId != null) {
                navController.navigate(NavRoutes.counterDetails(initialNotificationItemId)) {
                    launchSingleTop = true
                }
                onNotificationItemConsumed()
            }
        }

        LaunchedEffect(defaultTitle) {
            mainViewModel.dispatch(MainIntent.InitializeDefaultCounter(defaultTitle))
        }

        Scaffold(
            contentWindowInsets = WindowInsets(0),
            bottomBar = {
                if (showBottomBar) {
                    ConvexBottomBar(
                        currentRoute = currentRoute,
                        items = listOf(
                            BottomBarItem(
                                route = NavRoutes.LIST,
                                label = stringResource(R.string.tab_list),
                                icon = Icons.Default.List,
                                position = BottomBarItemPosition.Left
                            ),
                            BottomBarItem(
                                route = NavRoutes.ACTIVE,
                                label = stringResource(R.string.tab_active),
                                icon = Icons.Default.Tune,
                                position = BottomBarItemPosition.Center
                            ),
                            BottomBarItem(
                                route = NavRoutes.SETTINGS,
                                label = stringResource(R.string.tab_settings),
                                icon = Icons.Default.Settings,
                                position = BottomBarItemPosition.Right
                            )
                        ),
                        onNavigate = { route ->
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        ) { padding ->
            val contentPadding = PaddingValues(
                start = padding.calculateStartPadding(layoutDirection),
                top = padding.calculateTopPadding(),
                end = padding.calculateEndPadding(layoutDirection),
                bottom = 0.dp
            )
            val tabOrder = mapOf(
                NavRoutes.LIST to 0,
                NavRoutes.ACTIVE to 1,
                NavRoutes.SETTINGS to 2
            )
            val slideInTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
                {
                    val fromIndex = tabOrder[initialState.destination.route]
                    val toIndex = tabOrder[targetState.destination.route]
                    if (fromIndex != null && toIndex != null && fromIndex != toIndex) {
                        val direction = if (toIndex > fromIndex) 1 else -1
                        slideInHorizontally { fullWidth -> fullWidth * direction } + fadeIn()
                    } else {
                        fadeIn()
                    }
                }
            val slideOutTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
                {
                    val fromIndex = tabOrder[initialState.destination.route]
                    val toIndex = tabOrder[targetState.destination.route]
                    if (fromIndex != null && toIndex != null && fromIndex != toIndex) {
                        val direction = if (toIndex > fromIndex) -1 else 1
                        slideOutHorizontally { fullWidth -> fullWidth * direction } + fadeOut()
                    } else {
                        fadeOut()
                    }
                }
            AnimatedNavHost(
                navController = navController,
                startDestination = NavRoutes.LIST,
                modifier = androidx.compose.ui.Modifier.padding(contentPadding),
                enterTransition = slideInTransition,
                exitTransition = slideOutTransition,
                popEnterTransition = slideInTransition,
                popExitTransition = slideOutTransition
            ) {
                composable(NavRoutes.LIST) {
                    ListScreen(viewModel = mainViewModel, navController = navController)
                }
                composable(NavRoutes.ACTIVE) {
                    ActiveItemScreen(viewModel = mainViewModel, navController = navController)
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
                    SettingsScreen(
                        viewModel = mainViewModel,
                        navController = navController,
                        showBackButton = false
                    )
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
            }
        }
    }
}
