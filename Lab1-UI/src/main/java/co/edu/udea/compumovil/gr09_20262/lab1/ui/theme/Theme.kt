package co.edu.udea.compumovil.gr09_20262.lab1.ui.theme

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

// Esquema de color Material 3 con identidad "Aurora".
private val AuroraLightColors = lightColorScheme(
    primary = AuroraIndigo,
    onPrimary = Color.White,
    secondary = AuroraTeal,
    onSecondary = Color(0xFF04241E),
    tertiary = AuroraPink,
    onTertiary = Color.White,
    background = AuroraMist,
    onBackground = Color(0xFF1B1B2F),
    surface = Color.White,
    onSurface = Color(0xFF1B1B2F),
    surfaceVariant = Color(0xFFE7E4F7),
    onSurfaceVariant = Color(0xFF474357),
    outline = Color(0xFF7A7690),
)

private val AuroraDarkColors = darkColorScheme(
    primary = AuroraTeal,
    onPrimary = Color(0xFF04241E),
    secondary = AuroraBlue,
    onSecondary = Color(0xFF001A3D),
    tertiary = AuroraViolet,
    onTertiary = Color(0xFF1E0033),
    background = AuroraNight,
    onBackground = Color(0xFFE7E6F5),
    surface = AuroraNightAlt,
    onSurface = Color(0xFFE7E6F5),
    surfaceVariant = Color(0xFF2A2F52),
    onSurfaceVariant = Color(0xFFC5C3DE),
    outline = Color(0xFF8C8AA8),
)

@Composable
fun Labs20262Gr09Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Se desactiva por defecto el color dinámico para conservar la identidad "Aurora".
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> AuroraDarkColors
        else -> AuroraLightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
