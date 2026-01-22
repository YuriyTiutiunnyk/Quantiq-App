package com.example.quantiq.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quantiq.R
import com.example.quantiq.domain.model.Counter
import com.example.quantiq.ui.MainIntent
import com.example.quantiq.ui.MainViewModel
import com.example.quantiq.ui.navigation.NavRoutes

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
                title = { Text(stringResource(R.string.app_name)) },
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
                    navController.navigate(NavRoutes.SETTINGS)
                } else {
                    showDialog = true
                }
            }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_counter))
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                GuidelinesEntryCard(
                    onClick = { navController.navigate(NavRoutes.GUIDELINES) }
                )
            }
            items(state.counters) { counter ->
                CounterItem(
                    counter = counter,
                    isActive = counter.id == state.activeItemId,
                    onSelect = {
                        viewModel.dispatch(MainIntent.SetActiveCounter(counter.id))
                        navController.navigate(NavRoutes.ACTIVE) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onDelete = { viewModel.dispatch(MainIntent.DeleteCounter(counter.id)) }
                )
            }
            
            // Placeholder for locked item
            if (!state.isPro && state.counters.size >= 3) {
                item {
                    LockedItem(onClick = { navController.navigate(NavRoutes.SETTINGS) })
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
fun CounterItem(
    counter: Counter,
    isActive: Boolean,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onSelect,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = counter.title, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (counter.isDefault) {
                        AssistChip(
                            onClick = {},
                            label = { Text(stringResource(R.string.default_item_badge)) }
                        )
                    }
                    if (isActive) {
                        AssistChip(
                            onClick = {},
                            label = { Text(stringResource(R.string.active_item_badge)) }
                        )
                    }
                }
                Text(
                    text = stringResource(R.string.counter_step_format, counter.step),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = counter.value.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(
                    onClick = onDelete,
                    enabled = !counter.isDefault
                ) {
                    if (counter.isDefault) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = stringResource(R.string.default_item_locked)
                        )
                    } else {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete)
                        )
                    }
                }
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
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
            Text(stringResource(R.string.free_limit_reached))
        }
    }
}

@Composable
fun GuidelinesEntryCard(onClick: () -> Unit) {
    OutlinedCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.MenuBook,
                contentDescription = stringResource(R.string.guidelines_navigation),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.guidelines_title),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.guidelines_navigation_description),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AddCounterDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.new_counter_title)) },
        text = { 
            OutlinedTextField(
                value = text, 
                onValueChange = { text = it },
                label = { Text(stringResource(R.string.counter_name_label)) }
            ) 
        },
        confirmButton = {
            val defaultTitle = stringResource(R.string.new_counter_default)
            TextButton(onClick = { onConfirm(text.ifBlank { defaultTitle }) }) {
                Text(stringResource(R.string.create))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}
