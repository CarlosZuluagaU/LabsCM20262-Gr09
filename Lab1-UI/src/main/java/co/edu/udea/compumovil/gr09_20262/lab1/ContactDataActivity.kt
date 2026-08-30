package co.edu.udea.compumovil.gr09_20262.lab1

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.edu.udea.compumovil.gr09_20262.lab1.ui.theme.AuroraBackground
import co.edu.udea.compumovil.gr09_20262.lab1.ui.theme.GlassCard
import co.edu.udea.compumovil.gr09_20262.lab1.ui.theme.LanguageSwitcher
import co.edu.udea.compumovil.gr09_20262.lab1.ui.theme.Labs20262Gr09Theme
import co.edu.udea.compumovil.gr09_20262.lab1.util.LocaleHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.text.Normalizer

private const val TAG = "Lab1"

class ContactDataActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Labs20262Gr09Theme {
                AuroraBackground {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = Color.Transparent,
                    ) { innerPadding ->
                        ContactDataScreen(modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDataScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val isLandscape =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    LaunchedEffect(isLandscape) { scrollState.scrollTo(0) }

    // --- Estado del formulario ---
    var phone by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var country by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { mutableStateOf("") }

    // --- Errores de campos obligatorios ---
    var phoneError by rememberSaveable { mutableStateOf(false) }
    var emailError by rememberSaveable { mutableStateOf<String?>(null) }
    var countryError by rememberSaveable { mutableStateOf(false) }

    val countries = stringArrayResource(R.array.latin_america_countries).toList()

    // Ciudades: lista estática local como base (funciona sin conexión) y, si hay red,
    // se amplía con la API pública api-colombia.com.
    val staticCities = stringArrayResource(R.array.colombia_cities).toList()
    var cityOptions by remember { mutableStateOf(staticCities) }
    LaunchedEffect(Unit) {
        val remote = fetchColombiaCities()
        if (remote.isNotEmpty()) {
            cityOptions = (staticCities + remote).distinct().sortedBy { it.normalizeForSearch() }
        }
    }

    val phoneField: @Composable () -> Unit = {
        OutlinedTextField(
            value = phone,
            onValueChange = {
                phone = it
                if (it.isNotBlank()) phoneError = false
            },
            label = { Text(stringResource(R.string.phone_label) + " *") },
            singleLine = true,
            isError = phoneError,
            supportingText = { if (phoneError) Text(stringResource(R.string.required_field)) },
            leadingIcon = { Icon(painterResource(R.drawable.ic_phone), contentDescription = null) },
            // Teclado telefónico. La acción del IME es "Siguiente".
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
    }

    val addressField: @Composable () -> Unit = {
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text(stringResource(R.string.address_label)) },
            singleLine = true,
            leadingIcon = { Icon(painterResource(R.drawable.ic_home), contentDescription = null) },
            // Teclado normal, sin sugerencias ni autocorrección (textNoSuggestions). IME: "Siguiente".
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Sentences,
                autoCorrectEnabled = false,
                imeAction = ImeAction.Next,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
    }

    val emailField: @Composable () -> Unit = {
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                if (emailError != null) emailError = null
            },
            label = { Text(stringResource(R.string.email_label) + " *") },
            singleLine = true,
            isError = emailError != null,
            supportingText = { emailError?.let { Text(it) } },
            leadingIcon = { Icon(painterResource(R.drawable.ic_email), contentDescription = null) },
            // Tipo de dato email. IME: "Siguiente".
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                capitalization = KeyboardCapitalization.None,
                autoCorrectEnabled = false,
                imeAction = ImeAction.Next,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
    }

    val countryField: @Composable () -> Unit = {
        AutoCompleteField(
            label = stringResource(R.string.country_label),
            required = true,
            value = country,
            onValueChange = {
                country = it
                if (it.isNotBlank()) countryError = false
            },
            options = countries,
            leadingIcon = painterResource(R.drawable.ic_place),
            imeAction = ImeAction.Next,
            isError = countryError,
            errorText = stringResource(R.string.required_field),
        )
    }

    val cityField: @Composable () -> Unit = {
        AutoCompleteField(
            label = stringResource(R.string.city_label),
            required = false,
            value = city,
            onValueChange = { city = it },
            options = cityOptions,
            leadingIcon = painterResource(R.drawable.ic_search),
            // Último campo de texto -> el IME muestra "Listo".
            imeAction = ImeAction.Done,
        )
    }

    val onSave: () -> Unit = {
        phoneError = phone.isBlank()
        emailError = when {
            email.isBlank() -> context.getString(R.string.required_field)
            !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() ->
                context.getString(R.string.invalid_email)
            else -> null
        }
        countryError = country.isBlank()

        val valid = !phoneError && emailError == null && !countryError
        if (valid) {
            // Datos obligatorios OK -> se escriben en Logcat los datos de contacto.
            Log.d(TAG, "")
            Log.d(TAG, "Información de contacto:")
            Log.d(TAG, "Teléfono: ${phone.trim()}")
            if (address.isNotBlank()) Log.d(TAG, "Dirección: ${address.trim()}")
            Log.d(TAG, "Email: ${email.trim()}")
            Log.d(TAG, "País: ${country.trim()}")
            if (city.isNotBlank()) Log.d(TAG, "Ciudad: ${city.trim()}")
            Toast.makeText(context, context.getString(R.string.data_saved), Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.check_required_fields),
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            // El área de scroll se encoge con el teclado -> el campo enfocado nunca queda tapado.
            .imePadding()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = if (isLandscape) {
            Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        } else {
            Arrangement.spacedBy(16.dp)
        },
    ) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 720.dp),
        ) {
            Text(
                text = stringResource(R.string.contact_data_title),
                style = MaterialTheme.typography.headlineSmall,
            )
            LanguageSwitcher()

            if (isLandscape) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        phoneField()
                        addressField()
                        emailField()
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        countryField()
                        cityField()
                    }
                }
            } else {
                phoneField()
                addressField()
                emailField()
                countryField()
                cityField()
            }

            Button(onClick = onSave, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.save))
            }
        }
    }
}

/**
 * Campo tipo Autocomplete (equivalente Compose de AutoCompleteTextView):
 * un [OutlinedTextField] editable dentro de un [ExposedDropdownMenuBox] que
 * filtra [options] según lo que se escribe (sin distinguir mayúsculas ni tildes).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AutoCompleteField(
    label: String,
    required: Boolean,
    value: String,
    onValueChange: (String) -> Unit,
    options: List<String>,
    modifier: Modifier = Modifier,
    leadingIcon: Painter? = null,
    imeAction: ImeAction = ImeAction.Default,
    isError: Boolean = false,
    errorText: String? = null,
) {
    var expanded by remember { mutableStateOf(false) }
    val filtered = remember(value, options) {
        if (value.isBlank()) {
            options
        } else {
            val query = value.normalizeForSearch()
            options.filter { it.normalizeForSearch().contains(query) }
        }
    }
    val menuOpen = expanded && filtered.isNotEmpty()

    ExposedDropdownMenuBox(
        expanded = menuOpen,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                onValueChange(it)
                expanded = true
            },
            label = { Text(if (required) "$label *" else label) },
            singleLine = true,
            isError = isError,
            supportingText = { if (isError && errorText != null) Text(errorText) },
            leadingIcon = leadingIcon?.let { p -> { Icon(p, contentDescription = null) } },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuOpen) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Words,
                autoCorrectEnabled = false,
                imeAction = imeAction,
            ),
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                .fillMaxWidth(),
        )
        ExposedDropdownMenu(
            expanded = menuOpen,
            onDismissRequest = { expanded = false },
        ) {
            filtered.take(50).forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

/** Normaliza texto para buscar: quita tildes/diacríticos y pasa a minúsculas. */
private fun String.normalizeForSearch(): String =
    Normalizer.normalize(this, Normalizer.Form.NFD)
        .replace(Regex("\\p{Mn}+"), "")
        .lowercase()

/**
 * API para ampliar las ciudades: **api-colombia.com** (proyecto abierto, sin API key).
 * `GET https://api-colombia.com/api/v1/City` devuelve todas las ciudades de Colombia.
 * Si falla (sin red, timeout, etc.) se devuelve lista vacía y la pantalla se queda
 * con la lista estática local.
 */
private suspend fun fetchColombiaCities(): List<String> = withContext(Dispatchers.IO) {
    runCatching {
        val connection = (URL(COLOMBIA_CITIES_API_URL).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 8_000
            readTimeout = 8_000
        }
        val body = connection.inputStream.bufferedReader().use { it.readText() }
        connection.disconnect()

        val array = JSONArray(body)
        buildList {
            for (i in 0 until array.length()) {
                array.optJSONObject(i)?.optString("name")
                    ?.takeIf { it.isNotBlank() }
                    ?.let { add(it) }
            }
        }
    }.getOrElse { emptyList() }
}

private const val COLOMBIA_CITIES_API_URL = "https://api-colombia.com/api/v1/City"

@Preview(name = "Portrait", showBackground = true)
@Composable
fun ContactDataScreenPortraitPreview() {
    Labs20262Gr09Theme {
        ContactDataScreen()
    }
}

@Preview(name = "Landscape", showBackground = true, widthDp = 720, heightDp = 360)
@Composable
fun ContactDataScreenLandscapePreview() {
    Labs20262Gr09Theme {
        ContactDataScreen()
    }
}
