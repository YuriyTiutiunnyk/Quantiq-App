package com.example.quantiq.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quantiq.R
import com.example.quantiq.ui.MainIntent
import com.example.quantiq.ui.MainViewModel
import com.example.quantiq.ui.components.ElevatedCircleButton
import com.example.quantiq.ui.navigation.NavRoutes
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveItemScreen(
    viewModel: MainViewModel,
    navController: NavController
) {
    val state by viewModel.state.collectAsState()
    val activeCounter = state.counters.firstOrNull { it.id == state.activeItemId }
        ?: state.counters.firstOrNull { it.isDefault }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var showStepDialog by remember { mutableStateOf(false) }
    var showCustomStepDialog by remember { mutableStateOf(false) }
    var showResetItemDialog by remember { mutableStateOf(false) }
    var customStepInput by rememberSaveable(activeCounter?.id) {
        mutableStateOf(activeCounter?.step?.toString().orEmpty())
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (activeCounter == null) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(R.string.active_item_empty))
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = activeCounter.title,
                    style = MaterialTheme.typography.headlineSmall
                )
                IconButton(
                    onClick = {
                        navController.navigate(NavRoutes.counterDetails(activeCounter.id)) {
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.edit_item)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = activeCounter.value.toString(),
                style = MaterialTheme.typography.displayLarge
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ElevatedCircleButton(
                    onClick = {
                        viewModel.dispatch(
                            MainIntent.UpdateCounterValue(activeCounter, -activeCounter.step)
                        )
                    },
                    size = 72.dp
                ) {
                    Text(
                        text = stringResource(R.string.decrement_symbol),
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
                ElevatedCircleButton(
                    onClick = { showStepDialog = true },
                    size = 80.dp,
                    tonalElevation = 6.dp,
                    shadowElevation = 8.dp
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(R.string.step_label),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = activeCounter.step.toString(),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
                ElevatedCircleButton(
                    onClick = {
                        viewModel.dispatch(
                            MainIntent.UpdateCounterValue(activeCounter, activeCounter.step)
                        )
                    },
                    size = 72.dp
                ) {
                    Text(
                        text = stringResource(R.string.increment_symbol),
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(onClick = { showResetItemDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.reset_item))
            }
        }
    }

    if (activeCounter != null) {
        if (showStepDialog) {
            StepPickerDialog(
                onDismiss = { showStepDialog = false },
                onSelectStep = { step ->
                    viewModel.dispatch(
                        MainIntent.UpdateCounterDetails(activeCounter, activeCounter.title, step)
                    )
                    showStepDialog = false
                },
                onSelectCustom = {
                    showStepDialog = false
                    if (state.isPro) {
                        showCustomStepDialog = true
                    } else {
                        coroutineScope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = stringResource(R.string.custom_step_pro_message),
                                actionLabel = stringResource(R.string.upgrade_action)
                            )
                            if (result == androidx.compose.material3.SnackbarResult.ActionPerformed) {
                                navController.navigate(NavRoutes.SETTINGS) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    }
                }
            )
        }

        if (showCustomStepDialog) {
            AlertDialog(
                onDismissRequest = { showCustomStepDialog = false },
                title = { Text(stringResource(R.string.custom_step_title)) },
                text = {
                    OutlinedTextField(
                        value = customStepInput,
                        onValueChange = { customStepInput = it },
                        label = { Text(stringResource(R.string.custom_step_label)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val step = customStepInput.toIntOrNull()
                            if (step != null && step > 0) {
                                viewModel.dispatch(
                                    MainIntent.UpdateCounterDetails(
                                        activeCounter,
                                        activeCounter.title,
                                        step
                                    )
                                )
                                showCustomStepDialog = false
                            } else {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = stringResource(R.string.custom_step_invalid)
                                    )
                                }
                            }
                        }
                    ) {
                        Text(stringResource(R.string.save))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCustomStepDialog = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }

        if (showResetItemDialog) {
            AlertDialog(
                onDismissRequest = { showResetItemDialog = false },
                title = { Text(stringResource(R.string.reset_item_title)) },
                text = { Text(stringResource(R.string.reset_item_body)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.dispatch(MainIntent.ResetCounter(activeCounter.id))
                            showResetItemDialog = false
                        }
                    ) {
                        Text(stringResource(R.string.reset_item_confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetItemDialog = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
    }
}

@Composable
private fun StepPickerDialog(
    onDismiss: () -> Unit,
    onSelectStep: (Int) -> Unit,
    onSelectCustom: () -> Unit
) {
    val stepOptions = listOf(1, 5, 10, 100)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.step_picker_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                stepOptions.forEach { step ->
                    TextButton(onClick = { onSelectStep(step) }) {
                        Text(text = step.toString())
                    }
                }
                TextButton(onClick = onSelectCustom) {
                    Text(stringResource(R.string.step_custom_option))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    )
}
