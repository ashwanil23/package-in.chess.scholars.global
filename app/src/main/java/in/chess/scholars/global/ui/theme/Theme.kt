package `in`.chess.scholars.global.ui.theme

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
    primary = Color(0xFF4ECDC4),
    onPrimary = Color(0xFF003735),
    primaryContainer = Color(0xFF00524F),
    onPrimaryContainer = Color(0xFF70F7EE),
    secondary = Color(0xFFFFD700),
    onSecondary = Color(0xFF3E2E00),
    secondaryContainer = Color(0xFF584300),
    onSecondaryContainer = Color(0xFFFFDF88),
    tertiary = Color(0xFFFF5252),
    onTertiary = Color(0xFF690005),
    tertiaryContainer = Color(0xFF93000A),
    onTertiaryContainer = Color(0xFFFFDAD6),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    onError = Color(0xFF690005),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF0A0A0F),
    onBackground = Color(0xFFE1E3E3),
    surface = Color(0xFF1A1A2E),
    onSurface = Color(0xFFE1E3E3),
    surfaceVariant = Color(0xFF16213E),
    onSurfaceVariant = Color(0xFFBFC9C8),
    outline = Color(0xFF899392),
    inverseOnSurface = Color(0xFF191C1C),
    inverseSurface = Color(0xFFE1E3E3),
    inversePrimary = Color(0xFF006B67),
    surfaceTint = Color(0xFF4ECDC4),
    outlineVariant = Color(0xFF3F4948),
    scrim = Color(0xFF000000)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF006B67),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF70F7EE),
    onPrimaryContainer = Color(0xFF00201E),
    secondary = Color(0xFF725C00),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFDF88),
    onSecondaryContainer = Color(0xFF231B00),
    tertiary = Color(0xFFBB1614),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFDAD6),
    onTertiaryContainer = Color(0xFF410002),
    error = Color(0xFFBA1A1A),
    errorContainer = Color(0xFFFFDAD6),
    onError = Color(0xFFFFFFFF),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFAFDFC),
    onBackground = Color(0xFF191C1C),
    surface = Color(0xFFFAFDFC),
    onSurface = Color(0xFF191C1C),
    surfaceVariant = Color(0xFFDAE5E3),
    onSurfaceVariant = Color(0xFF3F4948),
    outline = Color(0xFF6F7978),
    inverseOnSurface = Color(0xFFEFF1F1),
    inverseSurface = Color(0xFF2E3131),
    inversePrimary = Color(0xFF4CD9D0),
    surfaceTint = Color(0xFF006B67),
    outlineVariant = Color(0xFFBFC9C8),
    scrim = Color(0xFF000000)
)

@Composable
fun ScholarsChessTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
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
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}