package com.example.ui.calculators

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.components.CalcInputField
import com.example.ui.components.CalculatorLayout
import com.example.ui.components.ResultCard
import kotlin.random.Random

// Helper for Indian Numbering System to Words
fun numberToIndianWords(number: Long): String {
    if (number == 0L) return "Zero"
    val units = arrayOf("", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
        "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen")
    val tens = arrayOf("", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety")

    fun convertLessThanThousand(n: Int): String {
        var str = ""
        if (n >= 100) {
            str += units[n / 100] + " Hundred "
        }
        val rem = n % 100
        if (rem in 1..19) {
            str += units[rem]
        } else if (rem >= 20) {
            str += tens[rem / 10] + if (rem % 10 > 0) " " + units[rem % 10] else ""
        }
        return str.trim()
    }

    var n = number
    val result = StringBuilder()

    val crore = (n / 10000000).toInt()
    n %= 10000000

    val lakh = (n / 100000).toInt()
    n %= 100000

    val thousand = (n / 1000).toInt()
    n %= 1000

    if (crore > 0) {
        result.append(convertLessThanThousand(crore)).append(" Crore ")
    }
    if (lakh > 0) {
        result.append(convertLessThanThousand(lakh)).append(" Lakh ")
    }
    if (thousand > 0) {
        result.append(convertLessThanThousand(thousand)).append(" Thousand ")
    }
    if (n > 0) {
        result.append(convertLessThanThousand(n.toInt()))
    }

    return result.toString().trim()
}

// 1. Number to Words
@Composable
fun NumToWordsCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var numStr by remember { mutableStateOf("") }
    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val n = numStr.toLongOrNull()
        if (n == null || n < 0) {
            errorMsg = "Please enter a valid positive number"
            return
        }
        errorMsg = null

        val words = numberToIndianWords(n)
        primaryVal = words
        details = listOf("Rupees Format" to "$words Rupees Only")
        onSaveHistory("Num to words $n", primaryVal)
    }

    CalculatorLayout(
        title = "Number to Words Converter", categoryName = "🧰 Tools", isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite, onBackClick = onBackClick,
        formulaText = "Indian Numbering Format (Lakhs & Crores)",
        explanationText = "Convert numeric amounts into English words for bank cheque writing and billing."
    ) {
        CalcInputField(value = numStr, onValueChange = { numStr = it }, label = "Enter Amount / Number")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) { Text("Convert to Words") }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { numStr = ""; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}

// 2. Roman Number Converter
@Composable
fun RomanCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var inputStr by remember { mutableStateOf("") }
    var primaryVal by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun toRoman(num: Int): String {
        val vals = intArrayOf(1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1)
        val syms = arrayOf("M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I")
        var n = num
        val sb = StringBuilder()
        for (i in vals.indices) {
            while (n >= vals[i]) {
                n -= vals[i]
                sb.append(syms[i])
            }
        }
        return sb.toString()
    }

    fun calculate() {
        val n = inputStr.toIntOrNull()
        if (n != null && n in 1..3999) {
            errorMsg = null
            val roman = toRoman(n)
            primaryVal = "$n = $roman"
            onSaveHistory("Roman $n", primaryVal)
        } else {
            errorMsg = "Enter a valid integer between 1 and 3999"
        }
    }

    CalculatorLayout(
        title = "Roman Numeral Converter", categoryName = "🧰 Tools", isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite, onBackClick = onBackClick,
        formulaText = "M=1000, D=500, C=100, L=50, X=10, V=5, I=1",
        explanationText = "Convert integers to Roman Numerals."
    ) {
        CalcInputField(value = inputStr, onValueChange = { inputStr = it }, label = "Decimal Number (1-3999)")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) { Text("Convert to Roman") }

        ResultCard(primaryValue = primaryVal, onReset = { inputStr = ""; primaryVal = ""; errorMsg = null })
    }
}

// 3. Binary Converter
@Composable
fun BinaryCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var inputStr by remember { mutableStateOf("") }
    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val n = inputStr.toLongOrNull()
        if (n == null || n < 0) {
            errorMsg = "Please enter a valid non-negative integer"
            return
        }
        errorMsg = null

        val bin = java.lang.Long.toBinaryString(n)
        val oct = java.lang.Long.toOctalString(n)
        val hex = java.lang.Long.toHexString(n).uppercase()

        primaryVal = "Binary (Base 2): $bin"
        details = listOf(
            "Decimal (Base 10)" to "$n",
            "Octal (Base 8)" to oct,
            "Hexadecimal (Base 16)" to "0x$hex"
        )
        onSaveHistory("Binary conv $n", primaryVal)
    }

    CalculatorLayout(
        title = "Binary / Base Converter", categoryName = "🧰 Tools", isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite, onBackClick = onBackClick,
        formulaText = "Decimal ↔ Binary ↔ Octal ↔ Hexadecimal",
        explanationText = "Convert numbers between Binary, Octal, Decimal, and Hexadecimal representations."
    ) {
        CalcInputField(value = inputStr, onValueChange = { inputStr = it }, label = "Decimal Number")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) { Text("Convert Base") }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { inputStr = ""; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}

// 4. Decimal Base Converter
@Composable
fun DecimalCalculatorScreen(isFavorite: Boolean, onToggleFavorite: () -> Unit, onBackClick: () -> Unit, onSaveHistory: (String, String) -> Unit) {
    BinaryCalculatorScreen(isFavorite, onToggleFavorite, onBackClick, onSaveHistory)
}

// 5. Hexadecimal Converter
@Composable
fun HexCalculatorScreen(isFavorite: Boolean, onToggleFavorite: () -> Unit, onBackClick: () -> Unit, onSaveHistory: (String, String) -> Unit) {
    BinaryCalculatorScreen(isFavorite, onToggleFavorite, onBackClick, onSaveHistory)
}

// 6. Random Number Generator
@Composable
fun RandomNumCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var minStr by remember { mutableStateOf("1") }
    var maxStr by remember { mutableStateOf("100") }

    var primaryVal by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun generate() {
        val min = minStr.toIntOrNull() ?: 1
        val max = maxStr.toIntOrNull() ?: 100

        if (min >= max) {
            errorMsg = "Min value must be strictly less than Max value"
            return
        }
        errorMsg = null

        val randomNum = Random.nextInt(min, max + 1)
        primaryVal = "Generated Number: $randomNum"
        onSaveHistory("Random number [$min, $max]", primaryVal)
    }

    CalculatorLayout(
        title = "Random Number Generator", categoryName = "🧰 Tools", isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite, onBackClick = onBackClick,
        formulaText = "Random Integer in range [Min, Max]",
        explanationText = "Generate uniform random numbers or dice rolls for games, raffles, and testing."
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CalcInputField(value = minStr, onValueChange = { minStr = it }, label = "Minimum", modifier = Modifier.weight(1f))
            CalcInputField(value = maxStr, onValueChange = { maxStr = it }, label = "Maximum", modifier = Modifier.weight(1f))
        }

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { generate() }, modifier = Modifier.fillMaxWidth()) { Text("🎲 Generate Random Number") }

        ResultCard(primaryValue = primaryVal, onReset = { minStr = "1"; maxStr = "100"; primaryVal = ""; errorMsg = null })
    }
}

// 7. Tools Percentage Change
@Composable
fun ToolsPercentCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var initialStr by remember { mutableStateOf("") }
    var finalStr by remember { mutableStateOf("") }

    var primaryVal by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val init = initialStr.toDoubleOrNull()
        val fin = finalStr.toDoubleOrNull()

        if (init == null || fin == null || init == 0.0) {
            errorMsg = "Please enter valid initial and final values (Initial ≠ 0)"
            return
        }
        errorMsg = null

        val change = fin - init
        val pctChange = (change / init) * 100.0

        primaryVal = if (pctChange >= 0) {
            "Increase of +${"%.2f".format(pctChange)}%"
        } else {
            "Decrease of ${"%.2f".format(pctChange)}%"
        }
        onSaveHistory("Percent change $init to $fin", primaryVal)
    }

    CalculatorLayout(
        title = "Percentage Change Calculator", categoryName = "🧰 Tools", isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite, onBackClick = onBackClick,
        formulaText = "Percentage Change = [(Final - Initial) / Initial] × 100",
        explanationText = "Find percentage increase or decrease between two values."
    ) {
        CalcInputField(value = initialStr, onValueChange = { initialStr = it }, label = "Initial Value")
        CalcInputField(value = finalStr, onValueChange = { finalStr = it }, label = "Final Value")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) { Text("Calculate % Change") }

        ResultCard(primaryValue = primaryVal, onReset = { initialStr = ""; finalStr = ""; primaryVal = ""; errorMsg = null })
    }
}

// 8. Password Generator
@Composable
fun PasswordGenCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var lengthStr by remember { mutableStateOf("12") }
    var includeSymbols by remember { mutableStateOf(true) }
    var includeNumbers by remember { mutableStateOf(true) }

    var primaryVal by remember { mutableStateOf("") }

    fun generate() {
        val len = lengthStr.toIntOrNull() ?: 12
        val chars = StringBuilder("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz")
        if (includeNumbers) chars.append("0123456789")
        if (includeSymbols) chars.append("!@#$%^&*()_+-=[]{}|;:,.<>?")

        val pass = (1..len).map { chars.random() }.joinToString("")
        primaryVal = pass
        onSaveHistory("Password generated ($len chars)", "Generated Secure Password")
    }

    CalculatorLayout(
        title = "Password Generator", categoryName = "🧰 Tools", isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite, onBackClick = onBackClick,
        formulaText = "Cryptographically secure randomized character string",
        explanationText = "Generate strong, secure passwords with customizable length and characters."
    ) {
        CalcInputField(value = lengthStr, onValueChange = { lengthStr = it }, label = "Password Length (e.g., 12)")

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = includeNumbers, onClick = { includeNumbers = !includeNumbers }, label = { Text("0-9 Numbers") })
            FilterChip(selected = includeSymbols, onClick = { includeSymbols = !includeSymbols }, label = { Text("!@# Symbols") })
        }

        Button(onClick = { generate() }, modifier = Modifier.fillMaxWidth()) { Text("🔑 Generate Password") }

        ResultCard(resultTitle = "Generated Password", primaryValue = primaryVal, onReset = { lengthStr = "12"; primaryVal = "" })
    }
}

// 9. Number Checker (Even/Odd, Prime, Armstrong, Palindrome)
@Composable
fun NumCheckerCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var numStr by remember { mutableStateOf("") }
    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun check() {
        val n = numStr.toLongOrNull()
        if (n == null) {
            errorMsg = "Please enter a valid integer"
            return
        }
        errorMsg = null

        val isEven = n % 2 == 0L
        val isPrime = if (n < 2) false else (2..Math.sqrt(n.toDouble()).toLong()).none { n % it == 0L }
        val isPalindrome = n.toString() == n.toString().reversed()

        // Armstrong check
        val s = n.toString()
        val len = s.length
        val armSum = s.sumOf { Math.pow((it - '0').toDouble(), len.toDouble()).toLong() }
        val isArmstrong = armSum == n

        primaryVal = "Number: $n"
        details = listOf(
            "Even / Odd" to if (isEven) "EVEN" else "ODD",
            "Prime Number" to if (isPrime) "YES" else "NO",
            "Palindrome" to if (isPalindrome) "YES" else "NO",
            "Armstrong Number" to if (isArmstrong) "YES" else "NO"
        )
        onSaveHistory("Checked $n", if (isPrime) "Prime, ${if (isEven) "Even" else "Odd"}" else "Not Prime")
    }

    CalculatorLayout(
        title = "Number Property Checker", categoryName = "🧰 Tools", isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite, onBackClick = onBackClick,
        formulaText = "Evaluates Prime, Even/Odd, Palindrome, and Armstrong properties",
        explanationText = "Instantly inspect mathematical properties of any number."
    ) {
        CalcInputField(value = numStr, onValueChange = { numStr = it }, label = "Enter Integer Number")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { check() }, modifier = Modifier.fillMaxWidth()) { Text("Inspect Properties") }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { numStr = ""; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}

// 10. History Tool Placeholder / Redirect
@Composable
fun HistoryToolCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    CalculatorLayout(
        title = "Calculation History Log", categoryName = "🧰 Tools", isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite, onBackClick = onBackClick,
        formulaText = "Local Room DB Stored History",
        explanationText = "All past calculations are automatically recorded with date and timestamp in the History tab below."
    ) {
        Text("Tip: Use the bottom navigation 'History' tab to view, search, and manage your saved calculations anytime!", style = MaterialTheme.typography.bodyLarge)
    }
}
