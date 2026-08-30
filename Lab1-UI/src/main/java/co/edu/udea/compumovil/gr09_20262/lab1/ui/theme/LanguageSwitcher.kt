package co.edu.udea.compumovil.gr09_20262.lab1.ui.theme

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import co.edu.udea.compumovil.gr09_20262.lab1.util.LocaleHelper

/**
 * Chips ES / EN / FR para elegir el idioma de la app. Al seleccionar uno distinto
 * al actual, se guarda la preferencia y se recrea la Activity para aplicar el cambio.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSwitcher(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val currentLanguage = LocaleHelper.getLanguage(context)

    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        LocaleHelper.SUPPORTED_LANGUAGES.forEach { language ->
            FilterChip(
                selected = language == currentLanguage,
                onClick = {
                    if (language != currentLanguage) {
                        LocaleHelper.setLanguage(context, language)
                        (context as? Activity)?.recreate()
                    }
                },
                label = { Text(language.uppercase()) },
            )
        }
    }
}
