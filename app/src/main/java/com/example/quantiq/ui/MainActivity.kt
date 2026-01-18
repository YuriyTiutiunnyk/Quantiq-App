package com.example.quantiq.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.quantiq.QuantiqApplication
import com.example.quantiq.ui.screens.DetailScreen
import com.example.quantiq.ui.screens.ListScreen
import com.example.quantiq.ui.theme.QuantiqTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer = (application as QuantiqApplication).appContainer
        appContainer.billingManager.startConnection()
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

        setContent {
            QuantiqTheme {
                val navController = rememberNavController()
                val mainViewModel: MainViewModel = viewModel(factory = mainViewModelFactory)

                NavHost(navController = navController, startDestination = "list") {
                    composable("list") {
                        ListScreen(viewModel = mainViewModel, navController = navController)
                    }
                    composable(
                        route = "details/{id}",
                        arguments = listOf(navArgument("id") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val counterId = backStackEntry.arguments?.getLong("id") ?: 0L
                        DetailScreen(
                            counterId = counterId,
                            viewModel = mainViewModel,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}
