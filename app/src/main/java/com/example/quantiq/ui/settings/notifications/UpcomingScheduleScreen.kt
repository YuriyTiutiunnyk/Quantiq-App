package com.example.quantiq.ui.settings.notifications

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quantiq.R
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpcomingScheduleScreen(
    navController: NavController,
    viewModel: UpcomingScheduleViewModel
) {
    val state by viewModel.state.collectAsState()
    val formatter = rememberScheduleFormatter()

    Column(modifier = Modifier.fillMaxWidth()) {
        TopAppBar(
            title = { Text(stringResource(R.string.upcoming_schedule_title)) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.upcoming_filter_title),
                    style = MaterialTheme.typography.titleSmall
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = state.filterMode == UpcomingFilterMode.ALL,
                        onClick = { viewModel.setFilterMode(UpcomingFilterMode.ALL) }
                    )
                    Text(text = stringResource(R.string.upcoming_filter_all))
                    Spacer(modifier = Modifier.width(12.dp))
                    RadioButton(
                        selected = state.filterMode == UpcomingFilterMode.SELECTED,
                        onClick = { viewModel.setFilterMode(UpcomingFilterMode.SELECTED) }
                    )
                    Text(text = stringResource(R.string.upcoming_filter_selected))
                }
            }

            if (state.filterMode == UpcomingFilterMode.SELECTED) {
                item {
                    Text(
                        text = stringResource(R.string.upcoming_filter_select_items),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                items(state.counters, key = { it.id }) { counter ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = state.selectedItemIds.contains(counter.id),
                            onCheckedChange = { viewModel.toggleItemSelection(counter.id) }
                        )
                        Text(
                            text = counter.title.ifBlank { "Item #${counter.id}" },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.upcoming_schedule_header),
                    style = MaterialTheme.typography.titleSmall
                )
            }

            items(state.upcoming, key = { "${it.itemId}_${it.scheduledAtEpochMillis}" }) { item ->
                OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = formatter.format(item.scheduledAtEpochMillis),
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(text = item.itemTitle, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = item.notificationTitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (item.notificationBody.isNotBlank()) {
                            Text(
                                text = item.notificationBody,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun rememberScheduleFormatter(): ScheduleFormatter {
    val zoneId = ZoneId.systemDefault()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    return ScheduleFormatter(zoneId, formatter)
}

/**
 * Represents ScheduleFormatter.
 */
private class ScheduleFormatter(
    private val zoneId: ZoneId,
    private val formatter: DateTimeFormatter
) {
    fun format(epochMillis: Long): String {
        return Instant.ofEpochMilli(epochMillis).atZone(zoneId).format(formatter)
    }
}
