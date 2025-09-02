package com.runiq.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Light color scheme for RunIQ app
 */
private val LightColorScheme = lightColorScheme(
    primary = RunIQColors.Primary,
    onPrimary = RunIQColors.OnPrimary,
    primaryContainer = RunIQColors.PrimaryContainer,
    onPrimaryContainer = RunIQColors.OnPrimaryContainer,
    secondary = RunIQColors.Secondary,
    onSecondary = RunIQColors.OnSecondary,
    secondaryContainer = RunIQColors.SecondaryContainer,
    onSecondaryContainer = RunIQColors.OnSecondaryContainer,
    tertiary = RunIQColors.Tertiary,
    onTertiary = RunIQColors.OnTertiary,
    tertiaryContainer = RunIQColors.TertiaryContainer,
    onTertiaryContainer = RunIQColors.OnTertiaryContainer,
    error = RunIQColors.Error,
    onError = RunIQColors.OnError,
    errorContainer = RunIQColors.ErrorContainer,
    onErrorContainer = RunIQColors.OnErrorContainer,
    background = RunIQColors.Background,
    onBackground = RunIQColors.OnBackground,
    surface = RunIQColors.Surface,
    onSurface = RunIQColors.OnSurface,
    surfaceVariant = RunIQColors.SurfaceVariant,
    onSurfaceVariant = RunIQColors.OnSurfaceVariant,
    outline = RunIQColors.Outline,
    outlineVariant = RunIQColors.OutlineVariant
)

/**
 * Dark color scheme for RunIQ app
 */
private val DarkColorScheme = darkColorScheme(
    primary = RunIQColors.DarkPrimary,
    onPrimary = RunIQColors.DarkOnPrimary,
    primaryContainer = RunIQColors.DarkPrimaryContainer,
    onPrimaryContainer = RunIQColors.DarkOnPrimaryContainer,
    secondary = RunIQColors.DarkSecondary,
    onSecondary = RunIQColors.DarkOnSecondary,
    secondaryContainer = RunIQColors.DarkSecondaryContainer,
    onSecondaryContainer = RunIQColors.DarkOnSecondaryContainer,
    tertiary = RunIQColors.DarkTertiary,
    onTertiary = RunIQColors.DarkOnTertiary,
    tertiaryContainer = RunIQColors.DarkTertiaryContainer,
    onTertiaryContainer = RunIQColors.DarkOnTertiaryContainer,
    error = RunIQColors.DarkError,
    onError = RunIQColors.DarkOnError,
    errorContainer = RunIQColors.DarkErrorContainer,
    onErrorContainer = RunIQColors.DarkOnErrorContainer,
    background = RunIQColors.DarkBackground,
    onBackground = RunIQColors.DarkOnBackground,
    surface = RunIQColors.DarkSurface,
    onSurface = RunIQColors.DarkOnSurface,
    surfaceVariant = RunIQColors.DarkSurfaceVariant,
    onSurfaceVariant = RunIQColors.DarkOnSurfaceVariant,
    outline = RunIQColors.DarkOutline,
    outlineVariant = RunIQColors.DarkOutlineVariant
)

/**
 * RunIQ theme composable that applies Material3 theming with dynamic color support
 */
@Composable
fun RunIQTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = RunIQTypography,
        shapes = RunIQShapes,
        content = content
    )
}