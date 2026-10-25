package com.airticket.bd.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val NavyPrimary = Color(0xFF0F172A)
val TealAccent = Color(0xFF0D9488)
val TealDark = Color(0xFF0F766E)
val LightBackground = Color(0xFFF8FAFC)
val LightSurface = Color(0xFFFFFFFF)
val SlateBorder = Color(0xFFE2E8F0)

private val LightColorScheme = lightColorScheme(
    primary = NavyPrimary,
    onPrimary = Color.White,
    secondary = TealAccent,
    onSecondary = Color.White,
    background = LightBackground,
    onBackground = Color(0xFF0F172A),
    surface = LightSurface,
    onSurface = Color(0xFF0F172A),
    outline = SlateBorder
)

@Composable
fun AirTicketTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}