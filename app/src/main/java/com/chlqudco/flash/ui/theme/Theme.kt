package com.chlqudco.flash.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val FlashColorScheme = darkColorScheme(
    primary = TorchYellow,
    secondary = TorchAmber,
    background = Night,
    surface = NightSurface,
    onPrimary = Night,
    onSecondary = Night,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun FlashTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = FlashColorScheme,
        typography = Typography,
        content = content
    )
}
