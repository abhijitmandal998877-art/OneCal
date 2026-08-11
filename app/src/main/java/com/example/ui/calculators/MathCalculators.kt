package com.example.ui.calculators

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CalcInputField
import com.example.ui.components.CalculatorLayout
import com.example.ui.components.ResultCard
import java.math.BigInteger
import java.time.LocalDate
import java.time.Period
import kotlin.math.*

// 1. Standard Calculator
@Composable
fun StandardCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var display by remember { mutableStateOf("0") }
    var expression by remember { mutableStateOf("") }
    var lastOp by remember { mutableStateOf("") }
    var storedVal by remember { mutableStateOf(0.0) }
    var newNum by remember { mutableStateOf(true) }

    fun onNum(n: String) {
        if (newNum || display == "0") {
            display = n
            newNum = false
        } else {
            display += n
        }
    }

    fun onOp(op: String) {
        val curr = display.toDoubleOrNull() ?: 0.0
        if (lastOp.isNotEmpty() && !newNum) {
            when (lastOp) {
                "+" -> storedVal += curr
                "-" -> storedVal -= curr
                "×" -> storedVal *= curr
                "÷" -> storedVal = if (curr != 0.0) storedVal / curr else Double.NaN
            }
            display = if (storedVal.isNaN()) "Error" else if (storedVal % 1.0 == 0.0) storedVal.toLong().toString() else "%.4f".format(storedVal)
        } else {
            storedVal = curr
        }
        lastOp = op
        expression = "$storedVal $op"
        newNum = true
    }

    fun onEquals() {
        if (lastOp.isEmpty()) return
        val curr = display.toDoubleOrNull() ?: 0.0
        val res = when (lastOp) {
            "+" -> storedVal + curr
            "-" -> storedVal - curr
            "×" -> storedVal * curr
            "÷" -> if (curr != 0.0) storedVal / curr else Double.NaN
            else -> curr
        }
        val resStr = if (res.isNaN()) "Error (Div by 0)" else if (res % 1.0 == 0.0) res.toLong().toString() else "%.6f".format(res)
        val expSummary = "$storedVal $lastOp $curr = $resStr"
        display = resStr
        expression = expSummary
        onSaveHistory("Expr: $storedVal $lastOp $curr", "Result: $resStr")
        lastOp = ""
        newNum = true
    }

    fun onClear() {
        display = "0"
        expression = ""
        lastOp = ""
        storedVal = 0.0
        newNum = true
    }

    CalculatorLayout(
        title = "Standard Calculator",
        categoryName = "🔢 Math",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "Standard arithmetic operations",
        explanationText = "Perform quick calculations with addition, subtraction, multiplication, and division."
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                Text(text = expression, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = display, style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val buttons = listOf(
            listOf("C", "±", "%", "÷"),
            listOf("7", "8", "9", "×"),
            listOf("4", "5", "6", "-"),
            listOf("1", "2", "3", "+"),
            listOf("0", ".", "⌫", "=")
        )

        buttons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { btn ->
                    val isOp = btn in listOf("÷", "×", "-", "+", "=")
                    val isSpecial = btn in listOf("C", "±", "%", "⌫")
                    Button(
                        onClick = {
                            when (btn) {
                                "C" -> onClear()
                                "⌫" -> {
                                    if (display.length > 1) display = display.dropLast(1) else display = "0"
                                }
                                "±" -> {
                                    val d = display.toDoubleOrNull() ?: 0.0
                                    display = if (d % 1.0 == 0.0) (-d.toLong()).toString() else (-d).toString()
                                }
                                "%" -> {
                                    val d = (display.toDoubleOrNull() ?: 0.0) / 100.0
                                    display = d.toString()
                                }
                                in listOf("÷", "×", "-", "+") -> onOp(btn)
                                "=" -> onEquals()
                                else -> onNum(btn)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = if (isOp) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        else if (isSpecial) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                        else ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface, contentColor = MaterialTheme.colorScheme.onSurface)
                    ) {
                        Text(text = btn, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// 2. Scientific Calculator
@Composable
fun ScientificCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var numInput by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculateFunc(funcName: String) {
        val valDouble = numInput.toDoubleOrNull()
        if (valDouble == null) {
            errorMsg = "Please enter a valid number"
            return
        }
        errorMsg = null
        val res = when (funcName) {
            "sin" -> sin(Math.toRadians(valDouble))
            "cos" -> cos(Math.toRadians(valDouble))
            "tan" -> tan(Math.toRadians(valDouble))
            "sqrt" -> if (valDouble >= 0) sqrt(valDouble) else Double.NaN
            "log" -> if (valDouble > 0) log10(valDouble) else Double.NaN
            "ln" -> if (valDouble > 0) ln(valDouble) else Double.NaN
            "factorial" -> {
                if (valDouble >= 0 && valDouble == floor(valDouble) && valDouble <= 20) {
                    var f = 1L
                    for (i in 1..valDouble.toLong()) f *= i
                    f.toDouble()
                } else Double.NaN
            }
            "square" -> valDouble * valDouble
            else -> Double.NaN
        }

        if (res.isNaN()) {
            errorMsg = "Math Error / Invalid Input"
            resultText = ""
        } else {
            val formatted = if (res % 1.0 == 0.0) res.toLong().toString() else "%.6f".format(res)
            resultText = "$funcName($numInput) = $formatted"
            onSaveHistory("Function: $funcName($numInput)", "Result: $formatted")
        }
    }

    CalculatorLayout(
        title = "Scientific Calculator",
        categoryName = "🔢 Math",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "sin, cos, tan, log, ln, x², √x, x!",
        explanationText = "Perform trigonometric (degrees), logarithmic, and factorial calculations."
    ) {
        CalcInputField(
            value = numInput,
            onValueChange = { numInput = it; errorMsg = null },
            label = "Enter Value (Angle in Degrees for Trig)",
            placeholder = "e.g. 45 or 100",
            errorMessage = errorMsg
        )

        val funcs = listOf(
            "sin" to "sin(x)", "cos" to "cos(x)", "tan" to "tan(x)",
            "sqrt" to "√x", "log" to "log₁₀", "ln" to "ln",
            "square" to "x²", "factorial" to "x!"
        )

        Text(text = "Scientific Functions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            funcs.chunked(3).forEach { chunk ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    chunk.forEach { (key, label) ->
                        Button(
                            onClick = { calculateFunc(key) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(label, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        ResultCard(
            primaryValue = resultText,
            onReset = { numInput = ""; resultText = ""; errorMsg = null }
        )
    }
}

// 3. Percentage Calculator
@Composable
fun PercentageCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var val1 by remember { mutableStateOf("") }
    var val2 by remember { mutableStateOf("") }
    var calcType by remember { mutableStateOf(0) } // 0: X% of Y, 1: X is what % of Y, 2: % Increase/Decrease
    var resultText by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val n1 = val1.toDoubleOrNull()
        val n2 = val2.toDoubleOrNull()
        if (n1 == null || n2 == null) {
            errorMsg = "Please enter both values"
            return
        }
        errorMsg = null
        when (calcType) {
            0 -> {
                val res = (n1 / 100.0) * n2
                resultText = "$n1% of $n2 = ${"%.2f".format(res)}"
                onSaveHistory("$n1% of $n2", resultText)
            }
            1 -> {
                if (n2 == 0.0) { errorMsg = "Cannot divide by zero"; return }
                val res = (n1 / n2) * 100.0
                resultText = "$n1 is ${"%.2f".format(res)}% of $n2"
                onSaveHistory("$n1 is what % of $n2", resultText)
            }
            2 -> {
                if (n1 == 0.0) { errorMsg = "Initial value cannot be 0"; return }
                val diff = n2 - n1
                val pct = (diff / n1) * 100.0
                val status = if (pct >= 0) "Increase" else "Decrease"
                resultText = "${abs(pct).let { "%.2f".format(it) }}% $status (Change: ${"%.2f".format(diff)})"
                onSaveHistory("Change from $n1 to $n2", resultText)
            }
        }
    }

    CalculatorLayout(
        title = "Percentage Calculator",
        categoryName = "🔢 Math",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "Percentage = (Part / Total) × 100",
        explanationText = "Easily calculate percentage parts, relative percentages, and percentage changes."
    ) {
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(selected = calcType == 0, onClick = { calcType = 0; resultText = "" }, shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3)) { Text("X% of Y") }
            SegmentedButton(selected = calcType == 1, onClick = { calcType = 1; resultText = "" }, shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3)) { Text("X is % of Y") }
            SegmentedButton(selected = calcType == 2, onClick = { calcType = 2; resultText = "" }, shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3)) { Text("Change %") }
        }

        when (calcType) {
            0 -> {
                CalcInputField(value = val1, onValueChange = { val1 = it }, label = "Percentage (X)", suffix = "%")
                CalcInputField(value = val2, onValueChange = { val2 = it }, label = "Total Value (Y)")
            }
            1 -> {
                CalcInputField(value = val1, onValueChange = { val1 = it }, label = "Part Value (X)")
                CalcInputField(value = val2, onValueChange = { val2 = it }, label = "Total Value (Y)")
            }
            2 -> {
                CalcInputField(value = val1, onValueChange = { val1 = it }, label = "Initial Value")
                CalcInputField(value = val2, onValueChange = { val2 = it }, label = "Final Value")
            }
        }

        if (errorMsg != null) Text(text = errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate")
        }

        ResultCard(primaryValue = resultText, onReset = { val1 = ""; val2 = ""; resultText = ""; errorMsg = null })
    }
}

// 4. Fraction Calculator
@Composable
fun FractionCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var num1 by remember { mutableStateOf("") }
    var den1 by remember { mutableStateOf("") }
    var num2 by remember { mutableStateOf("") }
    var den2 by remember { mutableStateOf("") }
    var op by remember { mutableStateOf("+") }
    var resultText by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun gcd(a: Long, b: Long): Long = if (b == 0L) abs(a) else gcd(b, a % b)

    fun calculate() {
        val n1 = num1.toLongOrNull()
        val d1 = den1.toLongOrNull()
        val n2 = num2.toLongOrNull()
        val d2 = den2.toLongOrNull()

        if (n1 == null || d1 == null || n2 == null || d2 == null) {
            errorMsg = "Please enter valid integer numerators and denominators"
            return
        }
        if (d1 == 0L || d2 == 0L) {
            errorMsg = "Denominator cannot be zero"
            return
        }
        errorMsg = null

        var resNum = 0L
        var resDen = 1L

        when (op) {
            "+" -> { resNum = n1 * d2 + n2 * d1; resDen = d1 * d2 }
            "-" -> { resNum = n1 * d2 - n2 * d1; resDen = d1 * d2 }
            "×" -> { resNum = n1 * n2; resDen = d1 * d2 }
            "÷" -> {
                if (n2 == 0L) { errorMsg = "Cannot divide by zero fraction"; return }
                resNum = n1 * d2; resDen = d1 * n2
            }
        }

        if (resDen < 0) { resNum = -resNum; resDen = -resDen }
        val g = gcd(resNum, resDen)
        val simpNum = resNum / g
        val simpDen = resDen / g

        val fracStr = if (simpDen == 1L) "$simpNum" else "$simpNum/$simpDen"
        val decVal = simpNum.toDouble() / simpDen.toDouble()
        resultText = "$fracStr  (Decimal: ${"%.4f".format(decVal)})"
        onSaveHistory("($n1/$d1) $op ($n2/$d2)", resultText)
    }

    CalculatorLayout(
        title = "Fraction Calculator",
        categoryName = "🔢 Math",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "a/b ± c/d = (ad ± bc)/bd",
        explanationText = "Performs exact fractional arithmetic and simplifies results to lowest terms."
    ) {
        Text("First Fraction", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CalcInputField(value = num1, onValueChange = { num1 = it }, label = "Numerator 1", modifier = Modifier.weight(1f))
            CalcInputField(value = den1, onValueChange = { den1 = it }, label = "Denominator 1", modifier = Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            listOf("+", "-", "×", "÷").forEach { o ->
                FilterChip(
                    selected = op == o,
                    onClick = { op = o },
                    label = { Text(o, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Text("Second Fraction", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CalcInputField(value = num2, onValueChange = { num2 = it }, label = "Numerator 2", modifier = Modifier.weight(1f))
            CalcInputField(value = den2, onValueChange = { den2 = it }, label = "Denominator 2", modifier = Modifier.weight(1f))
        }

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate Fraction")
        }

        ResultCard(primaryValue = resultText, onReset = { num1 = ""; den1 = ""; num2 = ""; den2 = ""; resultText = ""; errorMsg = null })
    }
}

// 5. Ratio Calculator
@Composable
fun RatioCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var a by remember { mutableStateOf("") }
    var b by remember { mutableStateOf("") }
    var c by remember { mutableStateOf("") }
    var d by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val valA = a.toDoubleOrNull()
        val valB = b.toDoubleOrNull()
        val valC = c.toDoubleOrNull()
        val valD = d.toDoubleOrNull()

        // Count how many are provided
        val list = listOf(valA, valB, valC, valD)
        val count = list.count { it != null }

        if (count != 3) {
            errorMsg = "Please enter exactly 3 values to calculate the 4th missing value (A:B = C:D)"
            return
        }
        errorMsg = null

        if (valA == null) {
            if (valD == 0.0) { errorMsg = "D cannot be 0"; return }
            val res = (valB!! * valC!!) / valD!!
            resultText = "A = ${"%.4f".format(res)}"
        } else if (valB == null) {
            if (valC == 0.0) { errorMsg = "C cannot be 0"; return }
            val res = (valA * valD!!) / valC!!
            resultText = "B = ${"%.4f".format(res)}"
        } else if (valC == null) {
            if (valB == 0.0) { errorMsg = "B cannot be 0"; return }
            val res = (valA * valD!!) / valB!!
            resultText = "C = ${"%.4f".format(res)}"
        } else if (valD == null) {
            if (valA == 0.0) { errorMsg = "A cannot be 0"; return }
            val res = (valB * valC) / valA!!
            resultText = "D = ${"%.4f".format(res)}"
        }

        onSaveHistory("Ratio A:B = C:D", resultText)
    }

    CalculatorLayout(
        title = "Ratio Calculator",
        categoryName = "🔢 Math",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "A : B = C : D  ⇒  A × D = B × C",
        explanationText = "Leave one field blank to solve for the missing proportion."
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CalcInputField(value = a, onValueChange = { a = it }, label = "A", modifier = Modifier.weight(1f))
            Text(":", fontSize = 24.sp, modifier = Modifier.align(Alignment.CenterVertically))
            CalcInputField(value = b, onValueChange = { b = it }, label = "B", modifier = Modifier.weight(1f))
        }

        Text("=", fontSize = 24.sp, modifier = Modifier.align(Alignment.CenterHorizontally))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CalcInputField(value = c, onValueChange = { c = it }, label = "C", modifier = Modifier.weight(1f))
            Text(":", fontSize = 24.sp, modifier = Modifier.align(Alignment.CenterVertically))
            CalcInputField(value = d, onValueChange = { d = it }, label = "D", modifier = Modifier.weight(1f))
        }

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Solve Ratio")
        }

        ResultCard(primaryValue = resultText, onReset = { a = ""; b = ""; c = ""; d = ""; resultText = ""; errorMsg = null })
    }
}

// 6. Average Calculator
@Composable
fun AverageCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var inputNumbers by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val nums = inputNumbers.split(",", " ", "\n")
            .mapNotNull { it.trim().toDoubleOrNull() }

        if (nums.isEmpty()) {
            errorMsg = "Please enter numbers separated by comma or space"
            return
        }
        errorMsg = null
        val sum = nums.sum()
        val count = nums.size
        val avg = sum / count
        val min = nums.minOrNull() ?: 0.0
        val max = nums.maxOrNull() ?: 0.0

        resultText = "Average (Mean): ${"%.4f".format(avg)}"
        details = listOf(
            "Count" to "$count",
            "Sum" to "${"%.2f".format(sum)}",
            "Min" to "$min",
            "Max" to "$max"
        )
        onSaveHistory("Average of $count numbers", resultText)
    }

    CalculatorLayout(
        title = "Average Calculator",
        categoryName = "🔢 Math",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "Average = Sum of Numbers / Count of Numbers",
        explanationText = "Enter numbers separated by commas, spaces, or newlines."
    ) {
        CalcInputField(
            value = inputNumbers,
            onValueChange = { inputNumbers = it },
            label = "Enter Numbers (comma or space separated)",
            placeholder = "e.g. 10, 20, 35, 42, 50"
        )

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate Average")
        }

        ResultCard(primaryValue = resultText, details = details, onReset = { inputNumbers = ""; resultText = ""; details = emptyList(); errorMsg = null })
    }
}

// 7. LCM Calculator
@Composable
fun LCMCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var inputNums by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun gcd(a: Long, b: Long): Long = if (b == 0L) abs(a) else gcd(b, a % b)
    fun lcm(a: Long, b: Long): Long = if (a == 0L || b == 0L) 0L else abs(a * b) / gcd(a, b)

    fun calculate() {
        val list = inputNums.split(",", " ", "\n").mapNotNull { it.trim().toLongOrNull() }
        if (list.size < 2) {
            errorMsg = "Please enter at least 2 integer numbers"
            return
        }
        errorMsg = null
        var currentLCM = list[0]
        for (i in 1 until list.size) {
            currentLCM = lcm(currentLCM, list[i])
        }
        resultText = "LCM = $currentLCM"
        onSaveHistory("LCM of ${list.joinToString(", ")}", resultText)
    }

    CalculatorLayout(
        title = "LCM Calculator",
        categoryName = "🔢 Math",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "LCM(a, b) = |a × b| / GCD(a, b)",
        explanationText = "Finds the Lowest Common Multiple for two or more numbers."
    ) {
        CalcInputField(
            value = inputNums,
            onValueChange = { inputNums = it },
            label = "Enter Integers (e.g., 12, 18, 24)",
            placeholder = "12, 18, 24"
        )

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Find LCM")
        }

        ResultCard(primaryValue = resultText, onReset = { inputNums = ""; resultText = ""; errorMsg = null })
    }
}

// 8. HCF / GCD Calculator
@Composable
fun HCFCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var inputNums by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun gcd(a: Long, b: Long): Long = if (b == 0L) abs(a) else gcd(b, a % b)

    fun calculate() {
        val list = inputNums.split(",", " ", "\n").mapNotNull { it.trim().toLongOrNull() }
        if (list.size < 2) {
            errorMsg = "Please enter at least 2 integer numbers"
            return
        }
        errorMsg = null
        var currentGCD = list[0]
        for (i in 1 until list.size) {
            currentGCD = gcd(currentGCD, list[i])
        }
        resultText = "HCF / GCD = $currentGCD"
        onSaveHistory("HCF of ${list.joinToString(", ")}", resultText)
    }

    CalculatorLayout(
        title = "HCF / GCD Calculator",
        categoryName = "🔢 Math",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "Euclidean Algorithm for Greatest Common Divisor",
        explanationText = "Calculates the Highest Common Factor (GCD) of numbers."
    ) {
        CalcInputField(
            value = inputNums,
            onValueChange = { inputNums = it },
            label = "Enter Integers (e.g., 24, 36, 60)",
            placeholder = "24, 36, 60"
        )

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Find HCF / GCD")
        }

        ResultCard(primaryValue = resultText, onReset = { inputNums = ""; resultText = ""; errorMsg = null })
    }
}

// 9. Power Calculator
@Composable
fun PowerCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var baseStr by remember { mutableStateOf("") }
    var expStr by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val b = baseStr.toDoubleOrNull()
        val e = expStr.toDoubleOrNull()
        if (b == null || e == null) {
            errorMsg = "Please enter valid base and exponent values"
            return
        }
        errorMsg = null
        val res = b.pow(e)
        val formatted = if (res % 1.0 == 0.0) res.toLong().toString() else "%.6f".format(res)
        resultText = "$b ^ $e = $formatted"
        onSaveHistory("$b ^ $e", resultText)
    }

    CalculatorLayout(
        title = "Power Calculator",
        categoryName = "🔢 Math",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "Result = Base ^ Exponent",
        explanationText = "Calculate powers for positive, negative, or fractional exponents."
    ) {
        CalcInputField(value = baseStr, onValueChange = { baseStr = it }, label = "Base (x)")
        CalcInputField(value = expStr, onValueChange = { expStr = it }, label = "Exponent (y)")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate Power")
        }

        ResultCard(primaryValue = resultText, onReset = { baseStr = ""; expStr = ""; resultText = ""; errorMsg = null })
    }
}

// 10. Square Root Calculator
@Composable
fun SquareRootCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var valStr by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val v = valStr.toDoubleOrNull()
        if (v == null || v < 0) {
            errorMsg = "Please enter a non-negative number"
            return
        }
        errorMsg = null
        val res = sqrt(v)
        val formatted = if (res % 1.0 == 0.0) res.toLong().toString() else "%.6f".format(res)
        resultText = "√$v = $formatted"
        onSaveHistory("√$v", resultText)
    }

    CalculatorLayout(
        title = "Square Root Calculator",
        categoryName = "🔢 Math",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "√x × √x = x",
        explanationText = "Find the principal square root of any positive real number."
    ) {
        CalcInputField(value = valStr, onValueChange = { valStr = it }, label = "Enter Number (x)")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate Square Root")
        }

        ResultCard(primaryValue = resultText, onReset = { valStr = ""; resultText = ""; errorMsg = null })
    }
}

// 11. Cube Root Calculator
@Composable
fun CubeRootCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var valStr by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val v = valStr.toDoubleOrNull()
        if (v == null) {
            errorMsg = "Please enter a valid number"
            return
        }
        errorMsg = null
        val res = Math.cbrt(v)
        val formatted = if (res % 1.0 == 0.0) res.toLong().toString() else "%.6f".format(res)
        resultText = "∛$v = $formatted"
        onSaveHistory("∛$v", resultText)
    }

    CalculatorLayout(
        title = "Cube Root Calculator",
        categoryName = "🔢 Math",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "y = ∛x  ⇒  y³ = x",
        explanationText = "Calculates the cube root of any real number."
    ) {
        CalcInputField(value = valStr, onValueChange = { valStr = it }, label = "Enter Number (x)")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate Cube Root")
        }

        ResultCard(primaryValue = resultText, onReset = { valStr = ""; resultText = ""; errorMsg = null })
    }
}

// 12. Prime Number Checker
@Composable
fun PrimeCheckerScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var valStr by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val n = valStr.toLongOrNull()
        if (n == null || n < 1) {
            errorMsg = "Please enter a positive integer greater than 0"
            return
        }
        errorMsg = null

        if (n == 1L) {
            resultText = "1 is neither Prime nor Composite"
            details = emptyList()
            return
        }

        var isPrime = true
        val factors = mutableListOf<Long>()
        var i = 1L
        while (i * i <= n) {
            if (n % i == 0L) {
                factors.add(i)
                if (i * i != n) factors.add(n / i)
                if (i > 1) isPrime = false
            }
            i++
        }
        factors.sort()

        resultText = if (isPrime) "$n is a PRIME Number!" else "$n is COMPOSITE"
        details = listOf(
            "Is Prime" to if (isPrime) "Yes" else "No",
            "Total Factors" to "${factors.size}",
            "Factors" to factors.joinToString(", ")
        )
        onSaveHistory("Prime check for $n", resultText)
    }

    CalculatorLayout(
        title = "Prime Number Checker",
        categoryName = "🔢 Math",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "Prime: divisible only by 1 and itself",
        explanationText = "Checks if a number is prime and lists all factors."
    ) {
        CalcInputField(value = valStr, onValueChange = { valStr = it }, label = "Enter Positive Integer")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Check Prime")
        }

        ResultCard(primaryValue = resultText, details = details, onReset = { valStr = ""; resultText = ""; details = emptyList(); errorMsg = null })
    }
}

// 13. Algebra Solver (Quadratic Equation)
@Composable
fun AlgebraCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var aStr by remember { mutableStateOf("") }
    var bStr by remember { mutableStateOf("") }
    var cStr by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val a = aStr.toDoubleOrNull()
        val b = bStr.toDoubleOrNull()
        val c = cStr.toDoubleOrNull()

        if (a == null || b == null || c == null) {
            errorMsg = "Please enter coefficients a, b, and c"
            return
        }
        if (a == 0.0) {
            errorMsg = "'a' cannot be zero in a quadratic equation (ax² + bx + c = 0)"
            return
        }
        errorMsg = null

        val disc = b * b - 4 * a * c
        if (disc > 0) {
            val x1 = (-b + sqrt(disc)) / (2 * a)
            val x2 = (-b - sqrt(disc)) / (2 * a)
            resultText = "Two Real Roots: x₁ = ${"%.4f".format(x1)}, x₂ = ${"%.4f".format(x2)}"
            details = listOf("Discriminant (D)" to "${"%.2f".format(disc)} (D > 0)")
        } else if (disc == 0.0) {
            val x = -b / (2 * a)
            resultText = "One Double Root: x = ${"%.4f".format(x)}"
            details = listOf("Discriminant (D)" to "0")
        } else {
            val real = -b / (2 * a)
            val imag = sqrt(-disc) / (2 * a)
            resultText = "Complex Roots: ${"%.4f".format(real)} ± ${"%.4f".format(imag)}i"
            details = listOf("Discriminant (D)" to "${"%.2f".format(disc)} (D < 0)")
        }

        onSaveHistory("Quadratic ${a}x² + ${b}x + $c = 0", resultText)
    }

    CalculatorLayout(
        title = "Quadratic Equation Solver",
        categoryName = "🔢 Math",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "ax² + bx + c = 0  ⇒  x = (-b ± √(b² - 4ac)) / (2a)",
        explanationText = "Solves real or complex roots for any quadratic equation."
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CalcInputField(value = aStr, onValueChange = { aStr = it }, label = "a (x² coeff)", modifier = Modifier.weight(1f))
            CalcInputField(value = bStr, onValueChange = { bStr = it }, label = "b (x coeff)", modifier = Modifier.weight(1f))
            CalcInputField(value = cStr, onValueChange = { cStr = it }, label = "c (const)", modifier = Modifier.weight(1f))
        }

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Solve Roots")
        }

        ResultCard(primaryValue = resultText, details = details, onReset = { aStr = ""; bStr = ""; cStr = ""; resultText = ""; details = emptyList(); errorMsg = null })
    }
}

// 14. Logarithm Calculator
@Composable
fun LogarithmCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var numStr by remember { mutableStateOf("") }
    var baseStr by remember { mutableStateOf("10") }
    var resultText by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val x = numStr.toDoubleOrNull()
        val b = baseStr.toDoubleOrNull()

        if (x == null || b == null || x <= 0 || b <= 0 || b == 1.0) {
            errorMsg = "Value and Base must be > 0 and Base ≠ 1"
            return
        }
        errorMsg = null
        val res = log10(x) / log10(b)
        val formatted = if (res % 1.0 == 0.0) res.toLong().toString() else "%.6f".format(res)
        resultText = "log_($b)($x) = $formatted"
        onSaveHistory("log_($b)($x)", resultText)
    }

    CalculatorLayout(
        title = "Logarithm Calculator",
        categoryName = "🔢 Math",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "log_b(x) = log_10(x) / log_10(b)",
        explanationText = "Calculate logarithm for any custom base."
    ) {
        CalcInputField(value = numStr, onValueChange = { numStr = it }, label = "Number (x)")
        CalcInputField(value = baseStr, onValueChange = { baseStr = it }, label = "Base (b)", placeholder = "10")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate Log")
        }

        ResultCard(primaryValue = resultText, onReset = { numStr = ""; baseStr = "10"; resultText = ""; errorMsg = null })
    }
}

// 15. Age Calculator
@Composable
fun AgeCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var day by remember { mutableStateOf("") }
    var month by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val d = day.toIntOrNull()
        val m = month.toIntOrNull()
        val y = year.toIntOrNull()

        if (d == null || m == null || y == null) {
            errorMsg = "Please enter valid day, month, and year"
            return
        }

        try {
            val birthDate = LocalDate.of(y, m, d)
            val today = LocalDate.now()

            if (birthDate.isAfter(today)) {
                errorMsg = "Birth date cannot be in the future"
                return
            }
            errorMsg = null

            val p = Period.between(birthDate, today)
            resultText = "${p.years} Years, ${p.months} Months, ${p.days} Days"

            val totalDays = java.time.temporal.ChronoUnit.DAYS.between(birthDate, today)
            val totalWeeks = totalDays / 7
            val totalHours = totalDays * 24

            details = listOf(
                "Total Days" to "$totalDays days",
                "Total Weeks" to "$totalWeeks weeks",
                "Total Hours" to "$totalHours hours"
            )
            onSaveHistory("Age for DOB $d/$m/$y", resultText)
        } catch (e: Exception) {
            errorMsg = "Invalid Date! Please check day and month combination."
        }
    }

    CalculatorLayout(
        title = "Age Calculator",
        categoryName = "🔢 Math",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "Age = Today Date - Date of Birth",
        explanationText = "Calculate exact age in years, months, days, weeks, and hours."
    ) {
        Text("Date of Birth", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CalcInputField(value = day, onValueChange = { day = it }, label = "Day (1-31)", modifier = Modifier.weight(1f))
            CalcInputField(value = month, onValueChange = { month = it }, label = "Month (1-12)", modifier = Modifier.weight(1f))
            CalcInputField(value = year, onValueChange = { year = it }, label = "Year (YYYY)", modifier = Modifier.weight(1f))
        }

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate Age")
        }

        ResultCard(primaryValue = resultText, details = details, onReset = { day = ""; month = ""; year = ""; resultText = ""; details = emptyList(); errorMsg = null })
    }
}
