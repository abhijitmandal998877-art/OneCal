package com.example.ui.calculators

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.components.CalcInputField
import com.example.ui.components.CalculatorLayout
import com.example.ui.components.ResultCard
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit

// 1. Date Difference Calculator
@Composable
fun DateDiffCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var d1 by remember { mutableStateOf("") }
    var m1 by remember { mutableStateOf("") }
    var y1 by remember { mutableStateOf("") }

    var d2 by remember { mutableStateOf("") }
    var m2 by remember { mutableStateOf("") }
    var y2 by remember { mutableStateOf("") }

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val day1 = d1.toIntOrNull()
        val mon1 = m1.toIntOrNull()
        val yr1 = y1.toIntOrNull()

        val day2 = d2.toIntOrNull()
        val mon2 = m2.toIntOrNull()
        val yr2 = y2.toIntOrNull()

        if (day1 == null || mon1 == null || yr1 == null || day2 == null || mon2 == null || yr2 == null) {
            errorMsg = "Please enter valid day, month, and year for both dates"
            return
        }

        try {
            val date1 = LocalDate.of(yr1, mon1, day1)
            val date2 = LocalDate.of(yr2, mon2, day2)
            errorMsg = null

            val diffDays = ChronoUnit.DAYS.between(date1, date2)
            val absDays = Math.abs(diffDays)
            val weeks = absDays / 7
            val remDays = absDays % 7

            primaryVal = "Difference: $absDays Days"
            details = listOf(
                "In Weeks" to "$weeks weeks and $remDays days",
                "In Hours" to "${absDays * 24} hours"
            )
            onSaveHistory("Date diff $d1/$m1/$y1 to $d2/$m2/$y2", primaryVal)
        } catch (e: Exception) {
            errorMsg = "Invalid Date combination"
        }
    }

    CalculatorLayout(
        title = "Date Difference Calculator", categoryName = "☀️ Daily Life", isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite, onBackClick = onBackClick,
        formulaText = "Days = Date 2 - Date 1", explanationText = "Find number of days and weeks between two calendar dates."
    ) {
        Text("Start Date", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CalcInputField(value = d1, onValueChange = { d1 = it }, label = "Day", modifier = Modifier.weight(1f))
            CalcInputField(value = m1, onValueChange = { m1 = it }, label = "Month", modifier = Modifier.weight(1f))
            CalcInputField(value = y1, onValueChange = { y1 = it }, label = "Year", modifier = Modifier.weight(1f))
        }

        Text("End Date", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CalcInputField(value = d2, onValueChange = { d2 = it }, label = "Day", modifier = Modifier.weight(1f))
            CalcInputField(value = m2, onValueChange = { m2 = it }, label = "Month", modifier = Modifier.weight(1f))
            CalcInputField(value = y2, onValueChange = { y2 = it }, label = "Year", modifier = Modifier.weight(1f))
        }

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) { Text("Calculate Difference") }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { d1 = ""; m1 = ""; y1 = ""; d2 = ""; m2 = ""; y2 = ""; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}

// 2. Time Duration Calculator
@Composable
fun TimeDurCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var h1 by remember { mutableStateOf("") }
    var min1 by remember { mutableStateOf("") }

    var h2 by remember { mutableStateOf("") }
    var min2 by remember { mutableStateOf("") }

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val hr1 = h1.toIntOrNull()
        val m1 = min1.toIntOrNull() ?: 0
        val hr2 = h2.toIntOrNull()
        val m2 = min2.toIntOrNull() ?: 0

        if (hr1 == null || hr2 == null || hr1 !in 0..23 || hr2 !in 0..23 || m1 !in 0..59 || m2 !in 0..59) {
            errorMsg = "Please enter valid 24-hour time values (Hours 0-23, Mins 0-59)"
            return
        }
        errorMsg = null

        val startMins = hr1 * 60 + m1
        var endMins = hr2 * 60 + m2

        if (endMins < startMins) {
            endMins += 24 * 60 // Overnight duration
        }

        val diffMins = endMins - startMins
        val hours = diffMins / 60
        val remainingMins = diffMins % 60

        primaryVal = "Duration: $hours Hours, $remainingMins Minutes"
        details = listOf("Total Minutes" to "$diffMins mins")
        onSaveHistory("Time duration $hr1:$m1 to $hr2:$m2", primaryVal)
    }

    CalculatorLayout(
        title = "Time Duration Calculator", categoryName = "☀️ Daily Life", isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite, onBackClick = onBackClick,
        formulaText = "Duration = End Time - Start Time (24-hour format)",
        explanationText = "Calculate total hours and minutes between start and end times (supports overnight shift calculations)."
    ) {
        Text("Start Time (24-Hour)", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CalcInputField(value = h1, onValueChange = { h1 = it }, label = "Hours (0-23)", modifier = Modifier.weight(1f))
            CalcInputField(value = min1, onValueChange = { min1 = it }, label = "Mins (0-59)", modifier = Modifier.weight(1f))
        }

        Text("End Time (24-Hour)", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CalcInputField(value = h2, onValueChange = { h2 = it }, label = "Hours (0-23)", modifier = Modifier.weight(1f))
            CalcInputField(value = min2, onValueChange = { min2 = it }, label = "Mins (0-59)", modifier = Modifier.weight(1f))
        }

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) { Text("Calculate Duration") }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { h1 = ""; min1 = ""; h2 = ""; min2 = ""; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}

// 3. Tip Calculator
@Composable
fun TipCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var billStr by remember { mutableStateOf("") }
    var tipPctStr by remember { mutableStateOf("10") }
    var peopleStr by remember { mutableStateOf("1") }

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val bill = billStr.toDoubleOrNull()
        val tipPct = tipPctStr.toDoubleOrNull() ?: 10.0
        val people = peopleStr.toIntOrNull() ?: 1

        if (bill == null || bill <= 0 || people <= 0) {
            errorMsg = "Please enter valid bill amount and number of people"
            return
        }
        errorMsg = null

        val tipAmt = (bill * tipPct) / 100.0
        val totalBill = bill + tipAmt
        val perPerson = totalBill / people

        primaryVal = "Per Person Pays: ${formatINR(perPerson)}"
        details = listOf(
            "Original Bill" to formatINR(bill),
            "Tip Amount ($tipPct%)" to formatINR(tipAmt),
            "Total Bill" to formatINR(totalBill),
            "Split Between" to "$people people"
        )
        onSaveHistory("Tip ${formatINR(bill)} split by $people", primaryVal)
    }

    CalculatorLayout(
        title = "Tip Calculator", categoryName = "☀️ Daily Life", isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite, onBackClick = onBackClick,
        formulaText = "Per Person = [Bill + (Bill × Tip%)] / People Count",
        explanationText = "Calculate restaurant tips and split total bill per person."
    ) {
        CalcInputField(value = billStr, onValueChange = { billStr = it }, label = "Bill Amount", prefix = "₹")

        Text("Select Tip Percentage", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("5%", "10%", "15%", "20%").forEach { pct ->
                FilterChip(selected = tipPctStr == pct.removeSuffix("%"), onClick = { tipPctStr = pct.removeSuffix("%") }, label = { Text(pct) })
            }
        }
        CalcInputField(value = tipPctStr, onValueChange = { tipPctStr = it }, label = "Custom Tip (%)", suffix = "%")
        CalcInputField(value = peopleStr, onValueChange = { peopleStr = it }, label = "Split Between (People Count)")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) { Text("Calculate Tip & Split") }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { billStr = ""; tipPctStr = "10"; peopleStr = "1"; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}

// 4. Shopping Discount Calculator
@Composable
fun ShoppingDiscCalculatorScreen(isFavorite: Boolean, onToggleFavorite: () -> Unit, onBackClick: () -> Unit, onSaveHistory: (String, String) -> Unit) {
    DiscountCalculatorScreen(isFavorite, onToggleFavorite, onBackClick, onSaveHistory)
}

// 5. Unit Price Comparison Calculator
@Composable
fun UnitCompCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var priceAStr by remember { mutableStateOf("") }
    var qtyAStr by remember { mutableStateOf("") }

    var priceBStr by remember { mutableStateOf("") }
    var qtyBStr by remember { mutableStateOf("") }

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val pA = priceAStr.toDoubleOrNull()
        val qA = qtyAStr.toDoubleOrNull()

        val pB = priceBStr.toDoubleOrNull()
        val qB = qtyBStr.toDoubleOrNull()

        if (pA == null || qA == null || pB == null || qB == null || qA <= 0 || qB <= 0) {
            errorMsg = "Please enter valid price and quantity for Pack A and Pack B"
            return
        }
        errorMsg = null

        val unitA = pA / qA
        val unitB = pB / qB

        if (unitA < unitB) {
            val savingsPct = ((unitB - unitA) / unitB) * 100.0
            primaryVal = "Pack A is CHEAPER! (${"%.1f".format(savingsPct)}% better value)"
        } else if (unitB < unitA) {
            val savingsPct = ((unitA - unitB) / unitA) * 100.0
            primaryVal = "Pack B is CHEAPER! (${"%.1f".format(savingsPct)}% better value)"
        } else {
            primaryVal = "Both Packs Have EQUAL Value!"
        }

        details = listOf(
            "Pack A Unit Price" to "₹${"%.4f".format(unitA)} / unit",
            "Pack B Unit Price" to "₹${"%.4f".format(unitB)} / unit"
        )
        onSaveHistory("Unit price comp Pack A vs B", primaryVal)
    }

    CalculatorLayout(
        title = "Unit Price Comparison", categoryName = "☀️ Daily Life", isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite, onBackClick = onBackClick,
        formulaText = "Unit Price = Price / Quantity",
        explanationText = "Compare two grocery packet sizes to find the best value deal."
    ) {
        Text("Pack A", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CalcInputField(value = priceAStr, onValueChange = { priceAStr = it }, label = "Price (₹)", prefix = "₹", modifier = Modifier.weight(1f))
            CalcInputField(value = qtyAStr, onValueChange = { qtyAStr = it }, label = "Quantity", modifier = Modifier.weight(1f))
        }

        Text("Pack B", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CalcInputField(value = priceBStr, onValueChange = { priceBStr = it }, label = "Price (₹)", prefix = "₹", modifier = Modifier.weight(1f))
            CalcInputField(value = qtyBStr, onValueChange = { qtyBStr = it }, label = "Quantity", modifier = Modifier.weight(1f))
        }

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) { Text("Compare Best Value") }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { priceAStr = ""; qtyAStr = ""; priceBStr = ""; qtyBStr = ""; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}

// 6. Fuel Cost Calculator
@Composable
fun FuelCostCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var distanceStr by remember { mutableStateOf("") }
    var mileageStr by remember { mutableStateOf("18") }
    var fuelPriceStr by remember { mutableStateOf("105") }

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val dist = distanceStr.toDoubleOrNull()
        val mileage = mileageStr.toDoubleOrNull() ?: 18.0
        val price = fuelPriceStr.toDoubleOrNull() ?: 105.0

        if (dist == null || dist <= 0 || mileage <= 0) {
            errorMsg = "Please enter valid distance and vehicle mileage"
            return
        }
        errorMsg = null

        val fuelNeeded = dist / mileage
        val totalCost = fuelNeeded * price

        primaryVal = "Trip Fuel Cost: ${formatINR(totalCost)}"
        details = listOf(
            "Fuel Required" to "${"%.2f".format(fuelNeeded)} Liters",
            "Cost per KM" to "₹${"%.2f".format(totalCost / dist)}"
        )
        onSaveHistory("Fuel cost for $dist km", primaryVal)
    }

    CalculatorLayout(
        title = "Fuel Cost Calculator", categoryName = "☀️ Daily Life", isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite, onBackClick = onBackClick,
        formulaText = "Trip Cost = (Distance / Mileage) × Fuel Price per Liter",
        explanationText = "Calculate total fuel expense for road trips and daily commutes."
    ) {
        CalcInputField(value = distanceStr, onValueChange = { distanceStr = it }, label = "Trip Distance (km)", suffix = "km")
        CalcInputField(value = mileageStr, onValueChange = { mileageStr = it }, label = "Vehicle Mileage (km/L)", suffix = "km/L")
        CalcInputField(value = fuelPriceStr, onValueChange = { fuelPriceStr = it }, label = "Fuel Price per Liter", prefix = "₹")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) { Text("Calculate Trip Cost") }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { distanceStr = ""; mileageStr = "18"; fuelPriceStr = "105"; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}

// 7. Electricity Bill Estimator
@Composable
fun ElecBillEstCalculatorScreen(isFavorite: Boolean, onToggleFavorite: () -> Unit, onBackClick: () -> Unit, onSaveHistory: (String, String) -> Unit) {
    ElectricityCostCalculatorScreen(isFavorite, onToggleFavorite, onBackClick, onSaveHistory)
}

// 8. Calorie Calculator
@Composable
fun CalorieCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var ageStr by remember { mutableStateOf("25") }
    var weightStr by remember { mutableStateOf("65") }
    var heightStr by remember { mutableStateOf("170") }
    var isMale by remember { mutableStateOf(true) }

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val age = ageStr.toIntOrNull()
        val weight = weightStr.toDoubleOrNull()
        val height = heightStr.toDoubleOrNull()

        if (age == null || weight == null || height == null || age <= 0 || weight <= 0 || height <= 0) {
            errorMsg = "Please enter valid Age, Weight, and Height"
            return
        }
        errorMsg = null

        // Mifflin-St Jeor Formula
        val bmr = if (isMale) {
            (10 * weight) + (6.25 * height) - (5 * age) + 5
        } else {
            (10 * weight) + (6.25 * height) - (5 * age) - 161
        }

        val maintenance = bmr * 1.375 // Lightly active multiplier

        primaryVal = "Maintenance: ${maintenance.toInt()} kcal/day"
        details = listOf(
            "BMR (Basal Metabolic Rate)" to "${bmr.toInt()} kcal/day",
            "Weight Loss Target" to "${(maintenance - 500).toInt()} kcal/day",
            "Weight Gain Target" to "${(maintenance + 500).toInt()} kcal/day"
        )
        onSaveHistory("Calorie BMR $bmr", primaryVal)
    }

    CalculatorLayout(
        title = "Calorie / TDEE Calculator", categoryName = "☀️ Daily Life", isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite, onBackClick = onBackClick,
        formulaText = "Mifflin-St Jeor BMR & Maintenance Calorie Formula",
        explanationText = "Estimate daily calories needed for weight maintenance, loss, or gain."
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = isMale, onClick = { isMale = true }, label = { Text("Male") })
            FilterChip(selected = !isMale, onClick = { isMale = false }, label = { Text("Female") })
        }
        CalcInputField(value = ageStr, onValueChange = { ageStr = it }, label = "Age (Years)")
        CalcInputField(value = weightStr, onValueChange = { weightStr = it }, label = "Weight (kg)", suffix = "kg")
        CalcInputField(value = heightStr, onValueChange = { heightStr = it }, label = "Height (cm)", suffix = "cm")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) { Text("Calculate Calories") }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { ageStr = "25"; weightStr = "65"; heightStr = "170"; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}

// 9. Water Intake Calculator
@Composable
fun WaterIntakeCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var weightStr by remember { mutableStateOf("") }

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val weight = weightStr.toDoubleOrNull()
        if (weight == null || weight <= 0) {
            errorMsg = "Please enter valid body weight in kg"
            return
        }
        errorMsg = null

        val ml = weight * 35.0 // 35 ml per kg body weight
        val liters = ml / 1000.0
        val glasses = (ml / 250.0).toInt()

        primaryVal = "Daily Requirement: ${"%.2f".format(liters)} Liters"
        details = listOf("In Glasses (250ml)" to "$glasses glasses per day")
        onSaveHistory("Water intake for $weight kg", primaryVal)
    }

    CalculatorLayout(
        title = "Water Intake Calculator", categoryName = "☀️ Daily Life", isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite, onBackClick = onBackClick,
        formulaText = "Daily Water (mL) = Body Weight (kg) × 35 mL",
        explanationText = "Calculate recommended daily hydration water target."
    ) {
        CalcInputField(value = weightStr, onValueChange = { weightStr = it }, label = "Body Weight (kg)", suffix = "kg")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) { Text("Calculate Water Target") }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { weightStr = ""; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}

// 10. Sleep Calculator
@Composable
fun SleepCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var bedHoursStr by remember { mutableStateOf("23") }
    var bedMinsStr by remember { mutableStateOf("00") }

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val h = bedHoursStr.toIntOrNull()
        val m = bedMinsStr.toIntOrNull() ?: 0

        if (h == null || h !in 0..23 || m !in 0..59) {
            errorMsg = "Please enter valid 24-hour sleep time"
            return
        }
        errorMsg = null

        // 14 mins to fall asleep + 90 min cycles (5 or 6 cycles recommended)
        val bedTime = LocalTime.of(h, m)
        val wake5Cycles = bedTime.plusMinutes(14 + (5 * 90))
        val wake6Cycles = bedTime.plusMinutes(14 + (6 * 90))

        primaryVal = "Recommended Wake-up: $wake6Cycles (9 Hours)"
        details = listOf(
            "Optimal 5 Cycles (7.5 Hours)" to "$wake5Cycles",
            "Optimal 6 Cycles (9.0 Hours)" to "$wake6Cycles"
        )
        onSaveHistory("Sleep wake-up time for bed at $h:$m", primaryVal)
    }

    CalculatorLayout(
        title = "Sleep Cycle Calculator", categoryName = "☀️ Daily Life", isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite, onBackClick = onBackClick,
        formulaText = "Optimal Wake Up = Sleep Time + 14 mins + (N × 90 min REM Cycles)",
        explanationText = "Calculate best wake-up times to feel refreshed based on 90-minute human REM sleep cycles."
    ) {
        Text("Bedtime (24-Hour)", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CalcInputField(value = bedHoursStr, onValueChange = { bedHoursStr = it }, label = "Hours (0-23)", modifier = Modifier.weight(1f))
            CalcInputField(value = bedMinsStr, onValueChange = { bedMinsStr = it }, label = "Mins (0-59)", modifier = Modifier.weight(1f))
        }

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) { Text("Calculate Wake-up Times") }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { bedHoursStr = "23"; bedMinsStr = "00"; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}
