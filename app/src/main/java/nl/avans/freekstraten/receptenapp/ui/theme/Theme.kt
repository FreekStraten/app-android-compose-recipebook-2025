package nl.avans.freekstraten.receptenapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Brand,
    onPrimary = Color.White,
    primaryContainer = BrandContainerLight,
    onPrimaryContainer = Color(0xFF3C0805),

    secondary = SecondaryLight,
    onSecondary = Color.White,

    tertiary = TertiaryLight,
    onTertiary = Color.White,

    background = BackgroundLight,
    onBackground = Color(0xFF1C1B1F),

    surface = SurfaceLight,
    onSurface = Color(0xFF1C1B1F)
)

private val DarkColorScheme = darkColorScheme(
    primary = Brand,
    onPrimary = Color.White,
    primaryContainer = BrandContainerDark,
    onPrimaryContainer = Color(0xFFFFDAD5),

    secondary = SecondaryDark,
    onSecondary = Color(0xFF201A18),

    tertiary = TertiaryDark,
    onTertiary = Color(0xFF3B2416),

    background = BackgroundDark,
    onBackground = Color(0xFFE6E1E5),

    surface = SurfaceDark,
    onSurface = Color(0xFFE6E1E5)
)

/**
 * AppTheme
 * - Volgt systeem (dark/light)
 * - Gebruikt Dynamic Color op Android 12+ (val terug op onze schema's)
 */
@Composable
fun AppTheme(
    forceDark: Boolean? = null,           // null = volg systeem; anders true/false voor in-app toggle
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val isDark = forceDark ?: isSystemInDarkTheme()

    val colorScheme =
        if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        } else {
            if (isDark) DarkColorScheme else LightColorScheme
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
