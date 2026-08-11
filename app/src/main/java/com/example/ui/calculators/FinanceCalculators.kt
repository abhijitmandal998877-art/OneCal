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
import kotlin.math.pow

fun formatINR(amount: Double): String {
    val rounded = amount.toLong()
    val str = rounded.toString()
    if (str.length <= 3) return "₹$str"
    val last3 = str.takeLast(3)
    val rest = str.dropLast(3)
    val result = StringBuilder()
    for (i in rest.indices) {
        if (i > 0 && (rest.length - i) % 2 == 0) {
            result.append(",")
        }
        result.append(rest[i])
    }
    result.append(",").append(last3)
    return "₹$result"
}

// 1. EMI Calculator
@Composable
fun EMICalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var principalStr by remember { mutableStateOf("") }
    var rateStr by remember { mutableStateOf("") }
    var tenureStr by remember { mutableStateOf("") }
    var tenureType by remember { mutableStateOf("Years") } // Years or Months

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val P = principalStr.toDoubleOrNull()
        val R = rateStr.toDoubleOrNull()
        val T = tenureStr.toDoubleOrNull()

        if (P == null || R == null || T == null || P <= 0 || R <= 0 || T <= 0) {
            errorMsg = "Please enter valid positive numbers for Principal, Interest Rate, and Tenure"
            return
        }
        errorMsg = null

        val months = if (tenureType == "Years") T * 12.0 else T
        val r = R / (12.0 * 100.0)

        val emi = (P * r * (1 + r).pow(months)) / ((1 + r).pow(months) - 1)
        val totalPayment = emi * months
        val totalInterest = totalPayment - P

        primaryVal = "Monthly EMI: ${formatINR(emi)}"
        details = listOf(
            "Principal Loan Amount" to formatINR(P),
            "Total Interest Payable" to formatINR(totalInterest),
            "Total Payment" to formatINR(totalPayment)
        )
        onSaveHistory("EMI for ${formatINR(P)} at $R% ($T $tenureType)", primaryVal)
    }

    CalculatorLayout(
        title = "EMI Calculator",
        categoryName = "💰 Finance",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "EMI = [P x R x (1+R)^N]/[(1+R)^N-1]",
        explanationText = "Calculate Equated Monthly Installment, Total Interest, and Total Amount for Home, Car, or Personal Loans."
    ) {
        CalcInputField(value = principalStr, onValueChange = { principalStr = it }, label = "Loan Amount (Principal)", prefix = "₹")
        CalcInputField(value = rateStr, onValueChange = { rateStr = it }, label = "Interest Rate (% per annum)", suffix = "%")

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CalcInputField(value = tenureStr, onValueChange = { tenureStr = it }, label = "Tenure", modifier = Modifier.weight(1f))
            Row(modifier = Modifier.padding(top = 8.dp)) {
                FilterChip(selected = tenureType == "Years", onClick = { tenureType = "Years" }, label = { Text("Years") })
                Spacer(modifier = Modifier.width(4.dp))
                FilterChip(selected = tenureType == "Months", onClick = { tenureType = "Months" }, label = { Text("Months") })
            }
        }

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate EMI")
        }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { principalStr = ""; rateStr = ""; tenureStr = ""; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}

// 2. Loan Eligibility Calculator
@Composable
fun LoanCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var incomeStr by remember { mutableStateOf("") }
    var existingEmiStr by remember { mutableStateOf("0") }
    var rateStr by remember { mutableStateOf("8.5") }
    var tenureYearsStr by remember { mutableStateOf("20") }

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val income = incomeStr.toDoubleOrNull()
        val existingEmi = existingEmiStr.toDoubleOrNull() ?: 0.0
        val R = rateStr.toDoubleOrNull() ?: 8.5
        val T = tenureYearsStr.toDoubleOrNull() ?: 20.0

        if (income == null || income <= 0) {
            errorMsg = "Please enter monthly net income"
            return
        }
        errorMsg = null

        val maxAvailableEmi = (income * 0.50) - existingEmi
        if (maxAvailableEmi <= 0) {
            errorMsg = "Existing EMIs exceed maximum allowed 50% income capacity"
            primaryVal = ""
            details = emptyList()
            return
        }

        val months = T * 12.0
        val r = R / (12.0 * 100.0)
        val maxLoan = (maxAvailableEmi * ((1 + r).pow(months) - 1)) / (r * (1 + r).pow(months))

        primaryVal = "Max Loan Eligible: ${formatINR(maxLoan)}"
        details = listOf(
            "Allowed Monthly EMI" to formatINR(maxAvailableEmi),
            "Assumed Interest Rate" to "$R% p.a.",
            "Tenure" to "$T Years"
        )
        onSaveHistory("Loan Eligibility for ${formatINR(income)}/mo", primaryVal)
    }

    CalculatorLayout(
        title = "Loan Eligibility Calculator",
        categoryName = "💰 Finance",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "Eligible Loan based on 50% FOIR (Fixed Obligation to Income Ratio)",
        explanationText = "Estimates maximum loan amount banks will sanction based on your net income and existing obligations."
    ) {
        CalcInputField(value = incomeStr, onValueChange = { incomeStr = it }, label = "Net Monthly Income", prefix = "₹")
        CalcInputField(value = existingEmiStr, onValueChange = { existingEmiStr = it }, label = "Existing Monthly EMIs (if any)", prefix = "₹")
        CalcInputField(value = rateStr, onValueChange = { rateStr = it }, label = "Interest Rate (% p.a.)", suffix = "%")
        CalcInputField(value = tenureYearsStr, onValueChange = { tenureYearsStr = it }, label = "Tenure (Years)")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate Loan Eligibility")
        }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { incomeStr = ""; existingEmiStr = "0"; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}

// 3. Simple Interest Calculator
@Composable
fun SimpleInterestCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var pStr by remember { mutableStateOf("") }
    var rStr by remember { mutableStateOf("") }
    var tStr by remember { mutableStateOf("") }

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val P = pStr.toDoubleOrNull()
        val R = rStr.toDoubleOrNull()
        val T = tStr.toDoubleOrNull()

        if (P == null || R == null || T == null) {
            errorMsg = "Please enter valid Principal, Rate, and Time"
            return
        }
        errorMsg = null

        val interest = (P * R * T) / 100.0
        val totalAmount = P + interest

        primaryVal = "Simple Interest: ${formatINR(interest)}"
        details = listOf(
            "Principal Amount" to formatINR(P),
            "Total Amount" to formatINR(totalAmount)
        )
        onSaveHistory("SI for ${formatINR(P)} at $R% for $T yrs", primaryVal)
    }

    CalculatorLayout(
        title = "Simple Interest Calculator",
        categoryName = "💰 Finance",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "SI = (Principal × Rate × Time) / 100",
        explanationText = "Calculates simple interest earned or payable on principal over time."
    ) {
        CalcInputField(value = pStr, onValueChange = { pStr = it }, label = "Principal Amount", prefix = "₹")
        CalcInputField(value = rStr, onValueChange = { rStr = it }, label = "Rate of Interest (% p.a.)", suffix = "%")
        CalcInputField(value = tStr, onValueChange = { tStr = it }, label = "Time Period (Years)")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate Simple Interest")
        }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { pStr = ""; rStr = ""; tStr = ""; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}

// 4. Compound Interest Calculator
@Composable
fun CompoundInterestCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var pStr by remember { mutableStateOf("") }
    var rStr by remember { mutableStateOf("") }
    var tStr by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf(1) } // 1: Yearly, 2: Half-yearly, 4: Quarterly, 12: Monthly

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val P = pStr.toDoubleOrNull()
        val R = rStr.toDoubleOrNull()
        val T = tStr.toDoubleOrNull()

        if (P == null || R == null || T == null) {
            errorMsg = "Please enter Principal, Rate, and Time"
            return
        }
        errorMsg = null

        val n = frequency.toDouble()
        val amount = P * (1 + (R / (100.0 * n))).pow(n * T)
        val interest = amount - P

        primaryVal = "Total Amount: ${formatINR(amount)}"
        details = listOf(
            "Principal Amount" to formatINR(P),
            "Compound Interest" to formatINR(interest)
        )
        onSaveHistory("CI for ${formatINR(P)} at $R% for $T yrs", primaryVal)
    }

    CalculatorLayout(
        title = "Compound Interest Calculator",
        categoryName = "💰 Finance",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "A = P × (1 + r/n)^(n × t)",
        explanationText = "Calculate compound interest with compounding frequency options."
    ) {
        CalcInputField(value = pStr, onValueChange = { pStr = it }, label = "Principal Amount", prefix = "₹")
        CalcInputField(value = rStr, onValueChange = { rStr = it }, label = "Interest Rate (% p.a.)", suffix = "%")
        CalcInputField(value = tStr, onValueChange = { tStr = it }, label = "Time Period (Years)")

        Text("Compounding Frequency", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Yearly" to 1, "Half-Yr" to 2, "Quarterly" to 4, "Monthly" to 12).forEach { (lbl, freq) ->
                FilterChip(selected = frequency == freq, onClick = { frequency = freq }, label = { Text(lbl) })
            }
        }

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate Compound Interest")
        }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { pStr = ""; rStr = ""; tStr = ""; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}

// 5. GST Calculator
@Composable
fun GSTCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var amountStr by remember { mutableStateOf("") }
    var selectedGstRate by remember { mutableStateOf(18.0) }
    var isCustomRate by remember { mutableStateOf(false) }
    var customRateStr by remember { mutableStateOf("") }
    var isInclusive by remember { mutableStateOf(false) } // Add GST or Remove GST

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val amount = amountStr.toDoubleOrNull()
        val rate = if (isCustomRate) customRateStr.toDoubleOrNull() else selectedGstRate

        if (amount == null || rate == null || amount <= 0 || rate < 0) {
            errorMsg = "Please enter a valid amount and GST rate"
            return
        }
        errorMsg = null

        val gstAmount: Double
        val netAmount: Double
        val totalAmount: Double

        if (!isInclusive) {
            // Exclusive: Add GST
            netAmount = amount
            gstAmount = (amount * rate) / 100.0
            totalAmount = amount + gstAmount
        } else {
            // Inclusive: Extract GST
            totalAmount = amount
            netAmount = (amount * 100.0) / (100.0 + rate)
            gstAmount = totalAmount - netAmount
        }

        val cgst = gstAmount / 2.0
        val sgst = gstAmount / 2.0

        primaryVal = "Total Amount: ${formatINR(totalAmount)}"
        details = listOf(
            "Net / Base Price" to formatINR(netAmount),
            "Total GST Amount ($rate%)" to formatINR(gstAmount),
            "CGST (${rate / 2}%)" to formatINR(cgst),
            "SGST (${rate / 2}%)" to formatINR(sgst)
        )
        onSaveHistory("GST ($rate%) on ${formatINR(amount)}", primaryVal)
    }

    CalculatorLayout(
        title = "GST Calculator",
        categoryName = "💰 Finance",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "Add GST: Total = Net + (Net × Rate / 100) | Remove GST: Net = Total × 100 / (100 + Rate)",
        explanationText = "Calculate GST for Indian standard rates (5%, 12%, 18%, 28%) or custom rate with CGST/SGST split."
    ) {
        CalcInputField(value = amountStr, onValueChange = { amountStr = it }, label = "Amount", prefix = "₹")

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = !isInclusive, onClick = { isInclusive = false }, label = { Text("Add GST (Exclusive)") })
            FilterChip(selected = isInclusive, onClick = { isInclusive = true }, label = { Text("Remove GST (Inclusive)") })
        }

        Text("Select GST Rate", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(5.0, 12.0, 18.0, 28.0).forEach { r ->
                FilterChip(
                    selected = !isCustomRate && selectedGstRate == r,
                    onClick = { selectedGstRate = r; isCustomRate = false },
                    label = { Text("${r.toInt()}%") }
                )
            }
            FilterChip(
                selected = isCustomRate,
                onClick = { isCustomRate = true },
                label = { Text("Custom") }
            )
        }

        if (isCustomRate) {
            CalcInputField(value = customRateStr, onValueChange = { customRateStr = it }, label = "Custom GST Rate (%)", suffix = "%")
        }

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate GST")
        }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { amountStr = ""; customRateStr = ""; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}

// 6. Discount Calculator
@Composable
fun DiscountCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var originalStr by remember { mutableStateOf("") }
    var discountStr by remember { mutableStateOf("") }

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val orig = originalStr.toDoubleOrNull()
        val disc = discountStr.toDoubleOrNull()

        if (orig == null || disc == null || orig < 0 || disc < 0 || disc > 100) {
            errorMsg = "Please enter valid original price and discount percentage (0-100%)"
            return
        }
        errorMsg = null

        val savings = (orig * disc) / 100.0
        val finalPrice = orig - savings

        primaryVal = "Final Price: ${formatINR(finalPrice)}"
        details = listOf(
            "Original Price" to formatINR(orig),
            "Discount ($disc%)" to formatINR(savings),
            "You Save" to formatINR(savings)
        )
        onSaveHistory("Discount $disc% on ${formatINR(orig)}", primaryVal)
    }

    CalculatorLayout(
        title = "Discount Calculator",
        categoryName = "💰 Finance",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "Savings = (Price × Discount%) / 100",
        explanationText = "Calculate discount savings and final offer price during sale shopping."
    ) {
        CalcInputField(value = originalStr, onValueChange = { originalStr = it }, label = "Original Price", prefix = "₹")
        CalcInputField(value = discountStr, onValueChange = { discountStr = it }, label = "Discount Percentage", suffix = "%")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate Discount")
        }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { originalStr = ""; discountStr = ""; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}

// 7. Profit & Loss Calculator
@Composable
fun ProfitLossCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var cpStr by remember { mutableStateOf("") }
    var spStr by remember { mutableStateOf("") }

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val CP = cpStr.toDoubleOrNull()
        val SP = spStr.toDoubleOrNull()

        if (CP == null || SP == null || CP <= 0) {
            errorMsg = "Please enter valid Cost Price (> 0) and Selling Price"
            return
        }
        errorMsg = null

        val diff = SP - CP
        val pct = (diff / CP) * 100.0

        if (diff >= 0) {
            primaryVal = "Profit: ${formatINR(diff)} (${"%.2f".format(pct)}%)"
            details = listOf("Status" to "Profit", "Cost Price" to formatINR(CP), "Selling Price" to formatINR(SP))
        } else {
            primaryVal = "Loss: ${formatINR(-diff)} (${"%.2f".format(-pct)}%)"
            details = listOf("Status" to "Loss", "Cost Price" to formatINR(CP), "Selling Price" to formatINR(SP))
        }

        onSaveHistory("P&L for CP ${formatINR(CP)}, SP ${formatINR(SP)}", primaryVal)
    }

    CalculatorLayout(
        title = "Profit & Loss Calculator",
        categoryName = "💰 Finance",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "Profit/Loss = SP - CP | Percentage = (Profit or Loss / CP) × 100",
        explanationText = "Calculate net profit or loss amount and percentage."
    ) {
        CalcInputField(value = cpStr, onValueChange = { cpStr = it }, label = "Cost Price (CP)", prefix = "₹")
        CalcInputField(value = spStr, onValueChange = { spStr = it }, label = "Selling Price (SP)", prefix = "₹")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate Profit / Loss")
        }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { cpStr = ""; spStr = ""; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}

// 8. Salary Calculator
@Composable
fun SalaryCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var ctcStr by remember { mutableStateOf("") }
    var bonusPctStr by remember { mutableStateOf("10") }

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val ctc = ctcStr.toDoubleOrNull()
        val bonusPct = bonusPctStr.toDoubleOrNull() ?: 0.0

        if (ctc == null || ctc <= 0) {
            errorMsg = "Please enter annual CTC"
            return
        }
        errorMsg = null

        val variableBonus = (ctc * bonusPct) / 100.0
        val fixedGrossAnnual = ctc - variableBonus

        // Standard Indian Salary deductions estimate (PF 12% basic ~50% ctc, Professional Tax ~2400)
        val basic = fixedGrossAnnual * 0.50
        val pfAnnual = minOf(basic * 0.12, 1800.0 * 12)
        val profTaxAnnual = 2400.0

        val netTakeHomeAnnual = fixedGrossAnnual - pfAnnual - profTaxAnnual
        val monthlyInHand = netTakeHomeAnnual / 12.0

        primaryVal = "Est. Monthly In-Hand: ${formatINR(monthlyInHand)}"
        details = listOf(
            "Annual CTC" to formatINR(ctc),
            "Annual Fixed Gross" to formatINR(fixedGrossAnnual),
            "PF Deduction (Annual)" to formatINR(pfAnnual),
            "Professional Tax (Annual)" to formatINR(profTaxAnnual),
            "Annual In-Hand (Pre-Tax)" to formatINR(netTakeHomeAnnual)
        )
        onSaveHistory("Salary for CTC ${formatINR(ctc)}", primaryVal)
    }

    CalculatorLayout(
        title = "Salary Calculator",
        categoryName = "💰 Finance",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "Monthly In-Hand = (Fixed Gross - PF - Professional Tax) / 12",
        explanationText = "Estimates monthly in-hand salary from gross CTC after PF and statutory deductions."
    ) {
        CalcInputField(value = ctcStr, onValueChange = { ctcStr = it }, label = "Annual CTC", prefix = "₹")
        CalcInputField(value = bonusPctStr, onValueChange = { bonusPctStr = it }, label = "Variable / Bonus Component (%)", suffix = "%")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate Salary")
        }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { ctcStr = ""; bonusPctStr = "10"; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}

// 9. SIP Calculator
@Composable
fun SIPCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var monthlyStr by remember { mutableStateOf("") }
    var rateStr by remember { mutableStateOf("12") }
    var yearsStr by remember { mutableStateOf("") }

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val P = monthlyStr.toDoubleOrNull()
        val i = (rateStr.toDoubleOrNull() ?: 12.0) / (12.0 * 100.0)
        val Y = yearsStr.toDoubleOrNull()

        if (P == null || Y == null || P <= 0 || Y <= 0) {
            errorMsg = "Please enter valid monthly SIP amount and tenure in years"
            return
        }
        errorMsg = null

        val n = Y * 12.0
        val totalMaturity = P * (((1 + i).pow(n) - 1) / i) * (1 + i)
        val totalInvested = P * n
        val wealthGained = totalMaturity - totalInvested

        primaryVal = "Total Value: ${formatINR(totalMaturity)}"
        details = listOf(
            "Invested Amount" to formatINR(totalInvested),
            "Estimated Returns" to formatINR(wealthGained),
            "Expected Annual Rate" to "$rateStr%"
        )
        onSaveHistory("SIP ${formatINR(P)}/mo for $Y yrs", primaryVal)
    }

    CalculatorLayout(
        title = "SIP Calculator",
        categoryName = "💰 Finance",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "FV = P × [{(1 + i)^n - 1} / i] × (1 + i)",
        explanationText = "Estimate future wealth accumulated through Systematic Investment Plans in Mutual Funds."
    ) {
        CalcInputField(value = monthlyStr, onValueChange = { monthlyStr = it }, label = "Monthly Investment Amount", prefix = "₹")
        CalcInputField(value = rateStr, onValueChange = { rateStr = it }, label = "Expected Return Rate (% p.a.)", suffix = "%")
        CalcInputField(value = yearsStr, onValueChange = { yearsStr = it }, label = "Time Period (Years)")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate SIP Returns")
        }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { monthlyStr = ""; rateStr = "12"; yearsStr = ""; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}

// 10. FD Calculator
@Composable
fun FDCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var pStr by remember { mutableStateOf("") }
    var rStr by remember { mutableStateOf("7.0") }
    var tStr by remember { mutableStateOf("") }

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val P = pStr.toDoubleOrNull()
        val R = rStr.toDoubleOrNull() ?: 7.0
        val T = tStr.toDoubleOrNull()

        if (P == null || T == null || P <= 0 || T <= 0) {
            errorMsg = "Please enter valid deposit amount and tenure"
            return
        }
        errorMsg = null

        // Standard FD quarterly compounding
        val n = 4.0
        val amount = P * (1 + (R / (100.0 * n))).pow(n * T)
        val interest = amount - P

        primaryVal = "Maturity Amount: ${formatINR(amount)}"
        details = listOf(
            "Deposit Amount" to formatINR(P),
            "Total Interest Earned" to formatINR(interest),
            "Interest Rate" to "$R% p.a."
        )
        onSaveHistory("FD ${formatINR(P)} at $R% for $T yrs", primaryVal)
    }

    CalculatorLayout(
        title = "FD Calculator",
        categoryName = "💰 Finance",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "A = P × (1 + r/4)^(4 × t) [Quarterly Compounding]",
        explanationText = "Calculate Fixed Deposit maturity value and interest income."
    ) {
        CalcInputField(value = pStr, onValueChange = { pStr = it }, label = "Total Deposit Amount", prefix = "₹")
        CalcInputField(value = rStr, onValueChange = { rStr = it }, label = "Interest Rate (% p.a.)", suffix = "%")
        CalcInputField(value = tStr, onValueChange = { tStr = it }, label = "Tenure (Years)")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate FD Maturity")
        }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { pStr = ""; rStr = "7.0"; tStr = ""; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}

// 11. RD Calculator
@Composable
fun RDCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var monthlyStr by remember { mutableStateOf("") }
    var rStr by remember { mutableStateOf("6.8") }
    var monthsStr by remember { mutableStateOf("") }

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val P = monthlyStr.toDoubleOrNull()
        val R = rStr.toDoubleOrNull() ?: 6.8
        val N = monthsStr.toDoubleOrNull()

        if (P == null || N == null || P <= 0 || N <= 0) {
            errorMsg = "Please enter valid monthly installment and tenure in months"
            return
        }
        errorMsg = null

        // Compound interest per quarter for RD
        var totalAmount = 0.0
        val r = R / 100.0
        val totalInvested = P * N

        for (m in 1..N.toInt()) {
            val monthsRemaining = N - m + 1
            val quarters = monthsRemaining / 3.0
            totalAmount += P * (1 + r / 4.0).pow(quarters)
        }

        val interest = totalAmount - totalInvested

        primaryVal = "Maturity Value: ${formatINR(totalAmount)}"
        details = listOf(
            "Total Investment" to formatINR(totalInvested),
            "Interest Earned" to formatINR(interest)
        )
        onSaveHistory("RD ${formatINR(P)}/mo for ${N.toInt()} mos", primaryVal)
    }

    CalculatorLayout(
        title = "RD Calculator",
        categoryName = "💰 Finance",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "Quarterly compounding for recurring monthly deposits",
        explanationText = "Calculate maturity value for bank Recurring Deposits."
    ) {
        CalcInputField(value = monthlyStr, onValueChange = { monthlyStr = it }, label = "Monthly Deposit Amount", prefix = "₹")
        CalcInputField(value = rStr, onValueChange = { rStr = it }, label = "Interest Rate (% p.a.)", suffix = "%")
        CalcInputField(value = monthsStr, onValueChange = { monthsStr = it }, label = "Tenure (Months)")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate RD Maturity")
        }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { monthlyStr = ""; rStr = "6.8"; monthsStr = ""; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}

// 12. Investment Return Calculator
@Composable
fun InvestmentReturnCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var initialStr by remember { mutableStateOf("") }
    var finalStr by remember { mutableStateOf("") }
    var yearsStr by remember { mutableStateOf("") }

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val initial = initialStr.toDoubleOrNull()
        val finalVal = finalStr.toDoubleOrNull()
        val Y = yearsStr.toDoubleOrNull()

        if (initial == null || finalVal == null || Y == null || initial <= 0 || Y <= 0) {
            errorMsg = "Please enter valid positive numbers"
            return
        }
        errorMsg = null

        val cagr = ((finalVal / initial).pow(1.0 / Y) - 1.0) * 100.0
        val totalGain = finalVal - initial
        val totalGainPct = (totalGain / initial) * 100.0

        primaryVal = "CAGR (Annual Return): ${"%.2f".format(cagr)}%"
        details = listOf(
            "Initial Investment" to formatINR(initial),
            "Final Value" to formatINR(finalVal),
            "Absolute Gain" to "${formatINR(totalGain)} (${"%.2f".format(totalGainPct)}%)"
        )
        onSaveHistory("Investment Return ${formatINR(initial)} -> ${formatINR(finalVal)}", primaryVal)
    }

    CalculatorLayout(
        title = "Investment Return (CAGR) Calculator",
        categoryName = "💰 Finance",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "CAGR = [(End Value / Start Value)^(1 / Years)] - 1",
        explanationText = "Calculates Compound Annual Growth Rate (CAGR) for lump sum investments."
    ) {
        CalcInputField(value = initialStr, onValueChange = { initialStr = it }, label = "Initial Investment Amount", prefix = "₹")
        CalcInputField(value = finalStr, onValueChange = { finalStr = it }, label = "Final Accumulated Value", prefix = "₹")
        CalcInputField(value = yearsStr, onValueChange = { yearsStr = it }, label = "Tenure (Years)")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate CAGR Return")
        }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { initialStr = ""; finalStr = ""; yearsStr = ""; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}

// 13. Tax Calculator (Income Tax New Regime Estimate)
@Composable
fun TaxCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var incomeStr by remember { mutableStateOf("") }

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val income = incomeStr.toDoubleOrNull()
        if (income == null || income < 0) {
            errorMsg = "Please enter valid annual taxable income"
            return
        }
        errorMsg = null

        val stdDeduction = 75000.0
        val taxableIncome = maxOf(0.0, income - stdDeduction)

        var tax = 0.0

        // Indian Income Tax New Regime Slabs (FY 2024-25 estimate)
        // Up to 3L: Nil
        // 3L - 7L: 5%
        // 7L - 10L: 10%
        // 10L - 12L: 15%
        // 12L - 15L: 20%
        // Above 15L: 30%
        // Rebate u/s 87A for taxable income up to 7L -> tax = 0

        val t = taxableIncome
        if (t > 1500000) {
            tax += (t - 1500000) * 0.30
            tax += 300000 * 0.20 // 12-15L
            tax += 200000 * 0.15 // 10-12L
            tax += 300000 * 0.10 // 7-10L
            tax += 400000 * 0.05 // 3-7L
        } else if (t > 1200000) {
            tax += (t - 1200000) * 0.20
            tax += 200000 * 0.15
            tax += 300000 * 0.10
            tax += 400000 * 0.05
        } else if (t > 1000000) {
            tax += (t - 1000000) * 0.15
            tax += 300000 * 0.10
            tax += 400000 * 0.05
        } else if (t > 700000) {
            tax += (t - 700000) * 0.10
            tax += 400000 * 0.05
        } else if (t > 300000) {
            tax += (t - 300000) * 0.05
        }

        // Rebate u/s 87A if taxable income <= 7,000,00 (effectively zero tax up to 7.75L CTC)
        if (taxableIncome <= 700000) {
            tax = 0.0
        }

        val cess = tax * 0.04
        val totalTax = tax + cess

        primaryVal = "Total Tax Payable: ${formatINR(totalTax)}"
        details = listOf(
            "Gross Income" to formatINR(income),
            "Standard Deduction" to formatINR(stdDeduction),
            "Taxable Income" to formatINR(taxableIncome),
            "Health & Education Cess (4%)" to formatINR(cess)
        )
        onSaveHistory("Income Tax for ${formatINR(income)}", primaryVal)
    }

    CalculatorLayout(
        title = "Income Tax Calculator",
        categoryName = "💰 Finance",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "Indian Income Tax New Tax Regime (FY 2024-25) Slabs with ₹75,000 Standard Deduction",
        explanationText = "Estimates tax liability under the Default New Tax Regime."
    ) {
        CalcInputField(value = incomeStr, onValueChange = { incomeStr = it }, label = "Annual Gross Income", prefix = "₹")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate Income Tax")
        }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { incomeStr = ""; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}
