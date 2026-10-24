package com.running.app.core.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val AdidasBlack   = Color(0xFF0B0B0B)
val AdidasSurface = Color(0xFF161616)
val AdidasCard    = Color(0xFF1F1F1F)
val AdidasAccent  = Color(0xFFCCFF00)   // neon lime — running energy
val AdidasWhite   = Color(0xFFFFFFFF)
val AdidasGray    = Color(0xFF9CA3AF)

private val RunningColors = darkColorScheme(
    primary = AdidasAccent,
    onPrimary = AdidasBlack,
    background = AdidasBlack,
    surface = AdidasSurface,
    surfaceVariant = AdidasCard,
    onBackground = AdidasWhite,
    onSurface = AdidasWhite,
    onSurfaceVariant = AdidasGray
)

@Composable
fun RunningTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = RunningColors, content = content)
}
