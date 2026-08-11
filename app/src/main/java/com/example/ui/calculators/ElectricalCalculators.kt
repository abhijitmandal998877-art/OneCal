package com.example.ui.calculators

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.components.CalcInputField
import com.example.ui.components.CalculatorLayout
import com.example.ui.components.ResultCard
import kotlin.math.sqrt

// 1. Ohm's Law Calculator
@Composable
fun OhmsLawCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var vStr by remember { mutableStateOf("") }
    var iStr by remember { mutableStateOf("") }
    var rStr by remember { mutableStateOf("") }

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val V = vStr.toDoubleOrNull()
        val I = iStr.toDoubleOrNull()
        val R = rStr.toDoubleOrNull()

        val count = listOf(V, I, R).count { it != null }
        if (count != 2) {
            errorMsg = "Please enter exactly 2 parameters out of Voltage (V), Current (I), and Resistance (R)"
            return
        }
        errorMsg = null

        if (V == null) {
            val calcV = I!! * R!!
            primaryVal = "Voltage (V) = ${"%.2f".format(calcV)} V"
            details = listOf("Current (I)" to "$I A", "Resistance (R)" to "$R Ω")
        } else if (I == null) {
            if (R == 0.0) { errorMsg = "Resistance cannot be zero"; return }
            val calcI = V / R!!
            primaryVal = "Current (I) = ${"%.3f".format(calcI)} A"
            details = listOf("Voltage (V)" to "$V V", "Resistance (R)" to "$R Ω")
        } else if (R == null) {
            if (I == 0.0) { errorMsg = "Current cannot be zero"; return }
            val calcR = V / I
            primaryVal = "Resistance (R) = ${"%.2f".format(calcR)} Ω"
            details = listOf("Voltage (V)" to "$V V", "Current (I)" to "$I A")
        }
        onSaveHistory("Ohm's Law Calculation", primaryVal)
    }

    CalculatorLayout(
        title = "Ohm's Law Calculator",
        categoryName = "⚡ Electrical",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "V = I × R  |  I = V / R  |  R = V / I",
        explanationText = "Calculate Voltage, Current, or Resistance for ITI & electrical engineering."
    ) {
        CalcInputField(value = vStr, onValueChange = { vStr = it }, label = "Voltage (V)", suffix = "V")
        CalcInputField(value = iStr, onValueChange = { iStr = it }, label = "Current (I)", suffix = "A")
        CalcInputField(value = rStr, onValueChange = { rStr = it }, label = "Resistance (R)", suffix = "Ω")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate Ohm's Law")
        }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { vStr = ""; iStr = ""; rStr = ""; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}

// 2. Voltage Calculator
@Composable
fun VoltageCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var iStr by remember { mutableStateOf("") }
    var rStr by remember { mutableStateOf("") }

    var primaryVal by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val I = iStr.toDoubleOrNull()
        val R = rStr.toDoubleOrNull()

        if (I == null || R == null) {
            errorMsg = "Please enter valid Current (A) and Resistance (Ω)"
            return
        }
        errorMsg = null

        val V = I * R
        val P = V * I
        primaryVal = "Voltage = ${"%.2f".format(V)} Volts (V)"
        onSaveHistory("Voltage for $I A, $R Ω", primaryVal)
    }

    CalculatorLayout(
        title = "Voltage Calculator",
        categoryName = "⚡ Electrical",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "Voltage V = Current (I) × Resistance (R)",
        explanationText = "Calculates potential difference in volts."
    ) {
        CalcInputField(value = iStr, onValueChange = { iStr = it }, label = "Current (I)", suffix = "A")
        CalcInputField(value = rStr, onValueChange = { rStr = it }, label = "Resistance (R)", suffix = "Ω")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate Voltage")
        }

        ResultCard(primaryValue = primaryVal, onReset = { iStr = ""; rStr = ""; primaryVal = ""; errorMsg = null })
    }
}

// 3. Current Calculator
@Composable
fun CurrentCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var vStr by remember { mutableStateOf("") }
    var rStr by remember { mutableStateOf("") }

    var primaryVal by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val V = vStr.toDoubleOrNull()
        val R = rStr.toDoubleOrNull()

        if (V == null || R == null || R == 0.0) {
            errorMsg = "Please enter valid Voltage and Resistance (> 0)"
            return
        }
        errorMsg = null

        val I = V / R
        primaryVal = "Current = ${"%.3f".format(I)} Amperes (A)"
        onSaveHistory("Current for $V V, $R Ω", primaryVal)
    }

    CalculatorLayout(
        title = "Current Calculator",
        categoryName = "⚡ Electrical",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "Current I = Voltage (V) / Resistance (R)",
        explanationText = "Calculates electric current flow in amperes."
    ) {
        CalcInputField(value = vStr, onValueChange = { vStr = it }, label = "Voltage (V)", suffix = "V")
        CalcInputField(value = rStr, onValueChange = { rStr = it }, label = "Resistance (R)", suffix = "Ω")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate Current")
        }

        ResultCard(primaryValue = primaryVal, onReset = { vStr = ""; rStr = ""; primaryVal = ""; errorMsg = null })
    }
}

// 4. Resistance Calculator
@Composable
fun ResistanceCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var vStr by remember { mutableStateOf("") }
    var iStr by remember { mutableStateOf("") }

    var primaryVal by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val V = vStr.toDoubleOrNull()
        val I = iStr.toDoubleOrNull()

        if (V == null || I == null || I == 0.0) {
            errorMsg = "Please enter valid Voltage and Current (> 0)"
            return
        }
        errorMsg = null

        val R = V / I
        primaryVal = "Resistance = ${"%.2f".format(R)} Ohms (Ω)"
        onSaveHistory("Resistance for $V V, $I A", primaryVal)
    }

    CalculatorLayout(
        title = "Resistance Calculator",
        categoryName = "⚡ Electrical",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "Resistance R = Voltage (V) / Current (I)",
        explanationText = "Calculate electrical resistance in ohms."
    ) {
        CalcInputField(value = vStr, onValueChange = { vStr = it }, label = "Voltage (V)", suffix = "V")
        CalcInputField(value = iStr, onValueChange = { iStr = it }, label = "Current (I)", suffix = "A")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate Resistance")
        }

        ResultCard(primaryValue = primaryVal, onReset = { vStr = ""; iStr = ""; primaryVal = ""; errorMsg = null })
    }
}

// 5. Electrical Power Calculator
@Composable
fun ElecPowerCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var vStr by remember { mutableStateOf("") }
    var iStr by remember { mutableStateOf("") }

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val V = vStr.toDoubleOrNull()
        val I = iStr.toDoubleOrNull()

        if (V == null || I == null) {
            errorMsg = "Please enter valid Voltage and Current"
            return
        }
        errorMsg = null

        val P = V * I
        val pKW = P / 1000.0

        primaryVal = "Power = ${"%.2f".format(P)} Watts (W)"
        details = listOf(
            "Power in Kilowatts" to "%.3f kW".format(pKW),
            "Voltage" to "$V V",
            "Current" to "$I A"
        )
        onSaveHistory("Electrical Power for $V V, $I A", primaryVal)
    }

    CalculatorLayout(
        title = "Electrical Power Calculator",
        categoryName = "⚡ Electrical",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "Power (P) = Voltage (V) × Current (I)",
        explanationText = "Calculate power dissipation in watts and kilowatts."
    ) {
        CalcInputField(value = vStr, onValueChange = { vStr = it }, label = "Voltage (V)", suffix = "V")
        CalcInputField(value = iStr, onValueChange = { iStr = it }, label = "Current (I)", suffix = "A")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate Power")
        }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { vStr = ""; iStr = ""; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}

// 6. Electrical Energy Calculator
@Composable
fun ElecEnergyCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var wattsStr by remember { mutableStateOf("") }
    var hoursPerDayStr by remember { mutableStateOf("8") }
    var daysStr by remember { mutableStateOf("30") }

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val W = wattsStr.toDoubleOrNull()
        val h = hoursPerDayStr.toDoubleOrNull() ?: 8.0
        val d = daysStr.toDoubleOrNull() ?: 30.0

        if (W == null || W <= 0) {
            errorMsg = "Please enter valid appliance power in Watts"
            return
        }
        errorMsg = null

        val totalHours = h * d
        val energyKWh = (W * totalHours) / 1000.0

        primaryVal = "Energy Consumed: ${"%.2f".format(energyKWh)} Units (kWh)"
        details = listOf(
            "Appliance Power" to "$W Watts",
            "Daily Usage" to "$h hours/day",
            "Period" to "${d.toInt()} days"
        )
        onSaveHistory("Energy for $W W over ${d.toInt()} days", primaryVal)
    }

    CalculatorLayout(
        title = "Electrical Energy Calculator",
        categoryName = "⚡ Electrical",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "Units (kWh) = [Power (W) × Usage Hours × Days] / 1000",
        explanationText = "Calculate electricity consumption units for any household appliance."
    ) {
        CalcInputField(value = wattsStr, onValueChange = { wattsStr = it }, label = "Appliance Power (Watts)", suffix = "W")
        CalcInputField(value = hoursPerDayStr, onValueChange = { hoursPerDayStr = it }, label = "Daily Usage Hours", suffix = "hrs/day")
        CalcInputField(value = daysStr, onValueChange = { daysStr = it }, label = "Number of Days", suffix = "days")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate Energy Units")
        }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { wattsStr = ""; hoursPerDayStr = "8"; daysStr = "30"; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}

// 7. Series Resistance Calculator
@Composable
fun SeriesRCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var rListStr by remember { mutableStateOf("") }

    var primaryVal by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val resistors = rListStr.split(",", " ", "\n").mapNotNull { it.trim().toDoubleOrNull() }

        if (resistors.isEmpty()) {
            errorMsg = "Please enter resistor values in ohms"
            return
        }
        errorMsg = null

        val totalR = resistors.sum()
        primaryVal = "Total Series Resistance (R_eq) = ${"%.2f".format(totalR)} Ω"
        onSaveHistory("Series Resistance of ${resistors.size} resistors", primaryVal)
    }

    CalculatorLayout(
        title = "Series Resistance Calculator",
        categoryName = "⚡ Electrical",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "R_total = R₁ + R₂ + R₃ + ...",
        explanationText = "Calculates total equivalent resistance for resistors connected in series."
    ) {
        CalcInputField(
            value = rListStr,
            onValueChange = { rListStr = it },
            label = "Enter Resistor Values (ohms, comma/space separated)",
            placeholder = "e.g. 100, 220, 470"
        )

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate Series Resistance")
        }

        ResultCard(primaryValue = primaryVal, onReset = { rListStr = ""; primaryVal = ""; errorMsg = null })
    }
}

// 8. Parallel Resistance Calculator
@Composable
fun ParallelRCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var rListStr by remember { mutableStateOf("") }

    var primaryVal by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val resistors = rListStr.split(",", " ", "\n").mapNotNull { it.trim().toDoubleOrNull() }

        if (resistors.isEmpty() || resistors.any { it <= 0 }) {
            errorMsg = "Please enter valid non-zero positive resistor values"
            return
        }
        errorMsg = null

        val recipSum = resistors.sumOf { 1.0 / it }
        val req = 1.0 / recipSum

        primaryVal = "Total Parallel Resistance (R_eq) = ${"%.2f".format(req)} Ω"
        onSaveHistory("Parallel Resistance of ${resistors.size} resistors", primaryVal)
    }

    CalculatorLayout(
        title = "Parallel Resistance Calculator",
        categoryName = "⚡ Electrical",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "1 / R_total = 1/R₁ + 1/R₂ + 1/R₃ + ...",
        explanationText = "Calculates equivalent resistance for resistors in parallel."
    ) {
        CalcInputField(
            value = rListStr,
            onValueChange = { rListStr = it },
            label = "Enter Resistor Values (ohms, comma/space separated)",
            placeholder = "e.g. 100, 200, 300"
        )

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate Parallel Resistance")
        }

        ResultCard(primaryValue = primaryVal, onReset = { rListStr = ""; primaryVal = ""; errorMsg = null })
    }
}

// 9. Electricity Cost Calculator
@Composable
fun ElectricityCostCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var powerStr by remember { mutableStateOf("") }
    var hoursStr by remember { mutableStateOf("8") }
    var ratePerUnitStr by remember { mutableStateOf("7.5") }

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val W = powerStr.toDoubleOrNull()
        val hrs = hoursStr.toDoubleOrNull() ?: 8.0
        val rate = ratePerUnitStr.toDoubleOrNull() ?: 7.5

        if (W == null || W <= 0) {
            errorMsg = "Please enter valid appliance power in Watts"
            return
        }
        errorMsg = null

        val dailyKWh = (W * hrs) / 1000.0
        val monthlyKWh = dailyKWh * 30.0
        val dailyCost = dailyKWh * rate
        val monthlyCost = monthlyKWh * rate

        primaryVal = "Monthly Cost: ${formatINR(monthlyCost)}"
        details = listOf(
            "Daily Units" to "%.2f kWh".format(dailyKWh),
            "Monthly Units (30 days)" to "%.2f kWh".format(monthlyKWh),
            "Daily Electricity Cost" to formatINR(dailyCost),
            "Tariff Rate" to "₹$rate / unit"
        )
        onSaveHistory("Appliance Bill for $W W ($hrs hrs/day)", primaryVal)
    }

    CalculatorLayout(
        title = "Electricity Cost Calculator",
        categoryName = "⚡ Electrical",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "Cost = (Watts × Hours / 1000) × Tariff Rate per Unit",
        explanationText = "Estimates monthly running cost for electrical appliances."
    ) {
        CalcInputField(value = powerStr, onValueChange = { powerStr = it }, label = "Appliance Power (Watts)", suffix = "W")
        CalcInputField(value = hoursStr, onValueChange = { hoursStr = it }, label = "Daily Running Hours", suffix = "hrs/day")
        CalcInputField(value = ratePerUnitStr, onValueChange = { ratePerUnitStr = it }, label = "Electricity Rate per Unit (₹/kWh)", prefix = "₹")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate Electricity Cost")
        }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { powerStr = ""; hoursStr = "8"; ratePerUnitStr = "7.5"; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}
