package com.sunshineloan.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val SunshineColorScheme = lightColorScheme(
    primary = SunshineOrangePrimary,
    onPrimary = SunshineWhite,
    primaryContainer = SunshineOrangeContainer,
    onPrimaryContainer = SunshineOrangeDark,
    secondary = SunshineOrangeDark,
    onSecondary = SunshineWhite,
    secondaryContainer = SunshineOrangeSurface,
    onSecondaryContainer = SunshineOrangeDark,
    background = SunshineBackground,
    onBackground = SunshineTextPrimary,
    surface = SunshineSurface,
    onSurface = SunshineTextPrimary,
    surfaceVariant = SunshineSurfaceVariant,
    onSurfaceVariant = SunshineTextSecondary,
    outline = SunshineBorder,
    outlineVariant = SunshineBorderLight,
    error = SunshineError,
    onError = SunshineWhite,
    errorContainer = SunshineErrorContainer,
    onErrorContainer = SunshineError
)

@Composable
fun SunshineLoanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Sunshine Loan maintains its signature clean orange-and-white theme
    MaterialTheme(
        colorScheme = SunshineColorScheme,
        typography = Typography,
        content = content
    )
}
