package com.runiq.ui.theme

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
import com.runiq.ui.theme.RunIQShapes

private val DarkColorScheme = darkColorScheme(
    primary = RunIQDarkPrimary,
    onPrimary = RunIQDarkOnPrimary,
    secondary = RunIQDarkSecondary,
    onSecondary = RunIQDarkOnSecondary,
    background = RunIQDarkBackground,
    onBackground = RunIQDarkOnBackground,
    surface = RunIQDarkSurface,
    onSurface = RunIQDarkOnSurface,
    error = RunIQError
)

private val LightColorScheme = lightColorScheme(
    primary = RunIQPrimary,
    onPrimary = RunIQOnPrimary,
    secondary = RunIQSecondary,
    onSecondary = RunIQOnSecondary,
    background = RunIQBackground,
    onBackground = RunIQOnBackground,
    surface = RunIQSurface,
    onSurface = RunIQOnSurface,
    error = RunIQError
)

@Composable
fun RunIQTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
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
        typography = Typography,
        shapes = RunIQShapes,
        content = content
    )
}