package com.handwritinggrader.ui.theme

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Blue80,
    onPrimary = Blue10,
    primaryContainer = Color(0xFF2A4A6A),
    onPrimaryContainer = Color(0xFFD4E3F5),
    secondary = BlueGrey80,
    onSecondary = Color(0xFF2C3140),
    secondaryContainer = Color(0xFF3D4460),
    onSecondaryContainer = Color(0xFFDCE2F4),
    tertiary = Teal80,
    onTertiary = Color(0xFF1A3A3A),
    tertiaryContainer = Color(0xFF2A5A5A),
    onTertiaryContainer = Color(0xFFC8EDED),
    surface = SurfaceDark,
    onSurface = Color(0xFFE2E2E9),
    surfaceVariant = Color(0xFF2D3038),
    onSurfaceVariant = Color(0xFFC4C6D0),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    outline = Color(0xFF8E9099)
)

private val LightColorScheme = lightColorScheme(
    primary = Blue40,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD4E3F5),
    onPrimaryContainer = Blue10,
    secondary = BlueGrey40,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0E6F0),
    onSecondaryContainer = Color(0xFF1D2540),
    tertiary = Teal40,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFC8EDED),
    onTertiaryContainer = Teal10,
    surface = SurfaceLight,
    onSurface = Color(0xFF1A1C20),
    surfaceVariant = Color(0xFFE7E8F0),
    onSurfaceVariant = Color(0xFF444750),
    error = WrongRed,
    onError = Color.White,
    errorContainer = WrongRedLight,
    onErrorContainer = Color(0xFF410002),
    outline = Color(0xFF74777F)
)

@Composable
fun HandwritingGraderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
