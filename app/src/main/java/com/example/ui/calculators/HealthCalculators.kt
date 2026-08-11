package com.example.ui.calculators

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.components.CalcInputField
import com.example.ui.components.CalculatorLayout
import com.example.ui.components.ResultCard

// 1. BMI Calculator
@Composable
fun BMICalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var weightStr by remember { mutableStateOf("") }
    var heightCmStr by remember { mutableStateOf("") }

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val weight = weightStr.toDoubleOrNull()
        val heightCm = heightCmStr.toDoubleOrNull()

        if (weight == null || heightCm == null || weight <= 0 || heightCm <= 0) {
            errorMsg = "Please enter valid weight (kg) and height (cm)"
            return
        }
        errorMsg = null

        val heightM = heightCm / 100.0
        val bmi = weight / (heightM * heightM)

        val (category, suggestion) = when {
            bmi < 18.5 -> "Underweight" to "Consider increasing calorie intake."
            bmi in 18.5..24.9 -> "Normal Weight" to "Great job! Maintain your healthy weight."
            bmi in 25.0..29.9 -> "Overweight" to "Incorporate daily exercise and balanced diet."
            else -> "Obese" to "Consult a healthcare professional for guidance."
        }

        primaryVal = "BMI: ${"%.1f".format(bmi)} ($category)"
        details = listOf(
            "Category" to category,
            "Healthy BMI Range" to "18.5 - 24.9 kg/m²",
            "Health Tip" to suggestion
        )
        onSaveHistory("BMI ${"%.1f".format(bmi)} ($category)", primaryVal)
    }

    CalculatorLayout(
        title = "BMI Calculator", categoryName = "❤️ Health", isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite, onBackClick = onBackClick,
        formulaText = "BMI = Weight (kg) / [Height (m)]²",
        explanationText = "Calculate Body Mass Index according to World Health Organization (WHO) standards."
    ) {
        CalcInputField(value = weightStr, onValueChange = { weightStr = it }, label = "Weight in Kilograms (kg)", suffix = "kg")
        CalcInputField(value = heightCmStr, onValueChange = { heightCmStr = it }, label = "Height in Centimeters (cm)", suffix = "cm")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) { Text("Calculate BMI") }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { weightStr = ""; heightCmStr = ""; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}

// 2. BMR Calculator
@Composable
fun BMRCalculatorScreen(isFavorite: Boolean, onToggleFavorite: () -> Unit, onBackClick: () -> Unit, onSaveHistory: (String, String) -> Unit) {
    CalorieCalculatorScreen(isFavorite, onToggleFavorite, onBackClick, onSaveHistory)
}

// 3. Body Fat Percentage Calculator
@Composable
fun BodyFatCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var waistStr by remember { mutableStateOf("") }
    var neckStr by remember { mutableStateOf("") }
    var heightStr by remember { mutableStateOf("") }

    var primaryVal by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val waist = waistStr.toDoubleOrNull()
        val neck = neckStr.toDoubleOrNull()
        val height = heightStr.toDoubleOrNull()

        if (waist == null || neck == null || height == null || waist <= neck || height <= 0) {
            errorMsg = "Please enter valid circumference measurements (Waist > Neck)"
            return
        }
        errorMsg = null

        // US Navy Body Fat estimation formula
        val bodyFatPct = 495.0 / (1.0324 - 0.19077 * Math.log10(waist - neck) + 0.15456 * Math.log10(height)) - 450.0
        primaryVal = "Estimated Body Fat: ${"%.1f".format(bodyFatPct)}%"
        onSaveHistory("Body Fat ${"%.1f".format(bodyFatPct)}%", primaryVal)
    }

    CalculatorLayout(
        title = "Body Fat Calculator", categoryName = "❤️ Health", isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite, onBackClick = onBackClick,
        formulaText = "US Navy Circumference Body Fat Estimation",
        explanationText = "Estimate body fat percentage using waist, neck, and height dimensions."
    ) {
        CalcInputField(value = waistStr, onValueChange = { waistStr = it }, label = "Waist Circumference (cm)", suffix = "cm")
        CalcInputField(value = neckStr, onValueChange = { neckStr = it }, label = "Neck Circumference (cm)", suffix = "cm")
        CalcInputField(value = heightStr, onValueChange = { heightStr = it }, label = "Height (cm)", suffix = "cm")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) { Text("Calculate Body Fat") }

        ResultCard(primaryValue = primaryVal, onReset = { waistStr = ""; neckStr = ""; heightStr = ""; primaryVal = ""; errorMsg = null })
    }
}

// 4. Ideal Body Weight Calculator
@Composable
fun IdealWeightCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var heightCmStr by remember { mutableStateOf("") }
    var isMale by remember { mutableStateOf(true) }

    var primaryVal by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val heightCm = heightCmStr.toDoubleOrNull()
        if (heightCm == null || heightCm < 100) {
            errorMsg = "Please enter valid height (> 100 cm)"
            return
        }
        errorMsg = null

        val heightInches = heightCm / 2.54
        val inchesOver5ft = maxOf(0.0, heightInches - 60.0)

        // Devine Formula
        val idealWeightKg = if (isMale) 50.0 + (2.3 * inchesOver5ft) else 45.5 + (2.3 * inchesOver5ft)

        primaryVal = "Ideal Weight: ${"%.1f".format(idealWeightKg)} kg"
        onSaveHistory("Ideal weight for height $heightCm cm", primaryVal)
    }

    CalculatorLayout(
        title = "Ideal Body Weight Calculator", categoryName = "❤️ Health", isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite, onBackClick = onBackClick,
        formulaText = "Devine Formula: Male = 50 + 2.3 × (Inches > 5ft)",
        explanationText = "Calculate ideal weight for height based on medical Devine formula."
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = isMale, onClick = { isMale = true }, label = { Text("Male") })
            FilterChip(selected = !isMale, onClick = { isMale = false }, label = { Text("Female") })
        }
        CalcInputField(value = heightCmStr, onValueChange = { heightCmStr = it }, label = "Height (cm)", suffix = "cm")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) { Text("Calculate Ideal Weight") }

        ResultCard(primaryValue = primaryVal, onReset = { heightCmStr = ""; primaryVal = ""; errorMsg = null })
    }
}

// 5. Water Intake
@Composable
fun HealthWaterCalculatorScreen(isFavorite: Boolean, onToggleFavorite: () -> Unit, onBackClick: () -> Unit, onSaveHistory: (String, String) -> Unit) {
    WaterIntakeCalculatorScreen(isFavorite, onToggleFavorite, onBackClick, onSaveHistory)
}

// 6. Daily Calorie Needs
@Composable
fun DailyCalorieCalculatorScreen(isFavorite: Boolean, onToggleFavorite: () -> Unit, onBackClick: () -> Unit, onSaveHistory: (String, String) -> Unit) {
    CalorieCalculatorScreen(isFavorite, onToggleFavorite, onBackClick, onSaveHistory)
}
