package com.example.quantiq.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Renders a floating bottom navigation bar with three circular actions.
 *
 * TODO: Confirm button sizes, spacing, typography, and colors from navbar.fig/elements.fig
 *  exports once available.
 */
@Composable
fun ConvexBottomBar(
    currentRoute: String?,
    items: List<BottomBarItem>,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    tokens: ConvexBottomBarTokens = ConvexBottomBarTokens()
) {
    val centerItem = items.firstOrNull { it.position == BottomBarItemPosition.Center }
    val sideItems = items.filter { it.position != BottomBarItemPosition.Center }
    val selectedColor = MaterialTheme.colorScheme.primary
    val unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
    val buttonContainerColor = MaterialTheme.colorScheme.surface
    val labelStyle = MaterialTheme.typography.labelMedium
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(
                    start = tokens.horizontalPadding,
                    end = tokens.horizontalPadding,
                    top = tokens.verticalPadding,
                    bottom = tokens.verticalPadding + bottomInset
                ),
            verticalAlignment = Alignment.Bottom
        ) {
            val leftItem = sideItems.firstOrNull()
            val rightItem = sideItems.lastOrNull().takeIf { sideItems.size > 1 }

            leftItem?.let { item ->
                BottomBarButton(
                    selected = currentRoute == item.route,
                    label = item.label,
                    icon = item.icon,
                    selectedColor = selectedColor,
                    unselectedColor = unselectedColor,
                    iconSize = tokens.sideIconSize,
                    buttonSize = tokens.sideButtonSize,
                    buttonContainerColor = buttonContainerColor,
                    shadowElevation = tokens.buttonShadowElevation,
                    tonalElevation = tokens.buttonTonalElevation,
                    labelStyle = labelStyle,
                    onClick = { onNavigate(item.route) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.width(tokens.centerGapWidth))

            rightItem?.let { item ->
                BottomBarButton(
                    selected = currentRoute == item.route,
                    label = item.label,
                    icon = item.icon,
                    selectedColor = selectedColor,
                    unselectedColor = unselectedColor,
                    iconSize = tokens.sideIconSize,
                    buttonSize = tokens.sideButtonSize,
                    buttonContainerColor = buttonContainerColor,
                    shadowElevation = tokens.buttonShadowElevation,
                    tonalElevation = tokens.buttonTonalElevation,
                    labelStyle = labelStyle,
                    onClick = { onNavigate(item.route) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        centerItem?.let { item ->
            val isSelected = currentRoute == item.route
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = bottomInset + tokens.verticalPadding),
                shape = CircleShape,
                color = buttonContainerColor,
                shadowElevation = tokens.buttonShadowElevation,
                tonalElevation = tokens.buttonTonalElevation,
                onClick = { onNavigate(item.route) }
            ) {
                Box(
                    modifier = Modifier.size(tokens.centerButtonSize),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        item.icon,
                        contentDescription = item.label,
                        tint = if (isSelected) selectedColor else unselectedColor,
                        modifier = Modifier.size(tokens.centerIconSize)
                    )
                }
            }
        }
    }
}

/**
 * Visual tokens for the convex bottom bar.
 */
@Immutable
data class ConvexBottomBarTokens(
    val horizontalPadding: Dp = 32.dp,
    val verticalPadding: Dp = 12.dp,
    val sideButtonSize: Dp = 56.dp,
    val sideIconSize: Dp = 22.dp,
    val centerButtonSize: Dp = 72.dp,
    val centerIconSize: Dp = 28.dp,
    val buttonShadowElevation: Dp = 10.dp,
    val buttonTonalElevation: Dp = 4.dp,
    val centerGapWidth: Dp = 72.dp
)

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
    selectedColor: Color,
    unselectedColor: Color,
    iconSize: Dp,
    buttonSize: Dp,
    buttonContainerColor: Color,
    shadowElevation: Dp,
    tonalElevation: Dp,
    labelStyle: TextStyle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentColor = if (selected) selectedColor else unselectedColor
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Surface(
            onClick = onClick,
            shape = CircleShape,
            color = buttonContainerColor,
            shadowElevation = shadowElevation,
            tonalElevation = tonalElevation
        ) {
            Box(
                modifier = Modifier.size(buttonSize),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = label,
                    tint = contentColor,
                    modifier = Modifier.size(iconSize)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, color = contentColor, style = labelStyle)
    }
}
