package com.example.quantiq.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateBottomPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Renders a curved/convex styled bottom navigation bar with a raised center action.
 *
 * TODO: Confirm bar height, corner radius, notch radius/depth, icon size, typography, and colors
 *  from navbar.fig/elements.fig/download.fig exports once available.
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
    val barColor = MaterialTheme.colorScheme.surface
    val selectedColor = MaterialTheme.colorScheme.primary
    val unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
    val centerSelectedColor = MaterialTheme.colorScheme.primaryContainer
    val centerUnselectedColor = MaterialTheme.colorScheme.surface
    val centerSelectedContent = MaterialTheme.colorScheme.onPrimaryContainer
    val centerUnselectedContent = MaterialTheme.colorScheme.onSurface
    val labelStyle = MaterialTheme.typography.labelMedium
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clipToBounds(false)
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
    ) {
        ConvexBottomBarBackground(
            barColor = barColor,
            tokens = tokens,
            bottomInset = bottomInset
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(tokens.barHeight)
                .align(Alignment.BottomCenter)
                .padding(
                    start = tokens.horizontalPadding,
                    end = tokens.horizontalPadding,
                    top = tokens.verticalPadding,
                    bottom = tokens.verticalPadding + bottomInset
                ),
            verticalAlignment = Alignment.CenterVertically
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
                    iconSize = tokens.iconSize,
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
                    iconSize = tokens.iconSize,
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
                    .align(Alignment.TopCenter)
                    .offset(y = -(tokens.centerButtonSize / 2 + tokens.notchDepth / 2)),
                shape = CircleShape,
                color = if (isSelected) centerSelectedColor else centerUnselectedColor,
                shadowElevation = tokens.centerButtonShadowElevation,
                tonalElevation = tokens.centerButtonTonalElevation
            ) {
                ElevatedCircleButton(
                    onClick = { onNavigate(item.route) },
                    size = tokens.centerButtonSize,
                    content = {
                        val contentColor = if (isSelected) {
                            centerSelectedContent
                        } else {
                            centerUnselectedContent
                        }
                        Icon(
                            item.icon,
                            contentDescription = item.label,
                            tint = contentColor,
                            modifier = Modifier.size(tokens.centerIconSize)
                        )
                    }
                )
            }
        }
    }
}

/**
 * Visual tokens for the convex bottom bar.
 */
@Immutable
data class ConvexBottomBarTokens(
    val barHeight: Dp = 72.dp,
    val horizontalPadding: Dp = 32.dp,
    val verticalPadding: Dp = 12.dp,
    val cornerRadius: Dp = 28.dp,
    val notchRadius: Dp = 40.dp,
    val notchDepth: Dp = 24.dp,
    val shadowBlurRadius: Dp = 20.dp,
    val shadowOffsetY: Dp = 6.dp,
    val iconSize: Dp = 22.dp,
    val centerButtonSize: Dp = 72.dp,
    val centerIconSize: Dp = 28.dp,
    val centerButtonShadowElevation: Dp = 10.dp,
    val centerButtonTonalElevation: Dp = 6.dp,
    val centerGapWidth: Dp = 96.dp
)

/**
 * Draws the curved bottom bar background with a concave notch for the center button.
 */
@Composable
private fun ConvexBottomBarBackground(
    barColor: Color,
    tokens: ConvexBottomBarTokens,
    bottomInset: Dp
) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(tokens.barHeight + bottomInset)
    ) {
        val barHeightPx = size.height
        val cornerRadiusPx = tokens.cornerRadius.toPx()
        val notchRadiusPx = tokens.notchRadius.toPx()
        val notchDepthPx = tokens.notchDepth.toPx()
        val notchCenter = Offset(
            x = size.width / 2f,
            y = -notchRadiusPx + notchDepthPx
        )
        val barRect = Rect(0f, 0f, size.width, barHeightPx)
        val barPath = Path().apply {
            fillType = PathFillType.EvenOdd
            addRoundRect(
                RoundRect(
                    rect = barRect,
                    cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
                )
            )
            addOval(
                Rect(
                    left = notchCenter.x - notchRadiusPx,
                    top = notchCenter.y - notchRadiusPx,
                    right = notchCenter.x + notchRadiusPx,
                    bottom = notchCenter.y + notchRadiusPx
                )
            )
        }

        drawIntoCanvas { canvas ->
            val shadowPaint = Paint().apply {
                color = barColor
                val frameworkPaint = asFrameworkPaint()
                frameworkPaint.setShadowLayer(
                    tokens.shadowBlurRadius.toPx(),
                    0f,
                    tokens.shadowOffsetY.toPx(),
                    Color.Black.copy(alpha = 0.18f).toArgb()
                )
            }
            canvas.drawPath(barPath, shadowPaint)
        }
        drawPath(path = barPath, color = barColor, style = Fill)
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
    selectedColor: Color,
    unselectedColor: Color,
    iconSize: Dp,
    labelStyle: TextStyle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentColor = if (selected) selectedColor else unselectedColor
    TextButton(
        onClick = onClick,
        contentPadding = PaddingValues(0.dp),
        modifier = modifier
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(iconSize)
            )
            Text(text = label, color = contentColor, style = labelStyle)
        }
    }
}
