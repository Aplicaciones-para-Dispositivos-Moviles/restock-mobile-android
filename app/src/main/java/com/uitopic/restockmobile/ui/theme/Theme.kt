package com.uitopic.restockmobile.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = RestockPrimary,
    onPrimary = RestockOnPrimary,
    secondary = RestockSecondary,
    onSecondary = RestockOnSecondary,
    background = Color(0xFF0F1115),
    onBackground = Color(0xFFE7E9ED),
    surface = Color(0xFF151922),
    onSurface = Color(0xFFE7E9ED),
    surfaceVariant = Color(0xFF1E2430),
    onSurfaceVariant = Color(0xFFB6BEC9),
    outline = Color(0xFF303845),
    error = RestockError,
    onError = RestockOnError,
    primaryContainer = Color(0xFF1B5E20),               // contenedor verde oscuro
    onPrimaryContainer = Color(0xFFCDEDCB)
)

private val LightColorScheme = lightColorScheme(
    primary = RestockPrimary,
    onPrimary = RestockOnPrimary,
    secondary = RestockSecondary,
    onSecondary = RestockOnSecondary,
    background = RestockBackground,
    onBackground = RestockOnBackground,
    surface = RestockSurface,
    onSurface = RestockOnSurface,
    surfaceVariant = RestockSurfaceVariant,
    onSurfaceVariant = RestockOnSurfaceVar,
    outline = RestockOutline,
    error = RestockError,
    onError = RestockOnError,
    primaryContainer = RestockPrimaryContainer,          // ← nuevo
    onPrimaryContainer = RestockOnPrimaryContainer
)

@Composable
fun RestockmobileTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // desactiva dinámico para mantener marca consistente
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,       // tu Typography actual
        shapes = RestockShapes,        // ← importante para textfields/cards redondeadas
        content = content
    )
}
