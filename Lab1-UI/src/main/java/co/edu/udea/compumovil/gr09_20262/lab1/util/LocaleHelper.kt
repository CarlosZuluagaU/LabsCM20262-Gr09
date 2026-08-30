package co.edu.udea.compumovil.gr09_20262.lab1.util

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

/**
 * Permite elegir el idioma de la app desde la propia UI, en vez de depender del
 * idioma del dispositivo. El idioma elegido se guarda en SharedPreferences y se
 * aplica envolviendo el Context de cada Activity en [attachBaseContext].
 */
object LocaleHelper {
    val SUPPORTED_LANGUAGES = listOf("es", "en", "fr")
    private const val DEFAULT_LANGUAGE = "es"
    private const val PREFS_NAME = "settings"
    private const val KEY_LANGUAGE = "app_language"

    fun getLanguage(context: Context): String {
        val stored = prefs(context).getString(KEY_LANGUAGE, null)
        if (stored in SUPPORTED_LANGUAGES) return stored!!
        val systemLanguage = Locale.getDefault().language
        return if (systemLanguage in SUPPORTED_LANGUAGES) systemLanguage else DEFAULT_LANGUAGE
    }

    fun setLanguage(context: Context, language: String) {
        prefs(context).edit().putString(KEY_LANGUAGE, language).apply()
    }

    /** Envuelve [context] para que sus recursos (strings, arrays) usen el idioma elegido. */
    fun wrap(context: Context): Context {
        val locale = Locale.forLanguageTag(getLanguage(context))
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
