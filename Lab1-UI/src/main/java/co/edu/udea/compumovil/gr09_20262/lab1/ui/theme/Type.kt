package co.edu.udea.compumovil.gr09_20262.lab1.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.DeviceFontFamilyName
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

/**
 * Roboto es la tipografía del sistema en Android. Se referencia por su nombre de
 * familia en el dispositivo ("sans-serif" y sus variantes), de modo que no hay que
 * empaquetar archivos .ttf ni depender de fuentes descargables.
 */
val Roboto: FontFamily = FontFamily(
    Font(DeviceFontFamilyName("sans-serif-light"), FontWeight.Light),
    Font(DeviceFontFamilyName("sans-serif"), FontWeight.Normal),
    Font(DeviceFontFamilyName("sans-serif-medium"), FontWeight.Medium),
    Font(DeviceFontFamilyName("sans-serif"), FontWeight.Bold),
)

// Se parte de la tipografía por defecto de Material 3 y solo se cambia la familia a Roboto.
private val default = Typography()

val Typography = Typography(
    displayLarge = default.displayLarge.copy(fontFamily = Roboto),
    displayMedium = default.displayMedium.copy(fontFamily = Roboto),
    displaySmall = default.displaySmall.copy(fontFamily = Roboto),
    headlineLarge = default.headlineLarge.copy(fontFamily = Roboto),
    headlineMedium = default.headlineMedium.copy(fontFamily = Roboto),
    headlineSmall = default.headlineSmall.copy(fontFamily = Roboto),
    titleLarge = default.titleLarge.copy(fontFamily = Roboto),
    titleMedium = default.titleMedium.copy(fontFamily = Roboto),
    titleSmall = default.titleSmall.copy(fontFamily = Roboto),
    bodyLarge = default.bodyLarge.copy(fontFamily = Roboto),
    bodyMedium = default.bodyMedium.copy(fontFamily = Roboto),
    bodySmall = default.bodySmall.copy(fontFamily = Roboto),
    labelLarge = default.labelLarge.copy(fontFamily = Roboto),
    labelMedium = default.labelMedium.copy(fontFamily = Roboto),
    labelSmall = default.labelSmall.copy(fontFamily = Roboto),
)