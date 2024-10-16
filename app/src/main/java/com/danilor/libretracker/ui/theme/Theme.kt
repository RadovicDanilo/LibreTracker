package com.danilor.libretracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun LibreTrackerTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        darkColorScheme(
            primary = LightBluePrimary,
            secondary = LightBlueSecondary,
            background = Color(0xFF121212),
            surface = Color(0xFF1E1E1E)
        )
    } else {
        lightColorScheme(
            primary = BluePrimary,
            secondary = BlueSecondary,
            background = Color(0xFFFFFFFF),
            surface = Color(0xFFF5F5F5)
        )
    }
    MaterialTheme(content = content, colorScheme = colors)
}