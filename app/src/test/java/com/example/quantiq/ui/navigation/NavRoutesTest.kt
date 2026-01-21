package com.example.quantiq.ui.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for route builders in [NavRoutes].
 */
class NavRoutesTest {
    @Test
    fun `counter details route includes id`() {
        assertEquals("details/42", NavRoutes.counterDetails(42L))
    }

    @Test
    fun `guideline details route includes id`() {
        assertEquals("guideline/7", NavRoutes.guidelineDetails(7))
    }

    @Test
    fun `notification details route includes id`() {
        assertEquals("notification_details/5", NavRoutes.notificationDetails(5L))
    }
}
