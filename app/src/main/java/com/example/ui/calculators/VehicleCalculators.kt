package com.example.ui.calculators

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.components.CalcInputField
import com.example.ui.components.CalculatorLayout
import com.example.ui.components.ResultCard
import kotlin.math.pow

// 1. Vehicle Mileage Calculator
@Composable
fun VehicleMileageCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var distStr by remember { mutableStateOf("") }
    var fuelStr by remember { mutableStateOf("") }

    var primaryVal by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val dist = distStr.toDoubleOrNull()
        val fuel = fuelStr.toDoubleOrNull()

        if (dist == null || fuel == null || dist <= 0 || fuel <= 0) {
            errorMsg = "Please enter valid distance travelled and fuel consumed (> 0)"
            return
        }
        errorMsg = null

        val mileage = dist / fuel
        primaryVal = "Vehicle Mileage: ${"%.2f".format(mileage)} km/L"
        onSaveHistory("Mileage $dist km / $fuel L", primaryVal)
    }

    CalculatorLayout(
        title = "Vehicle Mileage Calculator", categoryName = "🚗 Vehicle", isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite, onBackClick = onBackClick,
        formulaText = "Mileage (km/L) = Distance Travelled (km) / Fuel Consumed (Liters)",
        explanationText = "Calculate actual fuel average achieved for bike or car."
    ) {
        CalcInputField(value = distStr, onValueChange = { distStr = it }, label = "Distance Travelled (km)", suffix = "km")
        CalcInputField(value = fuelStr, onValueChange = { fuelStr = it }, label = "Fuel Filled / Consumed (Liters)", suffix = "L")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) { Text("Calculate Mileage") }

        ResultCard(primaryValue = primaryVal, onReset = { distStr = ""; fuelStr = ""; primaryVal = ""; errorMsg = null })
    }
}

// 2. Fuel Cost per KM Calculator
@Composable
fun FuelPerKMCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var mileageStr by remember { mutableStateOf("") }
    var fuelPriceStr by remember { mutableStateOf("105") }

    var primaryVal by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val mileage = mileageStr.toDoubleOrNull()
        val price = fuelPriceStr.toDoubleOrNull() ?: 105.0

        if (mileage == null || mileage <= 0) {
            errorMsg = "Please enter valid vehicle mileage"
            return
        }
        errorMsg = null

        val costPerKm = price / mileage
        primaryVal = "Running Cost: ₹${"%.2f".format(costPerKm)} / km"
        onSaveHistory("Running cost for mileage $mileage km/L", primaryVal)
    }

    CalculatorLayout(
        title = "Fuel Cost per KM Calculator", categoryName = "🚗 Vehicle", isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite, onBackClick = onBackClick,
        formulaText = "Cost per KM = Fuel Price per Liter / Vehicle Mileage",
        explanationText = "Find per kilometer running cost of your vehicle."
    ) {
        CalcInputField(value = mileageStr, onValueChange = { mileageStr = it }, label = "Vehicle Mileage (km/L)", suffix = "km/L")
        CalcInputField(value = fuelPriceStr, onValueChange = { fuelPriceStr = it }, label = "Fuel Price per Liter", prefix = "₹")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) { Text("Calculate Cost per KM") }

        ResultCard(primaryValue = primaryVal, onReset = { mileageStr = ""; fuelPriceStr = "105"; primaryVal = ""; errorMsg = null })
    }
}

// 3. EV Charging Cost Calculator
@Composable
fun EVCostCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var batteryCapacityStr by remember { mutableStateOf("30") }
    var ratePerKWhStr by remember { mutableStateOf("8.5") }

    var primaryVal by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val capacity = batteryCapacityStr.toDoubleOrNull()
        val rate = ratePerKWhStr.toDoubleOrNull() ?: 8.5

        if (capacity == null || capacity <= 0) {
            errorMsg = "Please enter battery capacity in kWh"
            return
        }
        errorMsg = null

        val fullChargeCost = capacity * rate
        primaryVal = "Full Charge Cost: ${formatINR(fullChargeCost)}"
        onSaveHistory("EV Charging $capacity kWh @ ₹$rate", primaryVal)
    }

    CalculatorLayout(
        title = "EV Charging Cost Calculator", categoryName = "🚗 Vehicle", isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite, onBackClick = onBackClick,
        formulaText = "Full Charge Cost = Battery Size (kWh) × Rate per Unit (kWh)",
        explanationText = "Calculate full 0-100% charging cost for Electric Cars and Scooters."
    ) {
        CalcInputField(value = batteryCapacityStr, onValueChange = { batteryCapacityStr = it }, label = "EV Battery Pack Size (kWh)", suffix = "kWh")
        CalcInputField(value = ratePerKWhStr, onValueChange = { ratePerKWhStr = it }, label = "Electricity Rate per kWh (Unit)", prefix = "₹")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) { Text("Calculate Charging Cost") }

        ResultCard(primaryValue = primaryVal, onReset = { batteryCapacityStr = "30"; ratePerKWhStr = "8.5"; primaryVal = ""; errorMsg = null })
    }
}

// 4. EV Range Estimator
@Composable
fun EVRangeCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var batteryCapacityStr by remember { mutableStateOf("30") }
    var efficiencyStr by remember { mutableStateOf("8.5") }

    var primaryVal by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val capacity = batteryCapacityStr.toDoubleOrNull()
        val efficiency = efficiencyStr.toDoubleOrNull() ?: 8.5

        if (capacity == null || capacity <= 0 || efficiency <= 0) {
            errorMsg = "Please enter valid battery size and efficiency"
            return
        }
        errorMsg = null

        val estimatedRange = capacity * efficiency
        primaryVal = "Estimated Driving Range: ${"%.1f".format(estimatedRange)} km"
        onSaveHistory("EV Range $capacity kWh", primaryVal)
    }

    CalculatorLayout(
        title = "EV Driving Range Estimator", categoryName = "🚗 Vehicle", isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite, onBackClick = onBackClick,
        formulaText = "Range (km) = Battery Size (kWh) × Efficiency (km/kWh)",
        explanationText = "Estimate single full charge driving distance for Electric Vehicles."
    ) {
        CalcInputField(value = batteryCapacityStr, onValueChange = { batteryCapacityStr = it }, label = "Battery Size (kWh)", suffix = "kWh")
        CalcInputField(value = efficiencyStr, onValueChange = { efficiencyStr = it }, label = "Efficiency Average (km/kWh)", suffix = "km/kWh")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) { Text("Estimate EV Range") }

        ResultCard(primaryValue = primaryVal, onReset = { batteryCapacityStr = "30"; efficiencyStr = "8.5"; primaryVal = ""; errorMsg = null })
    }
}

// 5. Vehicle Loan EMI Calculator
@Composable
fun VehicleEMICalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var onRoadPriceStr by remember { mutableStateOf("") }
    var downPaymentStr by remember { mutableStateOf("") }
    var rateStr by remember { mutableStateOf("9.0") }
    var tenureYearsStr by remember { mutableStateOf("5") }

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val price = onRoadPriceStr.toDoubleOrNull()
        val down = downPaymentStr.toDoubleOrNull() ?: 0.0
        val R = rateStr.toDoubleOrNull() ?: 9.0
        val T = tenureYearsStr.toDoubleOrNull() ?: 5.0

        if (price == null || price <= 0 || down >= price) {
            errorMsg = "Please enter valid On-Road price (Down payment must be < Price)"
            return
        }
        errorMsg = null

        val P = price - down
        val months = T * 12.0
        val r = R / (12.0 * 100.0)

        val emi = (P * r * (1 + r).pow(months)) / ((1 + r).pow(months) - 1)
        val totalPay = emi * months
        val totalInterest = totalPay - P

        primaryVal = "Monthly EMI: ${formatINR(emi)}"
        details = listOf(
            "Financed Loan Amount" to formatINR(P),
            "Total Interest" to formatINR(totalInterest),
            "Total Cost Paid" to formatINR(totalPay + down)
        )
        onSaveHistory("Vehicle EMI for ${formatINR(price)}", primaryVal)
    }

    CalculatorLayout(
        title = "Vehicle Loan EMI Calculator", categoryName = "🚗 Vehicle", isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite, onBackClick = onBackClick,
        formulaText = "EMI on (On-Road Price - Down Payment)",
        explanationText = "Calculate monthly EMI and total interest for bike or car loans."
    ) {
        CalcInputField(value = onRoadPriceStr, onValueChange = { onRoadPriceStr = it }, label = "Vehicle On-Road Price", prefix = "₹")
        CalcInputField(value = downPaymentStr, onValueChange = { downPaymentStr = it }, label = "Down Payment Amount", prefix = "₹")
        CalcInputField(value = rateStr, onValueChange = { rateStr = it }, label = "Interest Rate (% p.a.)", suffix = "%")
        CalcInputField(value = tenureYearsStr, onValueChange = { tenureYearsStr = it }, label = "Tenure (Years)")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) { Text("Calculate Vehicle EMI") }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { onRoadPriceStr = ""; downPaymentStr = ""; rateStr = "9.0"; tenureYearsStr = "5"; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}
