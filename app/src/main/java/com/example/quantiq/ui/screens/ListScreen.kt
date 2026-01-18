package com.example.quantiq.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quantiq.domain.model.Counter
import com.example.quantiq.ui.MainIntent
import com.example.quantiq.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    viewModel: MainViewModel,
    navController: NavController
) {
    val state by viewModel.state.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quantiq") },
                actions = {
                    // Counter count badge
                    Badge { Text(state.counters.size.toString()) }
                    Spacer(modifier = Modifier.width(16.dp))
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (!state.isPro && state.counters.size >= 3) {
                    // Show Limit Snackbar or Navigate to PRO
                    navController.navigate("settings")
                } else {
                    showDialog = true
                }
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Counter")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.counters) { counter ->
                CounterItem(counter = counter, onClick = {
                    navController.navigate("details/${counter.id}")
                })
            }
            
            // Placeholder for locked item
            if (!state.isPro && state.counters.size >= 3) {
                item {
                    LockedItem(onClick = { navController.navigate("settings") })
                }
            }
        }
    }
    
    if (showDialog) {
        AddCounterDialog(
            onDismiss = { showDialog = false },
            onConfirm = { title ->
                viewModel.dispatch(MainIntent.AddCounter(title))
                showDialog = false
            }
        )
    }
}

@Composable
fun CounterItem(counter: Counter, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = counter.title, style = MaterialTheme.typography.titleMedium)
                Text(text = "Step: ${counter.step}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = counter.value.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun LockedItem(onClick: () -> Unit) {
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().alpha(0.6f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Lock, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Free limit reached (3/3)")
        }
    }
}

@Composable
fun AddCounterDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Counter") },
        text = { 
            OutlinedTextField(
                value = text, 
                onValueChange = { text = it },
                label = { Text("Name") }
            ) 
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(text.ifBlank { "New Counter" }) }) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
