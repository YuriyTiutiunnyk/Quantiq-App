package com.example.quantiq.ui.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import org.junit.Rule
import org.junit.Test

class ActiveItemActionMenuTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun actionMenuProvidesShadowPaddingDuringAnimation() {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            MaterialTheme {
                var expanded by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) { expanded = true }
                CounterActionMenu(
                    expanded = expanded,
                    onToggle = { expanded = !expanded },
                    onDetails = {},
                    onReset = {},
                    onStep = {},
                    actionButtonSize = 56.dp,
                    actionIconSize = 22.dp,
                    actionButtonShadow = 10.dp,
                    actionButtonTonal = 4.dp,
                    actionShadowInset = 0.dp
                )
            }
        }

        composeRule.mainClock.advanceTimeBy(120)

        val containerBounds = composeRule.onNodeWithTag("active_action_menu_container")
            .fetchSemanticsNode()
            .boundsInRoot
        val buttonBounds = composeRule.onNodeWithTag("active_action_button_edit")
            .fetchSemanticsNode()
            .boundsInRoot
        val paddingPx = with(composeRule.density) { 8.dp.toPx() }

        assert(containerBounds.right - buttonBounds.right >= paddingPx - 0.5f) {
            "Expected end padding for shadow to be at least $paddingPx px."
        }
    }

    @Test
    fun actionMenuOpensAndClosesViaToggle() {
        composeRule.setContent {
            MaterialTheme {
                var expanded by remember { mutableStateOf(false) }
                CounterActionMenu(
                    expanded = expanded,
                    onToggle = { expanded = !expanded },
                    onDetails = {},
                    onReset = {},
                    onStep = {},
                    actionButtonSize = 56.dp,
                    actionIconSize = 22.dp,
                    actionButtonShadow = 10.dp,
                    actionButtonTonal = 4.dp,
                    actionShadowInset = 0.dp
                )
            }
        }

        composeRule.onNodeWithTag("active_action_menu_toggle")
            .performClick()
        composeRule.onNodeWithTag("active_action_button_edit")
            .assertIsDisplayed()

        composeRule.onNodeWithTag("active_action_menu_toggle")
            .performClick()
        composeRule.onNodeWithTag("active_action_button_edit")
            .assertIsNotDisplayed()
    }
}
