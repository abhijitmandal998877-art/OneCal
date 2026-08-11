package com.example.ui.calculators

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.CalcInputField
import com.example.ui.components.CalculatorLayout
import com.example.ui.components.ResultCard
import kotlin.math.*

// 1. Marks Percentage Calculator
@Composable
fun MarksPercentCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var obtainedStr by remember { mutableStateOf("") }
    var totalStr by remember { mutableStateOf("") }

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val obtained = obtainedStr.toDoubleOrNull()
        val total = totalStr.toDoubleOrNull()

        if (obtained == null || total == null || total <= 0 || obtained < 0 || obtained > total) {
            errorMsg = "Please enter valid marks (Obtained ≤ Total)"
            return
        }
        errorMsg = null

        val pct = (obtained / total) * 100.0
        primaryVal = "Percentage: ${"%.2f".format(pct)}%"
        details = listOf(
            "Obtained Marks" to "$obtained",
            "Total Marks" to "$total",
            "Lost Marks" to "${total - obtained}"
        )
        onSaveHistory("Marks % for $obtained/$total", primaryVal)
    }

    CalculatorLayout(
        title = "Marks Percentage Calculator",
        categoryName = "🎓 Student",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "Percentage = (Obtained Marks / Total Marks) × 100",
        explanationText = "Calculates overall percentage from total and scored marks."
    ) {
        CalcInputField(value = obtainedStr, onValueChange = { obtainedStr = it }, label = "Obtained Marks")
        CalcInputField(value = totalStr, onValueChange = { totalStr = it }, label = "Total Maximum Marks")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate Percentage")
        }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { obtainedStr = ""; totalStr = ""; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}

// 2. CGPA Calculator
@Composable
fun CGPACalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var sgpaListStr by remember { mutableStateOf("") }

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val sgpas = sgpaListStr.split(",", " ", "\n").mapNotNull { it.trim().toDoubleOrNull() }

        if (sgpas.isEmpty()) {
            errorMsg = "Please enter semester SGPAs or Grade Points"
            return
        }
        errorMsg = null

        val cgpa = sgpas.average()
        val approxPct = cgpa * 9.5

        primaryVal = "Cumulative CGPA: ${"%.2f".format(cgpa)}"
        details = listOf(
            "Semesters Count" to "${sgpas.size}",
            "Approx. CBSE/Standard Percentage" to "${"%.2f".format(approxPct)}%"
        )
        onSaveHistory("CGPA of ${sgpas.size} semesters", primaryVal)
    }

    CalculatorLayout(
        title = "CGPA Calculator",
        categoryName = "🎓 Student",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "CGPA = Average of all semester SGPAs",
        explanationText = "Enter individual semester SGPAs separated by commas or spaces."
    ) {
        CalcInputField(
            value = sgpaListStr,
            onValueChange = { sgpaListStr = it },
            label = "Enter Semester SGPAs (comma or space separated)",
            placeholder = "e.g. 8.2, 8.5, 7.8, 9.0"
        )

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate CGPA")
        }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { sgpaListStr = ""; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}

// 3. CGPA -> Percentage Converter
@Composable
fun CGPAPercentConverterScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var cgpaStr by remember { mutableStateOf("") }
    var multiplierStr by remember { mutableStateOf("9.5") }

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val cgpa = cgpaStr.toDoubleOrNull()
        val multiplier = multiplierStr.toDoubleOrNull() ?: 9.5

        if (cgpa == null || cgpa < 0 || cgpa > 10) {
            errorMsg = "Please enter valid CGPA (0 to 10)"
            return
        }
        errorMsg = null

        val pct = cgpa * multiplier
        primaryVal = "Equivalent Percentage: ${"%.2f".format(pct)}%"
        details = listOf(
            "CGPA Input" to "$cgpa",
            "Formula Formula Multiplier" to "$multiplier",
            "Board / Formula Standard" to if (multiplier == 9.5) "CBSE / Standard Board" else "Custom Board"
        )
        onSaveHistory("CGPA $cgpa to Percentage", primaryVal)
    }

    CalculatorLayout(
        title = "CGPA → Percentage Converter",
        categoryName = "🎓 Student",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "Percentage = CGPA × Formula Multiplier (default 9.5)",
        explanationText = "Convert CGPA to percentage. Configurable for different university and board formulas."
    ) {
        CalcInputField(value = cgpaStr, onValueChange = { cgpaStr = it }, label = "Enter CGPA (0 - 10)")

        Text("Select Conversion Standard", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = multiplierStr == "9.5", onClick = { multiplierStr = "9.5" }, label = { Text("CBSE (9.5×)") })
            FilterChip(selected = multiplierStr == "10.0", onClick = { multiplierStr = "10.0" }, label = { Text("Standard (10×)") })
            FilterChip(selected = multiplierStr == "9.0", onClick = { multiplierStr = "9.0" }, label = { Text("AICTE (9.0×)") })
        }

        CalcInputField(value = multiplierStr, onValueChange = { multiplierStr = it }, label = "Custom Multiplier Formula")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Convert to Percentage")
        }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { cgpaStr = ""; multiplierStr = "9.5"; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}

// 4. GPA Calculator
@Composable
fun GPACalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var inputsStr by remember { mutableStateOf("") }

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val lines = inputsStr.split("\n", ";").map { it.trim() }.filter { it.isNotEmpty() }
        if (lines.isEmpty()) {
            errorMsg = "Enter Grade Point and Credit Pairs (e.g., 10,4 or 8,3)"
            return
        }

        var totalPoints = 0.0
        var totalCredits = 0.0

        for (line in lines) {
            val parts = line.split(",", " ", ":").mapNotNull { it.toDoubleOrNull() }
            if (parts.size >= 2) {
                val gradePt = parts[0]
                val credit = parts[1]
                totalPoints += (gradePt * credit)
                totalCredits += credit
            }
        }

        if (totalCredits <= 0) {
            errorMsg = "Please enter valid (GradePoint, Credits) pairs"
            return
        }
        errorMsg = null

        val gpa = totalPoints / totalCredits
        primaryVal = "GPA: ${"%.2f".format(gpa)}"
        details = listOf(
            "Total Grade Points" to "%.2f".format(totalPoints),
            "Total Credits" to "%.1f".format(totalCredits)
        )
        onSaveHistory("GPA for $totalCredits credits", primaryVal)
    }

    CalculatorLayout(
        title = "Semester GPA Calculator",
        categoryName = "🎓 Student",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "GPA = Σ(Grade Point × Credits) / Σ(Credits)",
        explanationText = "Enter each course Grade Point (0-10) and Credits separated by comma per line."
    ) {
        CalcInputField(
            value = inputsStr,
            onValueChange = { inputsStr = it },
            label = "Enter (GradePoint, Credits) per line",
            placeholder = "10, 4\n9, 3\n8, 4\n7, 2"
        )

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate GPA")
        }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { inputsStr = ""; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}

// 5. Average Marks Calculator
@Composable
fun AvgMarksCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var marksStr by remember { mutableStateOf("") }

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val marks = marksStr.split(",", " ", "\n").mapNotNull { it.trim().toDoubleOrNull() }
        if (marks.isEmpty()) {
            errorMsg = "Please enter subject marks"
            return
        }
        errorMsg = null

        val sum = marks.sum()
        val count = marks.size
        val avg = sum / count

        primaryVal = "Average Marks: ${"%.2f".format(avg)}"
        details = listOf(
            "Total Subjects" to "$count",
            "Total Scored Marks" to "${"%.2f".format(sum)}",
            "Highest Score" to "${marks.maxOrNull()}",
            "Lowest Score" to "${marks.minOrNull()}"
        )
        onSaveHistory("Average marks for $count subjects", primaryVal)
    }

    CalculatorLayout(
        title = "Average Marks Calculator",
        categoryName = "🎓 Student",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "Average = Total Marks Scored / Number of Subjects",
        explanationText = "Compute mean score across all subjects."
    ) {
        CalcInputField(value = marksStr, onValueChange = { marksStr = it }, label = "Enter Subject Scores (comma or space separated)", placeholder = "85, 92, 78, 88, 90")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate Average")
        }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { marksStr = ""; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}

// 6. Required Marks Calculator
@Composable
fun ReqMarksCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var currentScoredStr by remember { mutableStateOf("") }
    var currentMaxStr by remember { mutableStateOf("") }
    var finalWeightStr by remember { mutableStateOf("") }
    var targetPctStr by remember { mutableStateOf("75") }

    var primaryVal by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val currScore = currentScoredStr.toDoubleOrNull()
        val currMax = currentMaxStr.toDoubleOrNull()
        val finalMax = finalWeightStr.toDoubleOrNull()
        val target = targetPctStr.toDoubleOrNull() ?: 75.0

        if (currScore == null || currMax == null || finalMax == null || currMax <= 0 || finalMax <= 0) {
            errorMsg = "Please enter valid current and final exam marks"
            return
        }
        errorMsg = null

        val totalMax = currMax + finalMax
        val totalNeeded = (target / 100.0) * totalMax
        val neededInFinal = totalNeeded - currScore

        if (neededInFinal <= 0) {
            primaryVal = "Target Achieved! You already have enough marks."
        } else if (neededInFinal > finalMax) {
            primaryVal = "Impossible: You need ${"%.1f".format(neededInFinal)} out of $finalMax marks."
        } else {
            val pctNeeded = (neededInFinal / finalMax) * 100.0
            primaryVal = "You Need: ${"%.1f".format(neededInFinal)} / $finalMax (${"%.1f".format(pctNeeded)}%) in Final Exam"
        }
        onSaveHistory("Target $target% Required Marks", primaryVal)
    }

    CalculatorLayout(
        title = "Required Marks Calculator",
        categoryName = "🎓 Student",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "Required Final Score = (Target% × Total Max) - Current Scored Marks",
        explanationText = "Find score needed in final exam to achieve overall target grade."
    ) {
        CalcInputField(value = currentScoredStr, onValueChange = { currentScoredStr = it }, label = "Current Total Scored Marks")
        CalcInputField(value = currentMaxStr, onValueChange = { currentMaxStr = it }, label = "Current Maximum Marks So Far")
        CalcInputField(value = finalWeightStr, onValueChange = { finalWeightStr = it }, label = "Final Exam Maximum Marks")
        CalcInputField(value = targetPctStr, onValueChange = { targetPctStr = it }, label = "Target Overall Percentage (%)", suffix = "%")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate Needed Score")
        }

        ResultCard(primaryValue = primaryVal, onReset = { currentScoredStr = ""; currentMaxStr = ""; finalWeightStr = ""; targetPctStr = "75"; primaryVal = ""; errorMsg = null })
    }
}

// 7. Grade Calculator
@Composable
fun GradeCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var pctStr by remember { mutableStateOf("") }

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val pct = pctStr.toDoubleOrNull()
        if (pct == null || pct < 0 || pct > 100) {
            errorMsg = "Please enter valid percentage (0 - 100%)"
            return
        }
        errorMsg = null

        val (grade, gpa, remark) = when {
            pct >= 90 -> Triple("A+", "10.0", "Outstanding")
            pct >= 80 -> Triple("A", "9.0", "Excellent")
            pct >= 70 -> Triple("B+", "8.0", "Very Good")
            pct >= 60 -> Triple("B", "7.0", "Good")
            pct >= 50 -> Triple("C", "6.0", "Average")
            pct >= 40 -> Triple("D", "5.0", "Pass")
            else -> Triple("F", "0.0", "Fail")
        }

        primaryVal = "Grade: $grade"
        details = listOf(
            "Grade Point" to gpa,
            "Performance" to remark
        )
        onSaveHistory("Grade for $pct%", primaryVal)
    }

    CalculatorLayout(
        title = "Grade Calculator",
        categoryName = "🎓 Student",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "Standard Letter Grade Mapping Scale",
        explanationText = "Map percentage score to letter grades A+, A, B+, B, C, D, F."
    ) {
        CalcInputField(value = pctStr, onValueChange = { pctStr = it }, label = "Percentage Score (%)", suffix = "%")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Determine Grade")
        }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { pctStr = ""; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}

// 8. Physics Calculator
@Composable
fun PhysicsCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Force (F=ma), 1: Kinetic Energy (1/2 mv²)
    var val1 by remember { mutableStateOf("") }
    var val2 by remember { mutableStateOf("") }

    var primaryVal by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val v1 = val1.toDoubleOrNull()
        val v2 = val2.toDoubleOrNull()

        if (v1 == null || v2 == null) {
            errorMsg = "Please enter valid numerical inputs"
            return
        }
        errorMsg = null

        if (selectedTab == 0) {
            val force = v1 * v2
            primaryVal = "Force (F): ${"%.2f".format(force)} N"
            onSaveHistory("Force F = $v1 kg × $v2 m/s²", primaryVal)
        } else {
            val ke = 0.5 * v1 * v2 * v2
            primaryVal = "Kinetic Energy (KE): ${"%.2f".format(ke)} J"
            onSaveHistory("KE = 1/2 × $v1 × ($v2)²", primaryVal)
        }
    }

    CalculatorLayout(
        title = "Physics Calculator",
        categoryName = "🎓 Student",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "Force: F = m × a  |  Kinetic Energy: KE = ½ × m × v²",
        explanationText = "Calculate mechanics forces and kinetic energies."
    ) {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0; primaryVal = "" }, text = { Text("Force (F=ma)") })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1; primaryVal = "" }, text = { Text("Kinetic Energy") })
        }

        if (selectedTab == 0) {
            CalcInputField(value = val1, onValueChange = { val1 = it }, label = "Mass (m)", suffix = "kg")
            CalcInputField(value = val2, onValueChange = { val2 = it }, label = "Acceleration (a)", suffix = "m/s²")
        } else {
            CalcInputField(value = val1, onValueChange = { val1 = it }, label = "Mass (m)", suffix = "kg")
            CalcInputField(value = val2, onValueChange = { val2 = it }, label = "Velocity (v)", suffix = "m/s")
        }

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate")
        }

        ResultCard(primaryValue = primaryVal, onReset = { val1 = ""; val2 = ""; primaryVal = ""; errorMsg = null })
    }
}

// 9. Geometry Calculator
@Composable
fun GeometryCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var shapeIndex by remember { mutableStateOf(0) } // 0: Circle, 1: Rectangle, 2: Triangle, 3: Cylinder
    var val1 by remember { mutableStateOf("") }
    var val2 by remember { mutableStateOf("") }

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val v1 = val1.toDoubleOrNull()
        val v2 = val2.toDoubleOrNull()

        if (v1 == null || v1 <= 0) {
            errorMsg = "Please enter valid positive dimensions"
            return
        }
        errorMsg = null

        when (shapeIndex) {
            0 -> {
                val area = Math.PI * v1 * v1
                val perimeter = 2 * Math.PI * v1
                primaryVal = "Area: ${"%.2f".format(area)} sq units"
                details = listOf("Perimeter (Circumference)" to "${"%.2f".format(perimeter)} units")
            }
            1 -> {
                if (v2 == null || v2 <= 0) { errorMsg = "Please enter width"; return }
                val area = v1 * v2
                val perimeter = 2 * (v1 + v2)
                primaryVal = "Area: ${"%.2f".format(area)} sq units"
                details = listOf("Perimeter" to "${"%.2f".format(perimeter)} units")
            }
            2 -> {
                if (v2 == null || v2 <= 0) { errorMsg = "Please enter height"; return }
                val area = 0.5 * v1 * v2
                primaryVal = "Area: ${"%.2f".format(area)} sq units"
                details = emptyList()
            }
            3 -> {
                if (v2 == null || v2 <= 0) { errorMsg = "Please enter height"; return }
                val volume = Math.PI * v1 * v1 * v2
                val surfaceArea = 2 * Math.PI * v1 * (v1 + v2)
                primaryVal = "Volume: ${"%.2f".format(volume)} cu units"
                details = listOf("Total Surface Area" to "${"%.2f".format(surfaceArea)} sq units")
            }
        }
        onSaveHistory("Geometry Shape Calculation", primaryVal)
    }

    CalculatorLayout(
        title = "Geometry Calculator",
        categoryName = "🎓 Student",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "Area, Perimeter & Volume for 2D/3D shapes",
        explanationText = "Calculate properties for Circle, Rectangle, Triangle, and Cylinder."
    ) {
        ScrollableTabRow(selectedTabIndex = shapeIndex) {
            listOf("Circle", "Rectangle", "Triangle", "Cylinder").forEachIndexed { idx, name ->
                Tab(selected = shapeIndex == idx, onClick = { shapeIndex = idx; primaryVal = ""; details = emptyList() }, text = { Text(name) })
            }
        }

        when (shapeIndex) {
            0 -> CalcInputField(value = val1, onValueChange = { val1 = it }, label = "Radius (r)")
            1 -> {
                CalcInputField(value = val1, onValueChange = { val1 = it }, label = "Length (l)")
                CalcInputField(value = val2, onValueChange = { val2 = it }, label = "Width (w)")
            }
            2 -> {
                CalcInputField(value = val1, onValueChange = { val1 = it }, label = "Base (b)")
                CalcInputField(value = val2, onValueChange = { val2 = it }, label = "Height (h)")
            }
            3 -> {
                CalcInputField(value = val1, onValueChange = { val1 = it }, label = "Radius (r)")
                CalcInputField(value = val2, onValueChange = { val2 = it }, label = "Height (h)")
            }
        }

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate Geometry")
        }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { val1 = ""; val2 = ""; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}

// 10. Trigonometry Calculator
@Composable
fun TrigCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var angleDegStr by remember { mutableStateOf("") }

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val deg = angleDegStr.toDoubleOrNull()
        if (deg == null) {
            errorMsg = "Please enter valid angle in degrees"
            return
        }
        errorMsg = null

        val rad = Math.toRadians(deg)
        val sinVal = sin(rad)
        val cosVal = cos(rad)
        val tanVal = if (abs(cosVal) < 1e-10) Double.NaN else tan(rad)

        primaryVal = "sin($deg°) = ${"%.4f".format(sinVal)}"
        details = listOf(
            "cos($deg°)" to "%.4f".format(cosVal),
            "tan($deg°)" to if (tanVal.isNaN()) "Undefined" else "%.4f".format(tanVal),
            "Radians" to "%.4f rad".format(rad)
        )
        onSaveHistory("Trig ratios for $deg°", primaryVal)
    }

    CalculatorLayout(
        title = "Trigonometry Calculator",
        categoryName = "🎓 Student",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "sin, cos, tan for angle θ in degrees",
        explanationText = "Calculate trigonometric functions and radian conversions."
    ) {
        CalcInputField(value = angleDegStr, onValueChange = { angleDegStr = it }, label = "Angle in Degrees (°)", suffix = "°")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate Trig Values")
        }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { angleDegStr = ""; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}

// 11. Speed-Distance-Time Calculator
@Composable
fun SDTCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var speedStr by remember { mutableStateOf("") }
    var distStr by remember { mutableStateOf("") }
    var timeStr by remember { mutableStateOf("") }

    var primaryVal by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val s = speedStr.toDoubleOrNull()
        val d = distStr.toDoubleOrNull()
        val t = timeStr.toDoubleOrNull()

        val count = listOf(s, d, t).count { it != null }
        if (count != 2) {
            errorMsg = "Enter exactly 2 values to calculate the 3rd missing value"
            return
        }
        errorMsg = null

        if (s == null) {
            if (t == 0.0) { errorMsg = "Time cannot be 0"; return }
            val speed = d!! / t!!
            primaryVal = "Speed = ${"%.2f".format(speed)} km/h"
        } else if (d == null) {
            val dist = s * t!!
            primaryVal = "Distance = ${"%.2f".format(dist)} km"
        } else if (t == null) {
            if (s == 0.0) { errorMsg = "Speed cannot be 0"; return }
            val time = d / s
            primaryVal = "Time = ${"%.2f".format(time)} hours (${"%.0f".format(time * 60)} mins)"
        }
        onSaveHistory("Speed-Distance-Time", primaryVal)
    }

    CalculatorLayout(
        title = "Speed-Distance-Time Calculator",
        categoryName = "🎓 Student",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "Speed = Distance / Time  |  Distance = Speed × Time",
        explanationText = "Leave one field empty to calculate it from the other two."
    ) {
        CalcInputField(value = speedStr, onValueChange = { speedStr = it }, label = "Speed (km/h)", suffix = "km/h")
        CalcInputField(value = distStr, onValueChange = { distStr = it }, label = "Distance (km)", suffix = "km")
        CalcInputField(value = timeStr, onValueChange = { timeStr = it }, label = "Time (Hours)", suffix = "hrs")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate Missing Value")
        }

        ResultCard(primaryValue = primaryVal, onReset = { speedStr = ""; distStr = ""; timeStr = ""; primaryVal = ""; errorMsg = null })
    }
}

// 12. Work & Power Calculator
@Composable
fun WorkPowerCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var forceStr by remember { mutableStateOf("") }
    var distStr by remember { mutableStateOf("") }
    var timeStr by remember { mutableStateOf("") }

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val F = forceStr.toDoubleOrNull()
        val d = distStr.toDoubleOrNull()
        val t = timeStr.toDoubleOrNull()

        if (F == null || d == null) {
            errorMsg = "Please enter Force and Distance"
            return
        }
        errorMsg = null

        val work = F * d
        if (t != null && t > 0) {
            val power = work / t
            primaryVal = "Power = ${"%.2f".format(power)} Watts"
            details = listOf("Work Done" to "${"%.2f".format(work)} Joules")
        } else {
            primaryVal = "Work Done = ${"%.2f".format(work)} Joules"
            details = emptyList()
        }
        onSaveHistory("Work & Power", primaryVal)
    }

    CalculatorLayout(
        title = "Work & Power Calculator",
        categoryName = "🎓 Student",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "Work (J) = Force (N) × Distance (m)  |  Power (W) = Work (J) / Time (s)",
        explanationText = "Calculate mechanical work done and power output."
    ) {
        CalcInputField(value = forceStr, onValueChange = { forceStr = it }, label = "Force (F)", suffix = "N")
        CalcInputField(value = distStr, onValueChange = { distStr = it }, label = "Distance (d)", suffix = "m")
        CalcInputField(value = timeStr, onValueChange = { timeStr = it }, label = "Time (optional)", suffix = "s")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate Work & Power")
        }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { forceStr = ""; distStr = ""; timeStr = ""; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}
