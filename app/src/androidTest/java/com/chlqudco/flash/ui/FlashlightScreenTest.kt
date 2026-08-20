package com.chlqudco.flash.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.chlqudco.flash.FlashlightStatus
import com.chlqudco.flash.FlashlightUiState
import com.chlqudco.flash.ui.theme.FlashTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class FlashlightScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun privacyOptions_whenRequired_isDisplayedAndClickable() {
        var clicked = false

        composeRule.setContent {
            FlashTheme {
                FlashlightScreen(
                    state = FlashlightUiState(status = FlashlightStatus.OFF),
                    onToggle = {},
                    onOpenSettings = {},
                    canRequestAds = false,
                    showPrivacyOptions = true,
                    onOpenPrivacyOptions = { clicked = true },
                )
            }
        }

        composeRule.onNodeWithText("개인정보 옵션")
            .assertIsDisplayed()
            .performClick()

        composeRule.runOnIdle {
            assertTrue(clicked)
        }
    }

    @Test
    fun privacyOptions_whenNotRequired_isHidden() {
        composeRule.setContent {
            FlashTheme {
                FlashlightScreen(
                    state = FlashlightUiState(status = FlashlightStatus.OFF),
                    onToggle = {},
                    onOpenSettings = {},
                    canRequestAds = false,
                    showPrivacyOptions = false,
                    onOpenPrivacyOptions = {},
                )
            }
        }

        composeRule.onNodeWithText("개인정보 옵션").assertDoesNotExist()
    }
}
