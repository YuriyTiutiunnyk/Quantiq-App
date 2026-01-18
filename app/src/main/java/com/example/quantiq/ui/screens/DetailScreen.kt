package com.example.quantiq.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.example.quantiq.ui.MainIntent
import com.example.quantiq.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    counterId: Long,
    viewModel: MainViewModel,
    navController: NavController
) {
    // In real app, collect specific counter flow
    val state by viewModel.state.collectAsState()
    val counter = state.counters.find { it.id == counterId }

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
                    onClick = { viewModel.dispatch(MainIntent.UpdateCounterValue(counter, -counter.step)) },
                    modifier = Modifier.size(80.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text("-${counter.step}", style = MaterialTheme.typography.titleLarge)
                }
                
                Button(
                    onClick = { viewModel.dispatch(MainIntent.UpdateCounterValue(counter, counter.step)) },
                    modifier = Modifier.size(96.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text("+${counter.step}", style = MaterialTheme.typography.headlineMedium)
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            TextButton(onClick = { viewModel.dispatch(MainIntent.ResetCounter(counter.id)) }) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reset")
            }
        }
    }
}
