package com.example.ui.calculators

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.components.CalcInputField
import com.example.ui.components.CalculatorLayout
import com.example.ui.components.ResultCard

// Reusable Generic Unit Converter UI Composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitConverterScreen(
    title: String,
    categoryName: String,
    isFavorite: Boolean,
    units: List<String>,
    conversionFactorsToBase: Map<String, Double>, // Factor to base unit
    formulaText: String,
    explanationText: String,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var inputValStr by remember { mutableStateOf("") }
    var fromUnit by remember { mutableStateOf(units[0]) }
    var toUnit by remember { mutableStateOf(if (units.size > 1) units[1] else units[0]) }

    var expandedFrom by remember { mutableStateOf(false) }
    var expandedTo by remember { mutableStateOf(false) }

    var primaryVal by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val input = inputValStr.toDoubleOrNull()
        if (input == null) {
            errorMsg = "Please enter a valid value to convert"
            return
        }
        errorMsg = null

        val factorFrom = conversionFactorsToBase[fromUnit] ?: 1.0
        val factorTo = conversionFactorsToBase[toUnit] ?: 1.0

        val baseVal = input * factorFrom
        val output = baseVal / factorTo

        primaryVal = "$input $fromUnit = ${"%.6g".format(output)} $toUnit"
        onSaveHistory("Converted $input $fromUnit to $toUnit", primaryVal)
    }

    CalculatorLayout(
        title = title,
        categoryName = categoryName,
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = formulaText,
        explanationText = explanationText
    ) {
        CalcInputField(value = inputValStr, onValueChange = { inputValStr = it }, label = "Value to Convert")

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // From Unit
            ExposedDropdownMenuBox(
                expanded = expandedFrom,
                onExpandedChange = { expandedFrom = !expandedFrom },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = fromUnit,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("From") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFrom) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedFrom,
                    onDismissRequest = { expandedFrom = false }
                ) {
                    units.forEach { u ->
                        DropdownMenuItem(
                            text = { Text(u) },
                            onClick = { fromUnit = u; expandedFrom = false }
                        )
                    }
                }
            }

            // To Unit
            ExposedDropdownMenuBox(
                expanded = expandedTo,
                onExpandedChange = { expandedTo = !expandedTo },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = toUnit,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("To") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTo) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedTo,
                    onDismissRequest = { expandedTo = false }
                ) {
                    units.forEach { u ->
                        DropdownMenuItem(
                            text = { Text(u) },
                            onClick = { toUnit = u; expandedTo = false }
                        )
                    }
                }
            }
        }

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Convert Unit")
        }

        ResultCard(primaryValue = primaryVal, onReset = { inputValStr = ""; primaryVal = ""; errorMsg = null })
    }
}

// 1. Length Converter
@Composable
fun LengthConverterScreen(isFavorite: Boolean, onToggleFavorite: () -> Unit, onBackClick: () -> Unit, onSaveHistory: (String, String) -> Unit) {
    val units = listOf("Meter (m)", "Kilometer (km)", "Centimeter (cm)", "Millimeter (mm)", "Foot (ft)", "Inch (in)", "Mile (mi)", "Yard (yd)")
    val factors = mapOf(
        "Meter (m)" to 1.0, "Kilometer (km)" to 1000.0, "Centimeter (cm)" to 0.01, "Millimeter (mm)" to 0.001,
        "Foot (ft)" to 0.3048, "Inch (in)" to 0.0254, "Mile (mi)" to 1609.344, "Yard (yd)" to 0.9144
    )
    UnitConverterScreen(
        title = "Length Converter", categoryName = "🔄 Converters", isFavorite = isFavorite,
        units = units, conversionFactorsToBase = factors, formulaText = "1 km = 1000 m | 1 ft = 0.3048 m | 1 in = 2.54 cm",
        explanationText = "Convert between metric and imperial length units.",
        onToggleFavorite = onToggleFavorite, onBackClick = onBackClick, onSaveHistory = onSaveHistory
    )
}

// 2. Weight Converter
@Composable
fun WeightConverterScreen(isFavorite: Boolean, onToggleFavorite: () -> Unit, onBackClick: () -> Unit, onSaveHistory: (String, String) -> Unit) {
    val units = listOf("Kilogram (kg)", "Gram (g)", "Milligram (mg)", "Metric Ton (t)", "Pound (lb)", "Ounce (oz)", "Quintal (q)")
    val factors = mapOf(
        "Kilogram (kg)" to 1.0, "Gram (g)" to 0.001, "Milligram (mg)" to 0.000001, "Metric Ton (t)" to 1000.0,
        "Pound (lb)" to 0.45359237, "Ounce (oz)" to 0.0283495, "Quintal (q)" to 100.0
    )
    UnitConverterScreen(
        title = "Weight / Mass Converter", categoryName = "🔄 Converters", isFavorite = isFavorite,
        units = units, conversionFactorsToBase = factors, formulaText = "1 kg = 1000 g | 1 lb = 0.4536 kg | 1 Quintal = 100 kg",
        explanationText = "Convert weight and mass across metric, imperial, and Indian trade units.",
        onToggleFavorite = onToggleFavorite, onBackClick = onBackClick, onSaveHistory = onSaveHistory
    )
}

// 3. Temperature Converter
@Composable
fun TemperatureConverterScreen(isFavorite: Boolean, onToggleFavorite: () -> Unit, onBackClick: () -> Unit, onSaveHistory: (String, String) -> Unit) {
    var valStr by remember { mutableStateOf("") }
    var fromUnit by remember { mutableStateOf("Celsius (°C)") }
    var toUnit by remember { mutableStateOf("Fahrenheit (°F)") }
    var primaryVal by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val v = valStr.toDoubleOrNull()
        if (v == null) { errorMsg = "Please enter temperature value"; return }
        errorMsg = null

        val celsius = when (fromUnit) {
            "Celsius (°C)" -> v
            "Fahrenheit (°F)" -> (v - 32.0) * (5.0 / 9.0)
            "Kelvin (K)" -> v - 273.15
            else -> v
        }

        val res = when (toUnit) {
            "Celsius (°C)" -> celsius
            "Fahrenheit (°F)" -> (celsius * 9.0 / 5.0) + 32.0
            "Kelvin (K)" -> celsius + 273.15
            else -> celsius
        }

        primaryVal = "$v $fromUnit = ${"%.2f".format(res)} $toUnit"
        onSaveHistory("Temp conversion", primaryVal)
    }

    CalculatorLayout(
        title = "Temperature Converter", categoryName = "🔄 Converters", isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite, onBackClick = onBackClick,
        formulaText = "°F = (°C × 9/5) + 32  |  K = °C + 273.15",
        explanationText = "Convert between Celsius, Fahrenheit, and Kelvin scales."
    ) {
        CalcInputField(value = valStr, onValueChange = { valStr = it }, label = "Temperature Value")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Celsius (°C)", "Fahrenheit (°F)", "Kelvin (K)").forEach { u ->
                FilterChip(selected = fromUnit == u, onClick = { fromUnit = u }, label = { Text(u) })
            }
        }
        Text("Target Unit", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Celsius (°C)", "Fahrenheit (°F)", "Kelvin (K)").forEach { u ->
                FilterChip(selected = toUnit == u, onClick = { toUnit = u }, label = { Text(u) })
            }
        }

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) { Text("Convert Temperature") }

        ResultCard(primaryValue = primaryVal, onReset = { valStr = ""; primaryVal = ""; errorMsg = null })
    }
}

// 4. Area Converter (Includes Bigha, Katha for India)
@Composable
fun AreaConverterScreen(isFavorite: Boolean, onToggleFavorite: () -> Unit, onBackClick: () -> Unit, onSaveHistory: (String, String) -> Unit) {
    val units = listOf("Square Meter (m²)", "Square Foot (sq ft)", "Acre", "Hectare", "Square Yard", "Bigha (WB)", "Katha (WB)")
    val factors = mapOf(
        "Square Meter (m²)" to 1.0, "Square Foot (sq ft)" to 0.092903, "Acre" to 4046.86,
        "Hectare" to 10000.0, "Square Yard" to 0.836127, "Bigha (WB)" to 1333.33, "Katha (WB)" to 66.67
    )
    UnitConverterScreen(
        title = "Area Converter", categoryName = "🔄 Converters", isFavorite = isFavorite,
        units = units, conversionFactorsToBase = factors, formulaText = "1 Acre = 43,560 sq ft | 1 Hectare = 10,000 m² | 1 Bigha (WB) ≈ 20 Katha",
        explanationText = "Convert land and floor area across standard, international, and regional Indian units.",
        onToggleFavorite = onToggleFavorite, onBackClick = onBackClick, onSaveHistory = onSaveHistory
    )
}

// 5. Volume Converter
@Composable
fun VolumeConverterScreen(isFavorite: Boolean, onToggleFavorite: () -> Unit, onBackClick: () -> Unit, onSaveHistory: (String, String) -> Unit) {
    val units = listOf("Liter (L)", "Milliliter (mL)", "Gallon (US)", "Cubic Meter (m³)", "Cup (US)")
    val factors = mapOf(
        "Liter (L)" to 1.0, "Milliliter (mL)" to 0.001, "Gallon (US)" to 3.78541, "Cubic Meter (m³)" to 1000.0, "Cup (US)" to 0.236588
    )
    UnitConverterScreen(
        title = "Volume Converter", categoryName = "🔄 Converters", isFavorite = isFavorite,
        units = units, conversionFactorsToBase = factors, formulaText = "1 L = 1000 mL | 1 Gallon = 3.785 L",
        explanationText = "Convert liquid and container volume capacities.",
        onToggleFavorite = onToggleFavorite, onBackClick = onBackClick, onSaveHistory = onSaveHistory
    )
}

// 6. Time Converter
@Composable
fun TimeConverterScreen(isFavorite: Boolean, onToggleFavorite: () -> Unit, onBackClick: () -> Unit, onSaveHistory: (String, String) -> Unit) {
    val units = listOf("Second (s)", "Minute (min)", "Hour (hr)", "Day", "Week", "Month (30 days)", "Year (365 days)")
    val factors = mapOf(
        "Second (s)" to 1.0, "Minute (min)" to 60.0, "Hour (hr)" to 3600.0, "Day" to 86400.0,
        "Week" to 604800.0, "Month (30 days)" to 2592000.0, "Year (365 days)" to 31536000.0
    )
    UnitConverterScreen(
        title = "Time Converter", categoryName = "🔄 Converters", isFavorite = isFavorite,
        units = units, conversionFactorsToBase = factors, formulaText = "1 hr = 60 mins = 3600 secs | 1 day = 24 hrs",
        explanationText = "Convert time durations from seconds up to calendar years.",
        onToggleFavorite = onToggleFavorite, onBackClick = onBackClick, onSaveHistory = onSaveHistory
    )
}

// 7. Speed Converter
@Composable
fun SpeedConverterScreen(isFavorite: Boolean, onToggleFavorite: () -> Unit, onBackClick: () -> Unit, onSaveHistory: (String, String) -> Unit) {
    val units = listOf("Kilometer per Hour (km/h)", "Meter per Second (m/s)", "Miles per Hour (mph)", "Knot (kn)")
    val factors = mapOf(
        "Meter per Second (m/s)" to 1.0, "Kilometer per Hour (km/h)" to (1.0 / 3.6),
        "Miles per Hour (mph)" to 0.44704, "Knot (kn)" to 0.514444
    )
    UnitConverterScreen(
        title = "Speed Converter", categoryName = "🔄 Converters", isFavorite = isFavorite,
        units = units, conversionFactorsToBase = factors, formulaText = "1 m/s = 3.6 km/h | 1 mph = 1.609 km/h",
        explanationText = "Convert travel and wind speeds.",
        onToggleFavorite = onToggleFavorite, onBackClick = onBackClick, onSaveHistory = onSaveHistory
    )
}

// 8. Digital Data Storage Converter
@Composable
fun DataConverterScreen(isFavorite: Boolean, onToggleFavorite: () -> Unit, onBackClick: () -> Unit, onSaveHistory: (String, String) -> Unit) {
    val units = listOf("Byte (B)", "Kilobyte (KB)", "Megabyte (MB)", "Gigabyte (GB)", "Terabyte (TB)")
    val factors = mapOf(
        "Byte (B)" to 1.0, "Kilobyte (KB)" to 1024.0, "Megabyte (MB)" to 1048576.0,
        "Gigabyte (GB)" to 1073741824.0, "Terabyte (TB)" to 1099511627776.0
    )
    UnitConverterScreen(
        title = "Digital Data Storage Converter", categoryName = "🔄 Converters", isFavorite = isFavorite,
        units = units, conversionFactorsToBase = factors, formulaText = "1 KB = 1024 Bytes | 1 MB = 1024 KB | 1 GB = 1024 MB",
        explanationText = "Convert file and storage sizes using binary 1024 standards.",
        onToggleFavorite = onToggleFavorite, onBackClick = onBackClick, onSaveHistory = onSaveHistory
    )
}

// 9. Fuel Efficiency Converter
@Composable
fun FuelConverterScreen(isFavorite: Boolean, onToggleFavorite: () -> Unit, onBackClick: () -> Unit, onSaveHistory: (String, String) -> Unit) {
    val units = listOf("Kilometers per Liter (km/L)", "Miles per Gallon (US MPG)", "Liters per 100km (L/100km)")
    var valStr by remember { mutableStateOf("") }
    var fromUnit by remember { mutableStateOf("Kilometers per Liter (km/L)") }
    var toUnit by remember { mutableStateOf("Miles per Gallon (US MPG)") }
    var primaryVal by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val v = valStr.toDoubleOrNull()
        if (v == null || v <= 0) { errorMsg = "Please enter valid fuel efficiency"; return }
        errorMsg = null

        val kmL = when (fromUnit) {
            "Kilometers per Liter (km/L)" -> v
            "Miles per Gallon (US MPG)" -> v * 0.425144
            "Liters per 100km (L/100km)" -> 100.0 / v
            else -> v
        }

        val res = when (toUnit) {
            "Kilometers per Liter (km/L)" -> kmL
            "Miles per Gallon (US MPG)" -> kmL / 0.425144
            "Liters per 100km (L/100km)" -> 100.0 / kmL
            else -> kmL
        }

        primaryVal = "$v $fromUnit = ${"%.2f".format(res)} $toUnit"
        onSaveHistory("Fuel conversion", primaryVal)
    }

    CalculatorLayout(
        title = "Fuel Efficiency Converter", categoryName = "🔄 Converters", isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite, onBackClick = onBackClick,
        formulaText = "1 km/L ≈ 2.352 MPG | L/100km = 100 / (km/L)",
        explanationText = "Convert vehicle fuel mileage across metric and US standards."
    ) {
        CalcInputField(value = valStr, onValueChange = { valStr = it }, label = "Fuel Mileage Value")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            units.forEach { u -> FilterChip(selected = fromUnit == u, onClick = { fromUnit = u }, label = { Text(u.take(10)) }) }
        }
        Text("To Unit", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            units.forEach { u -> FilterChip(selected = toUnit == u, onClick = { toUnit = u }, label = { Text(u.take(10)) }) }
        }

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) { Text("Convert Fuel Mileage") }

        ResultCard(primaryValue = primaryVal, onReset = { valStr = ""; primaryVal = ""; errorMsg = null })
    }
}

// 10. Currency Converter (Offline with realistic default exchange rates)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyConverterScreen(isFavorite: Boolean, onToggleFavorite: () -> Unit, onBackClick: () -> Unit, onSaveHistory: (String, String) -> Unit) {
    var amountStr by remember { mutableStateOf("100") }
    var fromCurrency by remember { mutableStateOf("INR (₹)") }
    var toCurrency by remember { mutableStateOf("USD ($)") }

    var expandedFrom by remember { mutableStateOf(false) }
    var expandedTo by remember { mutableStateOf(false) }

    var primaryVal by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    // Rates relative to 1 INR (Indian Rupee as base currency requirement)
    val currencies = listOf("INR (₹)", "USD ($)", "EUR (€)", "GBP (£)", "AED (د.إ)", "JPY (¥)", "CAD ($)", "AUD ($)")
    val ratesToINR = mapOf(
        "INR (₹)" to 1.0,
        "USD ($)" to 83.5,     // 1 USD = 83.5 INR
        "EUR (€)" to 90.2,     // 1 EUR = 90.2 INR
        "GBP (£)" to 106.0,    // 1 GBP = 106.0 INR
        "AED (د.إ)" to 22.7,   // 1 AED = 22.7 INR
        "JPY (¥)" to 0.55,     // 1 JPY = 0.55 INR
        "CAD ($)" to 61.2,     // 1 CAD = 61.2 INR
        "AUD ($)" to 54.8      // 1 AUD = 54.8 INR
    )

    fun calculate() {
        val amt = amountStr.toDoubleOrNull()
        if (amt == null || amt < 0) {
            errorMsg = "Please enter valid currency amount"
            return
        }
        errorMsg = null

        val rateFromINR = ratesToINR[fromCurrency] ?: 1.0
        val rateToINR = ratesToINR[toCurrency] ?: 1.0

        val inrVal = amt * rateFromINR
        val converted = inrVal / rateToINR

        primaryVal = "$amt $fromCurrency = ${"%.2f".format(converted)} $toCurrency"
        onSaveHistory("Converted $amt $fromCurrency to $toCurrency", primaryVal)
    }

    CalculatorLayout(
        title = "Currency Converter", categoryName = "🔄 Converters", isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite, onBackClick = onBackClick,
        formulaText = "Uses Indian Rupee (₹) Base Rates (Offline Ready)",
        explanationText = "Convert amounts between Major Global Currencies and Indian Rupee."
    ) {
        CalcInputField(value = amountStr, onValueChange = { amountStr = it }, label = "Amount", prefix = if (fromCurrency.contains("INR")) "₹" else "")

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ExposedDropdownMenuBox(expanded = expandedFrom, onExpandedChange = { expandedFrom = !expandedFrom }, modifier = Modifier.weight(1f)) {
                OutlinedTextField(value = fromCurrency, onValueChange = {}, readOnly = true, label = { Text("From") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFrom) }, modifier = Modifier.menuAnchor())
                ExposedDropdownMenu(expanded = expandedFrom, onDismissRequest = { expandedFrom = false }) {
                    currencies.forEach { c -> DropdownMenuItem(text = { Text(c) }, onClick = { fromCurrency = c; expandedFrom = false }) }
                }
            }
            ExposedDropdownMenuBox(expanded = expandedTo, onExpandedChange = { expandedTo = !expandedTo }, modifier = Modifier.weight(1f)) {
                OutlinedTextField(value = toCurrency, onValueChange = {}, readOnly = true, label = { Text("To") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTo) }, modifier = Modifier.menuAnchor())
                ExposedDropdownMenu(expanded = expandedTo, onDismissRequest = { expandedTo = false }) {
                    currencies.forEach { c -> DropdownMenuItem(text = { Text(c) }, onClick = { toCurrency = c; expandedTo = false }) }
                }
            }
        }

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) { Text("Convert Currency") }

        ResultCard(primaryValue = primaryVal, onReset = { amountStr = "100"; primaryVal = ""; errorMsg = null })
    }
}
