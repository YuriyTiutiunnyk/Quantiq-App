package com.example.quantiq.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quantiq.R
import com.example.quantiq.ui.MainIntent
import com.example.quantiq.ui.MainViewModel
import com.example.quantiq.ui.components.design.CircularIconButton
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
    var showActionMenu by remember { mutableStateOf(false) }
    var customStepInput by rememberSaveable(activeCounter?.id) {
        mutableStateOf(activeCounter?.step?.toString().orEmpty())
    }
    val counterPulse = remember { Animatable(1f) }
    val actionButtonSize = 56.dp
    val actionIconSize = 22.dp
    val decrementColor = Color(0xFFFFE1E1)
    val incrementColor = Color(0xFFDDF4E3)

    Scaffold(
        contentWindowInsets = WindowInsets(0),
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

        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .background(decrementColor)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            viewModel.dispatch(
                                MainIntent.UpdateCounterValue(activeCounter, -activeCounter.step)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.decrement_symbol),
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .background(incrementColor)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            viewModel.dispatch(
                                MainIntent.UpdateCounterValue(activeCounter, activeCounter.step)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.increment_symbol),
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                }
            }

            Column(
                modifier = Modifier
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
                }
                Spacer(modifier = Modifier.weight(1f))
            }

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(180.dp)
                    .graphicsLayer(
                        scaleX = counterPulse.value,
                        scaleY = counterPulse.value
                    )
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        viewModel.dispatch(
                            MainIntent.UpdateCounterValue(activeCounter, activeCounter.step)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = activeCounter.value.toString(),
                    style = MaterialTheme.typography.displayLarge
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CircularIconButton(
                    icon = Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.open_details),
                    onClick = { showActionMenu = !showActionMenu },
                    size = actionButtonSize,
                    iconSize = actionIconSize
                )
                AnimatedVisibility(
                    visible = showActionMenu,
                    enter = fadeIn(animationSpec = tween(durationMillis = 220)) +
                        expandVertically(
                            expandFrom = Alignment.Top,
                            animationSpec = tween(durationMillis = 240)
                        ),
                    exit = fadeOut(animationSpec = tween(durationMillis = 180)) +
                        shrinkVertically(
                            shrinkTowards = Alignment.Top,
                            animationSpec = tween(durationMillis = 200)
                        )
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularIconButton(
                            icon = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.open_details),
                            onClick = {
                                showActionMenu = false
                                navController.navigate(NavRoutes.counterDetails(activeCounter.id)) {
                                    launchSingleTop = true
                                }
                            },
                            size = actionButtonSize,
                            iconSize = actionIconSize
                        )
                        CircularIconButton(
                            icon = Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.reset_item),
                            onClick = {
                                showActionMenu = false
                                showResetItemDialog = true
                            },
                            size = actionButtonSize,
                            iconSize = actionIconSize
                        )
                        CircularIconButton(
                            icon = Icons.Default.Tune,
                            contentDescription = stringResource(R.string.step_label),
                            onClick = {
                                showActionMenu = false
                                showStepDialog = true
                            },
                            size = actionButtonSize,
                            iconSize = actionIconSize
                        )
                    }
                }
            }

        }
    }

    LaunchedEffect(activeCounter?.value) {
        counterPulse.snapTo(1f)
        counterPulse.animateTo(
            targetValue = 1.08f,
            animationSpec = tween(durationMillis = 140)
        )
        counterPulse.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 160)
        )
    }

    if (activeCounter != null) {
        if (showStepDialog) {
            val message = stringResource(R.string.custom_step_pro_message)
            val actionLabel = stringResource(R.string.upgrade_action)
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
                                message = message,
                                actionLabel = actionLabel
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
                    val message = stringResource(R.string.custom_step_invalid)
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
                                        message = message
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
