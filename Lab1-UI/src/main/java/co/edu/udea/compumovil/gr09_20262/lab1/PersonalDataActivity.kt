package co.edu.udea.compumovil.gr09_20262.lab1

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.edu.udea.compumovil.gr09_20262.lab1.ui.theme.AuroraBackground
import co.edu.udea.compumovil.gr09_20262.lab1.ui.theme.GlassCard
import co.edu.udea.compumovil.gr09_20262.lab1.ui.theme.LanguageSwitcher
import co.edu.udea.compumovil.gr09_20262.lab1.ui.theme.Labs20262Gr09Theme
import co.edu.udea.compumovil.gr09_20262.lab1.util.LocaleHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale
import java.util.TimeZone

private const val TAG = "Lab1"

class PersonalDataActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Labs20262Gr09Theme {
                AuroraBackground {
                    // Scaffold transparente para que el fondo "aurora" se vea a través.
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = Color.Transparent,
                    ) { innerPadding ->
                        PersonalDataScreen(modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalDataScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val isLandscape =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Al cambiar de orientación se vuelve al inicio para no dejar el título fuera de vista.
    LaunchedEffect(isLandscape) { scrollState.scrollTo(0) }

    // --- Estado del formulario (rememberSaveable -> sobrevive el giro de pantalla) ---
    var firstNames by rememberSaveable { mutableStateOf("") }
    var lastNames by rememberSaveable { mutableStateOf("") }
    // Índice del sexo seleccionado: 0 = masculino, 1 = femenino, null = sin elegir.
    var sex by rememberSaveable { mutableStateOf<Int?>(null) }
    var birthDateMillis by rememberSaveable { mutableStateOf<Long?>(null) }
    var educationLevel by rememberSaveable { mutableStateOf<String?>(null) }

    // --- Estado de errores (campos obligatorios) ---
    var firstNamesError by rememberSaveable { mutableStateOf(false) }
    var lastNamesError by rememberSaveable { mutableStateOf(false) }
    var birthDateError by rememberSaveable { mutableStateOf(false) }

    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var educationExpanded by remember { mutableStateOf(false) }

    val sexOptions = listOf(
        stringResource(R.string.sex_male),
        stringResource(R.string.sex_female),
    )
    val educationOptions = listOf(
        stringResource(R.string.education_primary),
        stringResource(R.string.education_secondary),
        stringResource(R.string.education_technical),
        stringResource(R.string.education_university),
        stringResource(R.string.education_postgraduate),
    )

    // Teclado: normal (Text), mayúscula inicial de cada palabra y sin sugerencias/autocorrección.
    // Equivale a android:inputType="textCapWords|textNoSuggestions" en XML.
    val nameKeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Text,
        capitalization = KeyboardCapitalization.Words,
        autoCorrectEnabled = false,
        imeAction = ImeAction.Next,
    )

    val dateText = birthDateMillis?.let { formatDate(it) }
        ?: stringResource(R.string.date_not_selected)

    // ---- Bloques reutilizables por ambas orientaciones ----
    val namesBlock: @Composable ColumnScope.() -> Unit = {
        NameField(
            label = stringResource(R.string.first_names_label),
            value = firstNames,
            isError = firstNamesError,
            keyboardOptions = nameKeyboardOptions,
            onValueChange = {
                firstNames = it
                if (it.isNotBlank()) firstNamesError = false
            },
        )
        NameField(
            label = stringResource(R.string.last_names_label),
            value = lastNames,
            isError = lastNamesError,
            keyboardOptions = nameKeyboardOptions.copy(imeAction = ImeAction.Done),
            onValueChange = {
                lastNames = it
                if (it.isNotBlank()) lastNamesError = false
            },
        )
    }

    val sexBlock: @Composable ColumnScope.() -> Unit = {
        SexField(
            label = stringResource(R.string.sex_label),
            options = sexOptions,
            selectedIndex = sex,
            onSelect = { sex = it },
        )
    }

    val birthDateBlock: @Composable ColumnScope.() -> Unit = {
        BirthDateField(
            label = stringResource(R.string.birth_date_label),
            dateText = dateText,
            isError = birthDateError,
            onPick = { showDatePicker = true },
        )
    }

    val educationBlock: @Composable ColumnScope.() -> Unit = {
        EducationField(
            label = stringResource(R.string.education_level_label),
            placeholder = stringResource(R.string.select_option),
            selected = educationLevel,
            options = educationOptions,
            expanded = educationExpanded,
            onExpandedChange = { educationExpanded = it },
            onSelect = {
                educationLevel = it
                educationExpanded = false
            },
        )
    }

    // Botón "Siguiente": valida los obligatorios, registra los datos y navega a
    // la pantalla de datos de contacto.
    val onNext: () -> Unit = {
        firstNamesError = firstNames.isBlank()
        lastNamesError = lastNames.isBlank()
        birthDateError = birthDateMillis == null

        val valid = !firstNamesError && !lastNamesError && !birthDateError
        if (valid) {
            // Datos obligatorios OK -> se escriben en Logcat y se pasa a la siguiente pantalla.
            Log.d(TAG, "Información personal:")
            Log.d(TAG, "${firstNames.trim()} ${lastNames.trim()}")
            when (sex) {
                0 -> Log.d(TAG, "Masculino")
                1 -> Log.d(TAG, "Femenino")
            }
            Log.d(TAG, "Nació el ${formatDate(birthDateMillis!!)}")
            educationLevel?.let { Log.d(TAG, it) }
            context.startActivity(Intent(context, ContactDataActivity::class.java))
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.check_required_fields),
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    // Columna exterior: ocupa toda la pantalla, permite scroll y centra el contenido
    // (horizontal siempre; vertical cuando sobra espacio, típico en landscape).
    Column(
        modifier = modifier
            .fillMaxSize()
            // El área de scroll se encoge con el teclado -> el campo enfocado nunca queda tapado.
            .imePadding()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        // En landscape sobra alto: se centra verticalmente. En portrait se ancla arriba.
        verticalArrangement = if (isLandscape) {
            Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        } else {
            Arrangement.spacedBy(16.dp)
        },
    ) {
        // Tarjeta "glassmorphism" sobre el fondo aurora, con ancho acotado para que
        // en pantallas anchas no se estire de borde a borde.
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 720.dp),
        ) {
            Text(
                text = stringResource(R.string.personal_data_title),
                style = MaterialTheme.typography.headlineSmall,
            )
            LanguageSwitcher()

            if (isLandscape) {
                // --- LANDSCAPE: dos columnas para aprovechar el ancho ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        namesBlock()
                        sexBlock()
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        birthDateBlock()
                        educationBlock()
                    }
                }
            } else {
                // --- PORTRAIT: una sola columna ---
                namesBlock()
                sexBlock()
                birthDateBlock()
                educationBlock()
            }

            Button(onClick = onNext, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.next))
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = birthDateMillis,
            selectableDates = PastOrPresentSelectableDates,
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    birthDateMillis = datePickerState.selectedDateMillis
                    if (birthDateMillis != null) birthDateError = false
                    showDatePicker = false
                }) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

// ---------- Nombres / Apellidos (EditText) ----------
@Composable
private fun NameField(
    label: String,
    value: String,
    isError: Boolean,
    keyboardOptions: KeyboardOptions,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("$label *") },
        singleLine = true,
        isError = isError,
        supportingText = {
            if (isError) Text(stringResource(R.string.required_field))
        },
        leadingIcon = { Icon(painterResource(R.drawable.ic_person), contentDescription = null) },
        keyboardOptions = keyboardOptions,
        modifier = modifier.fillMaxWidth(),
    )
}

// ---------- Sexo (RadioButton) ----------
@Composable
private fun SexField(
    label: String,
    options: List<String>,
    selectedIndex: Int?,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(text = label, style = MaterialTheme.typography.labelLarge)
        options.forEachIndexed { index, option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = selectedIndex == index,
                        onClick = { onSelect(index) },
                        role = Role.RadioButton,
                    )
                    .padding(vertical = 4.dp),
            ) {
                RadioButton(selected = selectedIndex == index, onClick = null)
                Text(text = option, modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

// ---------- Fecha de nacimiento (DatePicker) ----------
@Composable
private fun BirthDateField(
    label: String,
    dateText: String,
    isError: Boolean,
    onPick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(R.drawable.ic_calendar), contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "$label *", style = MaterialTheme.typography.labelLarge)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = dateText)
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = onPick) {
                Text(stringResource(R.string.pick_date))
            }
        }
        if (isError) {
            Text(
                text = stringResource(R.string.required_field),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

// ---------- Grado de escolaridad (Spinner -> ExposedDropdownMenu) ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EducationField(
    label: String,
    placeholder: String,
    selected: String?,
    options: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = selected ?: placeholder,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            leadingIcon = { Icon(painterResource(R.drawable.ic_list), contentDescription = null) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = { onSelect(option) },
                )
            }
        }
    }
}

/**
 * El [DatePicker] entrega la fecha seleccionada en milisegundos UTC a medianoche.
 * Se formatea como dd/MM/yyyy y en UTC para evitar corrimientos de un día por la zona horaria.
 */
private fun formatDate(utcMillis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    return formatter.format(Date(utcMillis))
}

/** Solo permite seleccionar fechas de hoy hacia atrás (una fecha de nacimiento no puede ser futura). */
@OptIn(ExperimentalMaterial3Api::class)
private object PastOrPresentSelectableDates : SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean =
        utcTimeMillis <= System.currentTimeMillis()

    override fun isSelectableYear(year: Int): Boolean =
        year <= GregorianCalendar().get(GregorianCalendar.YEAR)
}

@Preview(name = "Portrait", showBackground = true)
@Composable
fun PersonalDataScreenPortraitPreview() {
    Labs20262Gr09Theme {
        PersonalDataScreen()
    }
}

@Preview(
    name = "Landscape",
    showBackground = true,
    widthDp = 720,
    heightDp = 360,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL,
)
@Composable
fun PersonalDataScreenLandscapePreview() {
    Labs20262Gr09Theme {
        PersonalDataScreen()
    }
}
