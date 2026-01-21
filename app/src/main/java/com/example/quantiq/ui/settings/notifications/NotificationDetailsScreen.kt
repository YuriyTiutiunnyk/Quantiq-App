package com.example.quantiq.ui.settings.notifications

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.quantiq.R
import com.example.quantiq.ui.ItemNotificationViewModel
import com.example.quantiq.ui.screens.ItemNotificationSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationDetailsScreen(
    itemId: Long,
    navController: NavController,
    notificationViewModel: ItemNotificationViewModel,
    detailsViewModel: NotificationDetailsViewModel
) {
    val counter by detailsViewModel.counter.collectAsState()

    LaunchedEffect(itemId) {
        detailsViewModel.setCounterId(itemId)
        notificationViewModel.setItemId(itemId)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        TopAppBar(
            title = { Text(stringResource(R.string.notification_details_title)) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                }
            }
        )
        counter?.let { loadedCounter ->
            ItemNotificationSection(counter = loadedCounter, viewModel = notificationViewModel)
        } ?: run {
            Text(
                text = stringResource(R.string.notification_item_missing),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
