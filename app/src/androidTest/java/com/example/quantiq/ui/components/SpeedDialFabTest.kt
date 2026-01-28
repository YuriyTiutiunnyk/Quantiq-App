package com.example.quantiq.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class SpeedDialFabTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun speedDialShadowPaddingAllowsShadowDuringAnimation() {
        composeRule.setContent {
            MaterialTheme {
                SpeedDialFab(
                    actions = listOf(
                        SpeedDialAction(
                            icon = Icons.Default.Add,
                            label = "Add",
                            onClick = {}
                        )
                    ),
                    expanded = true,
                    onExpandedChange = {},
                    animationProgressOverride = 0.5f
                )
            }
        }

        val hostBounds = composeRule.onNodeWithTag("speed_dial_action_host_0")
            .fetchSemanticsNode()
            .boundsInRoot
        val contentBounds = composeRule.onNodeWithTag("speed_dial_action_content_0")
            .fetchSemanticsNode()
            .boundsInRoot
        val paddingPx = with(composeRule.density) { SpeedDialShadowPadding.toPx() }

        assert(hostBounds.width >= contentBounds.width + paddingPx * 2f - 0.5f) {
            "Expected host width ${hostBounds.width} to include shadow padding ${paddingPx}."
        }
        assert(hostBounds.height >= contentBounds.height + paddingPx * 2f - 0.5f) {
            "Expected host height ${hostBounds.height} to include shadow padding ${paddingPx}."
        }
    }

    @Test
    fun speedDialExpandsAndCollapses() {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            MaterialTheme {
                var expanded by remember { mutableStateOf(false) }
                SpeedDialFab(
                    actions = listOf(
                        SpeedDialAction(
                            icon = Icons.Default.Add,
                            label = "Add",
                            onClick = {}
                        )
                    ),
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    primaryContentDescription = "Toggle speed dial"
                )
            }
        }

        composeRule.onNodeWithContentDescription("Toggle speed dial")
            .performClick()
        composeRule.mainClock.advanceTimeBy(300)
        composeRule.onNodeWithContentDescription("Add")
            .assertIsDisplayed()

        composeRule.onNodeWithContentDescription("Toggle speed dial")
            .performClick()
        composeRule.mainClock.advanceTimeBy(300)
        composeRule.onNodeWithContentDescription("Add")
            .assertIsNotDisplayed()
    }
}
