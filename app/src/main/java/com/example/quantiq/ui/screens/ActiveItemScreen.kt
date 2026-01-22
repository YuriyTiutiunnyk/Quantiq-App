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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quantiq.R
import com.example.quantiq.ui.MainIntent
import com.example.quantiq.ui.MainViewModel
import com.example.quantiq.ui.navigation.NavRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveItemScreen(
    viewModel: MainViewModel,
    navController: NavController
) {
    val state by viewModel.state.collectAsState()
    val activeCounter = state.counters.firstOrNull { it.id == state.activeItemId }
        ?: state.counters.firstOrNull { it.isDefault }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.active_item_title)) }
            )
        }
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
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = activeCounter.title,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = activeCounter.value.toString(),
                style = MaterialTheme.typography.displayLarge
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalButton(
                    onClick = {
                        viewModel.dispatch(
                            MainIntent.UpdateCounterValue(activeCounter, -activeCounter.step)
                        )
                    },
                    modifier = Modifier.size(80.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text("-${activeCounter.step}", style = MaterialTheme.typography.titleLarge)
                }

                Button(
                    onClick = {
                        viewModel.dispatch(
                            MainIntent.UpdateCounterValue(activeCounter, activeCounter.step)
                        )
                    },
                    modifier = Modifier.size(96.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text("+${activeCounter.step}", style = MaterialTheme.typography.headlineMedium)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (activeCounter.isDefault) {
                OutlinedButton(
                    onClick = { viewModel.dispatch(MainIntent.ResetCounter(activeCounter.id)) }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        androidx.compose.material3.Icon(
                            Icons.Default.Refresh,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.reset_default_item))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { navController.navigate(NavRoutes.counterDetails(activeCounter.id)) }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.material3.Icon(
                        Icons.Default.Visibility,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.open_details))
                }
            }
        }
    }
}
