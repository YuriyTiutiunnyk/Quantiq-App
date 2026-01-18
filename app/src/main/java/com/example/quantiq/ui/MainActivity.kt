package com.example.quantiq.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.example.quantiq.billing.BillingManager
import com.example.quantiq.data.BackupManager
import com.example.quantiq.data.QuantiqDatabase
import com.example.quantiq.ui.screens.DetailScreen
import com.example.quantiq.ui.screens.ListScreen
import com.example.quantiq.ui.theme.QuantiqTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = Room.databaseBuilder(
            applicationContext,
            QuantiqDatabase::class.java,
            "quantiq.db"
        ).build()
        val dao = database.counterDao()

        val billingManager = BillingManager(this, lifecycleScope)
        billingManager.startConnection()
        val backupManager = BackupManager(this)

        val counterViewModelFactory = CounterViewModelFactory(dao)
        val mainViewModelFactory = MainViewModelFactory(dao, billingManager, backupManager)

        setContent {
            QuantiqTheme {
                val navController = rememberNavController()
                val counterViewModel: CounterViewModel = viewModel(factory = counterViewModelFactory)
                viewModel<MainViewModel>(factory = mainViewModelFactory)

                NavHost(navController = navController, startDestination = "list") {
                    composable("list") {
                        ListScreen(viewModel = counterViewModel, navController = navController)
                    }
                    composable(
                        route = "details/{id}",
                        arguments = listOf(navArgument("id") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val counterId = backStackEntry.arguments?.getLong("id") ?: 0L
                        DetailScreen(
                            counterId = counterId,
                            viewModel = counterViewModel,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}
