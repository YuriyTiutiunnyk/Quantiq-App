package com.example.quantiq.ui.screens

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.quantiq.R
import com.example.quantiq.domain.model.Counter
import com.example.quantiq.domain.model.ItemNotificationConfig
import com.example.quantiq.domain.model.NotificationAction
import com.example.quantiq.domain.model.NotificationActionType
import com.example.quantiq.domain.model.RepeatType
import com.example.quantiq.domain.model.ScheduleType
import com.example.quantiq.ui.ItemNotificationViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

@Composable
fun ItemNotificationSection(
    counter: Counter,
    viewModel: ItemNotificationViewModel
) {
    val context = LocalContext.current
    val config by viewModel.config.collectAsState()
    val actions = remember { mutableStateListOf<NotificationAction>() }
    val zoneId = remember { ZoneId.systemDefault() }

    var enabled by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf(counter.title) }
    var body by remember { mutableStateOf("") }
    var scheduleType by remember { mutableStateOf(ScheduleType.ONE_TIME) }
    var startAtMillis by remember { mutableStateOf(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1)) }
    var repeatType by remember { mutableStateOf(RepeatType.NONE) }
    var repeatIntervalMinutes by remember { mutableStateOf("60") }
    var endAtMillis by remember { mutableStateOf<Long?>(null) }
    var showPermissionSettings by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        enabled = granted
        showPermissionSettings = !granted
    }

    LaunchedEffect(counter.id) {
        viewModel.setItemId(counter.id)
    }

    LaunchedEffect(config) {
        val current = config
        if (current != null) {
            enabled = current.enabled
            title = current.title
            body = current.body
            scheduleType = current.scheduleType
            startAtMillis = current.startAtEpochMillis
            repeatType = current.repeatType
            repeatIntervalMinutes = current.repeatIntervalMinutes?.toString().orEmpty()
            endAtMillis = current.endAtEpochMillis
            actions.clear()
            actions.addAll(current.actions)
        } else {
            enabled = false
            title = counter.title
            body = ""
            scheduleType = ScheduleType.ONE_TIME
            startAtMillis = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1)
            repeatType = RepeatType.NONE
            repeatIntervalMinutes = "60"
            endAtMillis = null
            actions.clear()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Notifications, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(R.string.local_notification_section), style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = enabled,
                onCheckedChange = { checked ->
                    if (checked && Build.VERSION.SDK_INT >= 33) {
                        val granted = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                        if (!granted) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            return@Switch
                        }
                    }
                    enabled = checked
                }
            )
        }

        if (showPermissionSettings) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = stringResource(R.string.notification_permission_denied))
            TextButton(onClick = {
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                }
                context.startActivity(intent)
            }) {
                Text(text = stringResource(R.string.open_notification_settings))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(text = stringResource(R.string.notification_title)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = body,
            onValueChange = { body = it },
            label = { Text(text = stringResource(R.string.notification_body)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = stringResource(R.string.notification_schedule), style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(8.dp))
        DropdownSelector(
            label = stringResource(R.string.notification_schedule_type),
            selected = scheduleType,
            options = ScheduleType.values().toList(),
            onSelected = { scheduleType = it }
        ) { it.name }

        Spacer(modifier = Modifier.height(8.dp))
        DateTimePickerButton(
            label = stringResource(R.string.notification_start_at),
            epochMillis = startAtMillis,
            zoneId = zoneId,
            onPicked = { picked -> picked?.let { startAtMillis = it } }
        )

        if (scheduleType == ScheduleType.REPEATING) {
            Spacer(modifier = Modifier.height(8.dp))
            DropdownSelector(
                label = stringResource(R.string.notification_repeat),
                selected = repeatType,
                options = RepeatType.values().toList(),
                onSelected = { repeatType = it }
            ) { it.name }

            if (repeatType == RepeatType.INTERVAL) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = repeatIntervalMinutes,
                    onValueChange = { repeatIntervalMinutes = it },
                    label = { Text(text = stringResource(R.string.notification_repeat_interval)) },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            DateTimePickerButton(
                label = stringResource(R.string.notification_end_at),
                epochMillis = endAtMillis,
                zoneId = zoneId,
                onPicked = { endAtMillis = it },
                allowClear = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = stringResource(R.string.notification_actions), style = MaterialTheme.typography.titleSmall)
        actions.forEachIndexed { index, action ->
            Spacer(modifier = Modifier.height(8.dp))
            ActionEditorRow(
                action = action,
                onActionChange = { updated -> actions[index] = updated },
                onRemove = { actions.removeAt(index) }
            )
        }
        if (actions.size < 3) {
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = {
                actions.add(NotificationAction(label = "Action", type = NotificationActionType.OPEN_ITEM))
            }) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = stringResource(R.string.notification_add_action))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val interval = repeatIntervalMinutes.toIntOrNull()?.takeIf { it > 0 }
                val configToSave = ItemNotificationConfig(
                    itemId = counter.id,
                    enabled = enabled,
                    title = title.ifBlank { counter.title },
                    body = body,
                    scheduleType = scheduleType,
                    startAtEpochMillis = startAtMillis,
                    timeZoneId = zoneId.id,
                    repeatType = if (scheduleType == ScheduleType.REPEATING) repeatType else RepeatType.NONE,
                    repeatIntervalMinutes = if (repeatType == RepeatType.INTERVAL) interval else null,
                    endAtEpochMillis = if (scheduleType == ScheduleType.REPEATING) endAtMillis else null,
                    actions = actions.toList()
                )
                viewModel.save(configToSave)
                if (!enabled) {
                    viewModel.disable(counter.id)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.notification_save))
        }
    }
}

@Composable
private fun DateTimePickerButton(
    label: String,
    epochMillis: Long?,
    zoneId: ZoneId,
    onPicked: (Long?) -> Unit,
    allowClear: Boolean = false
) {
    val context = LocalContext.current
    val formatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm") }
    val displayText = epochMillis?.let {
        Instant.ofEpochMilli(it).atZone(zoneId).format(formatter)
    } ?: stringResource(R.string.notification_not_set)

    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedButton(onClick = {
            val now = Instant.ofEpochMilli(epochMillis ?: System.currentTimeMillis()).atZone(zoneId)
            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    TimePickerDialog(
                        context,
                        { _, hour, minute ->
                            val picked = now
                                .withYear(year)
                                .withMonth(month + 1)
                                .withDayOfMonth(dayOfMonth)
                                .withHour(hour)
                                .withMinute(minute)
                                .withSecond(0)
                                .withNano(0)
                            onPicked(picked.toInstant().toEpochMilli())
                        },
                        now.hour,
                        now.minute,
                        android.text.format.DateFormat.is24HourFormat(context)
                    ).show()
                },
                now.year,
                now.monthValue - 1,
                now.dayOfMonth
            ).show()
        }) {
            Text(text = "$label: $displayText")
        }
        if (allowClear && epochMillis != null) {
            IconButton(onClick = { onPicked(null) }) {
                Icon(Icons.Default.Delete, contentDescription = null)
            }
        }
    }
}

@Composable
private fun <T> DropdownSelector(
    label: String,
    selected: T,
    options: List<T>,
    onSelected: (T) -> Unit,
    labelProvider: (T) -> String
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        TextButton(onClick = { expanded = true }) {
            Text(text = labelProvider(selected))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = labelProvider(option)) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ActionEditorRow(
    action: NotificationAction,
    onActionChange: (NotificationAction) -> Unit,
    onRemove: () -> Unit
) {
    var typeExpanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = action.label,
                onValueChange = { onActionChange(action.copy(label = it)) },
                label = { Text(text = stringResource(R.string.notification_action_label)) },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = null)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = { typeExpanded = true }) {
                Text(text = action.type.name)
            }
            DropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }) {
                NotificationActionType.values().forEach { type ->
                    DropdownMenuItem(
                        text = { Text(text = type.name) },
                        onClick = {
                            onActionChange(action.copy(type = type))
                            typeExpanded = false
                        }
                    )
                }
            }
            if (action.type == NotificationActionType.SNOOZE) {
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = action.payload ?: "10",
                    onValueChange = { onActionChange(action.copy(payload = it)) },
                    label = { Text(text = stringResource(R.string.notification_snooze_minutes)) },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.width(160.dp)
                )
            }
        }
    }
}
