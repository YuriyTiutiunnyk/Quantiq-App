package com.example.quantiq.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalMotionDurationScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex

@Immutable
data class SpeedDialAction(
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit,
    val enabled: Boolean = true
)

internal val SpeedDialShadowPadding = 8.dp

internal val SpeedDialShadowPaddingKey = SemanticsPropertyKey<Dp>("SpeedDialShadowPadding")
internal var SemanticsPropertyReceiver.speedDialShadowPadding by SpeedDialShadowPaddingKey

@Composable
fun SpeedDialFab(
    actions: List<SpeedDialAction>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    animationProgressOverride: Float? = null,
    primaryContentDescription: String = "Expand actions",
    primaryIcon: ImageVector = Icons.Default.Add
) {
    val motionScale = LocalMotionDurationScale.current
    val reduceMotion = motionScale == 0f
    val animationSpec = tween<Float>(durationMillis = if (reduceMotion) 150 else 220)
    val targetProgress = if (expanded) 1f else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = animationSpec,
        label = "SpeedDialProgress"
    )
    val progress = animationProgressOverride ?: animatedProgress
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 45f else 0f,
        animationSpec = animationSpec,
        label = "SpeedDialRotation"
    )

    Column(
        modifier = modifier
            .graphicsLayer { clip = false }
            .semantics { if (progress == 0f) invisibleToUser() },
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        actions.forEachIndexed { index, action ->
            SpeedDialActionItem(
                action = action,
                index = index,
                progress = progress,
                reduceMotion = reduceMotion
            )
        }
        SpeedDialPrimaryFab(
            rotation = rotation,
            onClick = { onExpandedChange(!expanded) },
            contentDescription = primaryContentDescription,
            icon = primaryIcon
        )
    }
}

@Composable
private fun SpeedDialActionItem(
    action: SpeedDialAction,
    index: Int,
    progress: Float,
    reduceMotion: Boolean
) {
    val actionSize = 48.dp
    val offset = if (reduceMotion) 0.dp else lerp(24.dp, 0.dp, progress)
    val scale = if (reduceMotion) 1f else lerp(0.85f, 1f, progress)
    val alpha = progress
    val offsetPx = with(LocalDensity.current) { offset.toPx() }

    Box(
        modifier = Modifier
            .zIndex(1f)
            .graphicsLayer { clip = false }
            .semantics { speedDialShadowPadding = SpeedDialShadowPadding }
            .testTag("speed_dial_action_host_$index")
            .size(actionSize)
            .padding(SpeedDialShadowPadding)
            .alpha(alpha)
            .graphicsLayer {
                translationY = offsetPx
                scaleX = scale
                scaleY = scale
            }
            .semantics { if (progress == 0f) invisibleToUser() }
    ) {
        SpeedDialActionSurface(
            action = action,
            modifier = Modifier
                .size(actionSize)
                .graphicsLayer { clip = false }
                .testTag("speed_dial_action_content_$index")
        )
    }
}

@Composable
private fun SpeedDialActionSurface(
    action: SpeedDialAction,
    modifier: Modifier = Modifier,
    actionSize: Dp = 48.dp
) {
    Surface(
        modifier = modifier
            .size(actionSize)
            .clickable(enabled = action.enabled, onClick = action.onClick),
        shape = CircleShape,
        tonalElevation = 3.dp,
        shadowElevation = 6.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .padding(PaddingValues(horizontal = 12.dp))
                .widthIn(min = actionSize),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = action.label,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun SpeedDialPrimaryFab(
    rotation: Float,
    onClick: () -> Unit,
    contentDescription: String,
    icon: ImageVector
) {
    val layoutDirection = LocalLayoutDirection.current
    val rotationDirection = if (layoutDirection == LayoutDirection.Ltr) rotation else -rotation
    Surface(
        modifier = Modifier
            .size(56.dp)
            .clickable(onClick = onClick)
            .zIndex(2f),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary,
        tonalElevation = 6.dp,
        shadowElevation = 10.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.rotate(rotationDirection),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
