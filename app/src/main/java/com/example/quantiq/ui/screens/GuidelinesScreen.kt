package com.example.quantiq.ui.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quantiq.R
import com.example.quantiq.ui.navigation.NavRoutes

private const val BULLET_SEPARATOR = "•"

enum class GuidelineCategory(
    val id: Int,
    @StringRes val titleResId: Int,
    @StringRes val descriptionResId: Int,
    val highlights: List<Int>
) {
    MATERIAL(
        id = 1,
        titleResId = R.string.guideline_material_title,
        descriptionResId = R.string.guideline_material_description,
        highlights = listOf(
            R.string.guideline_material_point_one,
            R.string.guideline_material_point_two,
            R.string.guideline_material_point_three
        )
    ),
    ANDROID(
        id = 2,
        titleResId = R.string.guideline_android_title,
        descriptionResId = R.string.guideline_android_description,
        highlights = listOf(
            R.string.guideline_android_point_one,
            R.string.guideline_android_point_two,
            R.string.guideline_android_point_three
        )
    ),
    ACCESSIBILITY(
        id = 3,
        titleResId = R.string.guideline_accessibility_title,
        descriptionResId = R.string.guideline_accessibility_description,
        highlights = listOf(
            R.string.guideline_accessibility_point_one,
            R.string.guideline_accessibility_point_two,
            R.string.guideline_accessibility_point_three
        )
    ),
    PERFORMANCE(
        id = 4,
        titleResId = R.string.guideline_performance_title,
        descriptionResId = R.string.guideline_performance_description,
        highlights = listOf(
            R.string.guideline_performance_point_one,
            R.string.guideline_performance_point_two,
            R.string.guideline_performance_point_three
        )
    ),
    QUALITY(
        id = 5,
        titleResId = R.string.guideline_quality_title,
        descriptionResId = R.string.guideline_quality_description,
        highlights = listOf(
            R.string.guideline_quality_point_one,
            R.string.guideline_quality_point_two,
            R.string.guideline_quality_point_three
        )
    );

    companion object {
        fun fromId(id: Int): GuidelineCategory =
            entries.firstOrNull { it.id == id } ?: MATERIAL
    }
}

@Composable
fun GuidelinesScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.guidelines_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.guidelines_intro_title),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = stringResource(R.string.guidelines_intro_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            items(GuidelineCategory.entries) { category ->
                GuidelineCategoryCard(
                    category = category,
                    onClick = {
                        navController.navigate(
                            NavRoutes.guidelineDetails(category.id)
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun GuidelineDetailScreen(
    categoryId: Int,
    navController: NavController
) {
    val category = GuidelineCategory.fromId(categoryId)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(category.titleResId)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(category.descriptionResId),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = stringResource(R.string.guideline_key_focus),
                style = MaterialTheme.typography.titleMedium
            )
            category.highlights.forEach { highlightResId ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = BULLET_SEPARATOR,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                    Text(
                        text = stringResource(highlightResId),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun GuidelineCategoryCard(
    category: GuidelineCategory,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.LibraryBooks,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.padding(horizontal = 12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(category.titleResId),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(category.descriptionResId),
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
