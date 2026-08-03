package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = BentoPrimary,
    secondary = BentoAccentLight,
    tertiary = BentoSuccess,
    background = BentoBackground,
    surface = BentoSurface,
    onPrimary = Color.White,
    onSecondary = BentoPrimaryDark,
    onBackground = BentoTextPrimary,
    onSurface = BentoTextPrimary,
    surfaceVariant = BentoContainerSecondary,
    outline = BentoBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = BentoAccentLight,
    secondary = BentoPrimary,
    tertiary = BentoSuccess,
    background = Color(0xFF1D1B1E),
    surface = Color(0xFF2B282F),
    onPrimary = BentoPrimaryDark,
    onSecondary = Color.White,
    onBackground = Color(0xFFFDF8F6),
    onSurface = Color(0xFFFDF8F6),
    surfaceVariant = Color(0xFF36343B),
    outline = Color(0xFF49454F)
)

@Composable
fun PRMSMedicalTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    PRMSMedicalTheme(darkTheme = darkTheme, content = content)
}

