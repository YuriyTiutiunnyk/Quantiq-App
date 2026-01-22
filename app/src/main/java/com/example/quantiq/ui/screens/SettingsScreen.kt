package com.example.quantiq.ui.screens

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavController
import com.example.quantiq.R
import com.example.quantiq.ui.navigation.NavRoutes
import java.util.Locale

/**
 * Represents LanguageOption.
 */
data class LanguageOption(
    val tag: String,
    val labelResId: Int
)

private fun languageFlag(tag: String): String {
    return when (tag) {
        "en" -> "\uD83C\uDDFA\uD83C\uDDF8"
        "es" -> "\uD83C\uDDEA\uD83C\uDDF8"
        "de" -> "\uD83C\uDDE9\uD83C\uDDEA"
        "it" -> "\uD83C\uDDEE\uD83C\uDDF9"
        "fr" -> "\uD83C\uDDEB\uD83C\uDDF7"
        "cs" -> "\uD83C\uDDE8\uD83C\uDDFF"
        "uk" -> "\uD83C\uDDFA\uD83C\uDDE6"
        "hi" -> "\uD83C\uDDEE\uD83C\uDDF3"
        "pt" -> "\uD83C\uDDF5\uD83C\uDDF9"
        "ar" -> "\uD83C\uDDF8\uD83C\uDDE6"
        else -> "\uD83C\uDFF3\uFE0F"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    showBackButton: Boolean = true
) {
    val configuration = LocalConfiguration.current
    val availableLanguages = listOf(
        LanguageOption("en", R.string.language_english),
        LanguageOption("es", R.string.language_spanish),
        LanguageOption("de", R.string.language_german),
        LanguageOption("it", R.string.language_italian),
        LanguageOption("fr", R.string.language_french),
        LanguageOption("cs", R.string.language_czech),
        LanguageOption("uk", R.string.language_ukrainian),
        LanguageOption("hi", R.string.language_hindi),
        LanguageOption("pt", R.string.language_portuguese),
        LanguageOption("ar", R.string.language_arabic)
    )
    val selectedLanguageTag = AppCompatDelegate.getApplicationLocales().toLanguageTags()
        .ifBlank { configuration.locales[0].toLanguageTag() }
    val normalizedSelectedTag = Locale.forLanguageTag(selectedLanguageTag).language
    val selectedLanguage = availableLanguages.firstOrNull { it.tag == normalizedSelectedTag }
        ?: availableLanguages.first()
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showProDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        TopAppBar(
            title = { Text(stringResource(R.string.settings)) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
            navigationIcon = {
                if (showBackButton) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SettingCard(
                icon = { Icon(Icons.Default.Star, contentDescription = null) },
                title = stringResource(R.string.pro_purchase_title),
                subtitle = stringResource(R.string.pro_purchase_subtitle),
                onClick = { showProDialog = true }
            )
            SettingCard(
                icon = { Icon(Icons.Default.Language, contentDescription = null) },
                title = stringResource(R.string.language),
                subtitle = stringResource(
                    R.string.language_current_format,
                    stringResource(selectedLanguage.labelResId)
                ),
                onClick = { showLanguageDialog = true }
            )
            SettingCard(
                icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
                title = stringResource(R.string.notifications_settings_title),
                subtitle = stringResource(R.string.notifications_settings_subtitle),
                onClick = { navController.navigate(NavRoutes.NOTIFICATIONS_SETTINGS) }
            )
        }
    }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(stringResource(R.string.select_language)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    availableLanguages.forEach { option ->
                        val isSelected = option.tag == normalizedSelectedTag
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    AppCompatDelegate.setApplicationLocales(
                                        LocaleListCompat.forLanguageTags(option.tag)
                                    )
                                    showLanguageDialog = false
                                }
                            )
                            Text(
                                text = languageFlag(option.tag),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(option.labelResId),
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                )
                                Text(
                                    text = Locale.forLanguageTag(option.tag).displayLanguage,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    if (showProDialog) {
        AlertDialog(
            onDismissRequest = { showProDialog = false },
            title = { Text(stringResource(R.string.pro_purchase_title)) },
            text = { Text(stringResource(R.string.pro_purchase_placeholder)) },
            confirmButton = {
                TextButton(onClick = { showProDialog = false }) {
                    Text(stringResource(R.string.close))
                }
            }
        )
    }
}

@Composable
private fun SettingCard(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    OutlinedCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
