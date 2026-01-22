package com.example.quantiq.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * Renders a curved/convex styled bottom navigation bar with a raised center action.
 */
@Composable
fun ConvexBottomBar(
    currentRoute: String?,
    items: List<BottomBarItem>,
    onNavigate: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Surface(
            tonalElevation = 3.dp,
            shadowElevation = 8.dp,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.filter { it.position != BottomBarItemPosition.Center }.forEach { item ->
                    BottomBarButton(
                        selected = currentRoute == item.route,
                        label = item.label,
                        icon = item.icon,
                        onClick = { onNavigate(item.route) }
                    )
                }
            }
        }

        items.firstOrNull { it.position == BottomBarItemPosition.Center }?.let { item ->
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-28).dp),
                shape = CircleShape,
                color = if (currentRoute == item.route) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surface
                },
                shadowElevation = 10.dp,
                tonalElevation = 6.dp
            ) {
                ElevatedCircleButton(
                    onClick = { onNavigate(item.route) },
                    size = 64.dp,
                    content = {
                        val contentColor = if (currentRoute == item.route) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                        Icon(
                            item.icon,
                            contentDescription = item.label,
                            tint = contentColor
                        )
                    }
                )
            }
        }
    }
}

/**
 * Defines a bottom bar destination slot.
 */
data class BottomBarItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val position: BottomBarItemPosition
)

/**
 * Declares the slot position for a bottom bar item.
 */
enum class BottomBarItemPosition {
    Left,
    Center,
    Right
}

@Composable
private fun BottomBarButton(
    selected: Boolean,
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    val contentColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    androidx.compose.material3.TextButton(onClick = onClick) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = label, tint = contentColor)
            Text(text = label, color = contentColor, style = MaterialTheme.typography.labelMedium)
        }
    }
}
