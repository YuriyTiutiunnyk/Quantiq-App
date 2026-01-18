package com.example.quantiq.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quantiq.ui.CounterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    counterId: Long,
    viewModel: CounterViewModel,
    navController: NavController
) {
    // In real app, collect specific counter flow
    val counters by viewModel.counters.collectAsState()
    val counter = counters.find { it.id == counterId }

    if (counter == null) {
        // Handle loading or not found
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(counter.title) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Open Edit Dialog */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Edit")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = counter.value.toString(),
                style = MaterialTheme.typography.displayLarge
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalButton(
                    onClick = { viewModel.decrement(counter) },
                    modifier = Modifier.size(80.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text("-${counter.step}", style = MaterialTheme.typography.titleLarge)
                }
                
                Button(
                    onClick = { viewModel.increment(counter) },
                    modifier = Modifier.size(96.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text("+${counter.step}", style = MaterialTheme.typography.headlineMedium)
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            TextButton(onClick = { viewModel.reset(counter) }) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reset")
            }
        }
    }
}
