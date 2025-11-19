package com.example.taxidrivermobile.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = Color.White,
    primaryContainer = BlueLight,
    secondary = BlueSecondary,
    background = GrayBackground,
    surface = GrayCard,
    onBackground = Color(0xFF1F2937),
    onSurface = Color(0xFF1F2937),
    error = Red
)

@Composable
fun TaxiDriverMobileTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}