package com.example.ui.calculators

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.components.CalcInputField
import com.example.ui.components.CalculatorLayout
import com.example.ui.components.ResultCard

// 1. Price per KG -> Gram
@Composable
fun KGToGramCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var pricePerKgStr by remember { mutableStateOf("") }
    var gramsStr by remember { mutableStateOf("") }

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val pricePerKg = pricePerKgStr.toDoubleOrNull()
        val grams = gramsStr.toDoubleOrNull()

        if (pricePerKg == null || grams == null || pricePerKg < 0 || grams < 0) {
            errorMsg = "Please enter valid price per kg and quantity in grams"
            return
        }
        errorMsg = null

        val calculatedPrice = (pricePerKg / 1000.0) * grams
        val pricePer100g = pricePerKg / 10.0

        primaryVal = "Calculated Price: ${formatINR(calculatedPrice)}"
        details = listOf(
            "Price per 100g" to formatINR(pricePer100g),
            "Price per 1 Gram" to "₹${"%.4f".format(pricePerKg / 1000.0)}"
        )
        onSaveHistory("Price for ${grams.toInt()}g at ${formatINR(pricePerKg)}/kg", primaryVal)
    }

    CalculatorLayout(
        title = "Price per KG → Gram",
        categoryName = "🛒 Business",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "Total Price = (Price per KG / 1000) × Quantity in Grams",
        explanationText = "Designed for small shopkeepers & grocery shoppers. Example: ₹500/kg for 250 grams = ₹125."
    ) {
        CalcInputField(value = pricePerKgStr, onValueChange = { pricePerKgStr = it }, label = "Price per KG (₹)", prefix = "₹")
        CalcInputField(value = gramsStr, onValueChange = { gramsStr = it }, label = "Quantity in Grams (g)", suffix = "g")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate Price")
        }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { pricePerKgStr = ""; gramsStr = ""; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}

// 2. Unit Price Calculator
@Composable
fun UnitPriceCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var totalPriceStr by remember { mutableStateOf("") }
    var totalUnitsStr by remember { mutableStateOf("") }

    var primaryVal by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val price = totalPriceStr.toDoubleOrNull()
        val units = totalUnitsStr.toDoubleOrNull()

        if (price == null || units == null || units <= 0) {
            errorMsg = "Please enter valid total price and units (>0)"
            return
        }
        errorMsg = null

        val unitPrice = price / units
        primaryVal = "Unit Price: ₹${"%.2f".format(unitPrice)} per unit"
        onSaveHistory("Unit Price for ${formatINR(price)} / $units units", primaryVal)
    }

    CalculatorLayout(
        title = "Unit Price Calculator",
        categoryName = "🛒 Business",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "Unit Price = Total Price / Total Quantity",
        explanationText = "Find unit cost to compare item value across different pack sizes."
    ) {
        CalcInputField(value = totalPriceStr, onValueChange = { totalPriceStr = it }, label = "Total Price", prefix = "₹")
        CalcInputField(value = totalUnitsStr, onValueChange = { totalUnitsStr = it }, label = "Total Quantity / Units")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate Unit Price")
        }

        ResultCard(primaryValue = primaryVal, onReset = { totalPriceStr = ""; totalUnitsStr = ""; primaryVal = ""; errorMsg = null })
    }
}

// 3. Quantity x Price Calculator
@Composable
fun QtyPriceCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var qtyStr by remember { mutableStateOf("") }
    var pricePerUnitStr by remember { mutableStateOf("") }

    var primaryVal by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val qty = qtyStr.toDoubleOrNull()
        val unitPrice = pricePerUnitStr.toDoubleOrNull()

        if (qty == null || unitPrice == null || qty < 0 || unitPrice < 0) {
            errorMsg = "Please enter valid quantity and price per unit"
            return
        }
        errorMsg = null

        val total = qty * unitPrice
        primaryVal = "Total Bill: ${formatINR(total)}"
        onSaveHistory("Total for $qty × ${formatINR(unitPrice)}", primaryVal)
    }

    CalculatorLayout(
        title = "Quantity × Price Calculator",
        categoryName = "🛒 Business",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "Total Amount = Quantity × Unit Price",
        explanationText = "Quick total calculator for items bought or sold in bulk."
    ) {
        CalcInputField(value = qtyStr, onValueChange = { qtyStr = it }, label = "Quantity")
        CalcInputField(value = pricePerUnitStr, onValueChange = { pricePerUnitStr = it }, label = "Price per Unit", prefix = "₹")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate Total")
        }

        ResultCard(primaryValue = primaryVal, onReset = { qtyStr = ""; pricePerUnitStr = ""; primaryVal = ""; errorMsg = null })
    }
}

// 4. Selling Price Calculator
@Composable
fun SPCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var cpStr by remember { mutableStateOf("") }
    var profitPctStr by remember { mutableStateOf("") }

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val CP = cpStr.toDoubleOrNull()
        val profitPct = profitPctStr.toDoubleOrNull()

        if (CP == null || profitPct == null || CP < 0) {
            errorMsg = "Please enter valid Cost Price and Profit percentage"
            return
        }
        errorMsg = null

        val profitAmt = (CP * profitPct) / 100.0
        val SP = CP + profitAmt

        primaryVal = "Selling Price: ${formatINR(SP)}"
        details = listOf(
            "Cost Price" to formatINR(CP),
            "Profit Amount ($profitPct%)" to formatINR(profitAmt)
        )
        onSaveHistory("Selling Price for CP ${formatINR(CP)} + $profitPct%", primaryVal)
    }

    CalculatorLayout(
        title = "Selling Price Calculator",
        categoryName = "🛒 Business",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "Selling Price = Cost Price + (Cost Price × Profit% / 100)",
        explanationText = "Determine the required selling price to achieve target profit margin."
    ) {
        CalcInputField(value = cpStr, onValueChange = { cpStr = it }, label = "Cost Price (CP)", prefix = "₹")
        CalcInputField(value = profitPctStr, onValueChange = { profitPctStr = it }, label = "Desired Profit (%)", suffix = "%")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate Selling Price")
        }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { cpStr = ""; profitPctStr = ""; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}

// 5. Profit Margin Calculator
@Composable
fun ProfitMarginCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var revenueStr by remember { mutableStateOf("") }
    var cogsStr by remember { mutableStateOf("") }

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val revenue = revenueStr.toDoubleOrNull()
        val cogs = cogsStr.toDoubleOrNull()

        if (revenue == null || cogs == null || revenue <= 0) {
            errorMsg = "Please enter valid positive Revenue and Cost of Goods Sold"
            return
        }
        errorMsg = null

        val grossProfit = revenue - cogs
        val marginPct = (grossProfit / revenue) * 100.0
        val markupPct = (grossProfit / cogs) * 100.0

        primaryVal = "Profit Margin: ${"%.2f".format(marginPct)}%"
        details = listOf(
            "Gross Profit" to formatINR(grossProfit),
            "Markup on Cost" to "${"%.2f".format(markupPct)}%"
        )
        onSaveHistory("Margin for Revenue ${formatINR(revenue)}", primaryVal)
    }

    CalculatorLayout(
        title = "Profit Margin Calculator",
        categoryName = "🛒 Business",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "Margin % = [(Revenue - Cost) / Revenue] × 100",
        explanationText = "Calculate gross profit percentage and markup ratio."
    ) {
        CalcInputField(value = revenueStr, onValueChange = { revenueStr = it }, label = "Revenue / Total Sales", prefix = "₹")
        CalcInputField(value = cogsStr, onValueChange = { cogsStr = it }, label = "Cost of Goods Sold (COGS)", prefix = "₹")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate Profit Margin")
        }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { revenueStr = ""; cogsStr = ""; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}

// 6. Wholesale Price Calculator
@Composable
fun WholesalePriceCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var costStr by remember { mutableStateOf("") }
    var marginPctStr by remember { mutableStateOf("20") }

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val cost = costStr.toDoubleOrNull()
        val margin = marginPctStr.toDoubleOrNull() ?: 20.0

        if (cost == null || cost <= 0) {
            errorMsg = "Please enter manufacturing / acquisition cost"
            return
        }
        errorMsg = null

        val wholesalePrice = cost / (1 - (margin / 100.0))
        val profitPerUnit = wholesalePrice - cost

        primaryVal = "Wholesale Price: ${formatINR(wholesalePrice)}"
        details = listOf(
            "Acquisition Cost" to formatINR(cost),
            "Wholesale Profit / Unit" to formatINR(profitPerUnit)
        )
        onSaveHistory("Wholesale Price for cost ${formatINR(cost)}", primaryVal)
    }

    CalculatorLayout(
        title = "Wholesale Price Calculator",
        categoryName = "🛒 Business",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "Wholesale Price = Cost / (1 - Wholesale Margin%)",
        explanationText = "Determine competitive bulk wholesale price for retailers."
    ) {
        CalcInputField(value = costStr, onValueChange = { costStr = it }, label = "Cost per Unit", prefix = "₹")
        CalcInputField(value = marginPctStr, onValueChange = { marginPctStr = it }, label = "Target Wholesale Margin (%)", suffix = "%")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate Wholesale Price")
        }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { costStr = ""; marginPctStr = "20"; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}

// 7. Retail Price Calculator
@Composable
fun RetailPriceCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var wholesaleStr by remember { mutableStateOf("") }
    var markupPctStr by remember { mutableStateOf("50") }

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val wholesale = wholesaleStr.toDoubleOrNull()
        val markup = markupPctStr.toDoubleOrNull() ?: 50.0

        if (wholesale == null || wholesale <= 0) {
            errorMsg = "Please enter wholesale price"
            return
        }
        errorMsg = null

        val retailPrice = wholesale * (1 + (markup / 100.0))
        val profitPerUnit = retailPrice - wholesale

        primaryVal = "Retail MRP Price: ${formatINR(retailPrice)}"
        details = listOf(
            "Wholesale Price" to formatINR(wholesale),
            "Retailer Margin / Unit" to formatINR(profitPerUnit)
        )
        onSaveHistory("Retail Price for wholesale ${formatINR(wholesale)}", primaryVal)
    }

    CalculatorLayout(
        title = "Retail Price Calculator",
        categoryName = "🛒 Business",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "Retail Price = Wholesale Price × (1 + Markup%)",
        explanationText = "Calculate suggested retail selling price for shops."
    ) {
        CalcInputField(value = wholesaleStr, onValueChange = { wholesaleStr = it }, label = "Wholesale Price", prefix = "₹")
        CalcInputField(value = markupPctStr, onValueChange = { markupPctStr = it }, label = "Retail Markup (%)", suffix = "%")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate Retail Price")
        }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { wholesaleStr = ""; markupPctStr = "50"; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}

// 8. Discount + GST Calculator
@Composable
fun DiscGstCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var priceStr by remember { mutableStateOf("") }
    var discPctStr by remember { mutableStateOf("10") }
    var gstPctStr by remember { mutableStateOf("18") }

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val price = priceStr.toDoubleOrNull()
        val disc = discPctStr.toDoubleOrNull() ?: 0.0
        val gst = gstPctStr.toDoubleOrNull() ?: 0.0

        if (price == null || price <= 0) {
            errorMsg = "Please enter valid list price"
            return
        }
        errorMsg = null

        val discAmt = (price * disc) / 100.0
        val discountedPrice = price - discAmt
        val gstAmt = (discountedPrice * gst) / 100.0
        val finalBill = discountedPrice + gstAmt

        primaryVal = "Final Invoice Amount: ${formatINR(finalBill)}"
        details = listOf(
            "Original Price" to formatINR(price),
            "Discount ($disc%)" to "-${formatINR(discAmt)}",
            "Price After Discount" to formatINR(discountedPrice),
            "GST ($gst%)" to "+${formatINR(gstAmt)}"
        )
        onSaveHistory("Discount + GST Bill for ${formatINR(price)}", primaryVal)
    }

    CalculatorLayout(
        title = "Discount + GST Calculator",
        categoryName = "🛒 Business",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "Final Bill = (Price - Discount) + GST on Discounted Price",
        explanationText = "Applies discount first, then computes GST on discounted amount for business invoices."
    ) {
        CalcInputField(value = priceStr, onValueChange = { priceStr = it }, label = "MRP / List Price", prefix = "₹")
        CalcInputField(value = discPctStr, onValueChange = { discPctStr = it }, label = "Discount (%)", suffix = "%")
        CalcInputField(value = gstPctStr, onValueChange = { gstPctStr = it }, label = "GST (%)", suffix = "%")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate Total Invoice")
        }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { priceStr = ""; discPctStr = "10"; gstPctStr = "18"; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}

// 9. Bill & Change Counter Calculator
@Composable
fun BillCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var billAmtStr by remember { mutableStateOf("") }
    var cashPaidStr by remember { mutableStateOf("") }

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val bill = billAmtStr.toDoubleOrNull()
        val cash = cashPaidStr.toDoubleOrNull()

        if (bill == null || cash == null || bill < 0 || cash < 0) {
            errorMsg = "Please enter valid bill amount and cash paid"
            return
        }
        errorMsg = null

        val balance = cash - bill

        if (balance >= 0) {
            primaryVal = "Change to Return: ${formatINR(balance)}"
            details = listOf(
                "Bill Amount" to formatINR(bill),
                "Cash Received" to formatINR(cash),
                "Balance" to formatINR(balance)
            )
        } else {
            primaryVal = "Pending Payment: ${formatINR(-balance)}"
            details = listOf(
                "Bill Amount" to formatINR(bill),
                "Cash Received" to formatINR(cash),
                "Shortage" to formatINR(-balance)
            )
        }
        onSaveHistory("Bill ${formatINR(bill)}, Cash ${formatINR(cash)}", primaryVal)
    }

    CalculatorLayout(
        title = "Bill & Change Calculator",
        categoryName = "🛒 Business",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "Change = Cash Received - Bill Amount",
        explanationText = "Quick change counter for cashiers and shop owners."
    ) {
        CalcInputField(value = billAmtStr, onValueChange = { billAmtStr = it }, label = "Total Bill Amount", prefix = "₹")
        CalcInputField(value = cashPaidStr, onValueChange = { cashPaidStr = it }, label = "Cash Given by Customer", prefix = "₹")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate Change")
        }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { billAmtStr = ""; cashPaidStr = ""; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}

// 10. Markup Calculator
@Composable
fun MarkupCalculatorScreen(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit,
    onSaveHistory: (String, String) -> Unit
) {
    var costStr by remember { mutableStateOf("") }
    var sellingStr by remember { mutableStateOf("") }

    var primaryVal by remember { mutableStateOf("") }
    var details by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun calculate() {
        val cost = costStr.toDoubleOrNull()
        val selling = sellingStr.toDoubleOrNull()

        if (cost == null || selling == null || cost <= 0) {
            errorMsg = "Please enter valid positive Cost and Selling Price"
            return
        }
        errorMsg = null

        val profit = selling - cost
        val markup = (profit / cost) * 100.0
        val margin = (profit / selling) * 100.0

        primaryVal = "Markup: ${"%.2f".format(markup)}%"
        details = listOf(
            "Profit Amount" to formatINR(profit),
            "Corresponding Profit Margin" to "${"%.2f".format(margin)}%"
        )
        onSaveHistory("Markup for Cost ${formatINR(cost)}, SP ${formatINR(selling)}", primaryVal)
    }

    CalculatorLayout(
        title = "Markup Calculator",
        categoryName = "🛒 Business",
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onBackClick = onBackClick,
        formulaText = "Markup% = [(Selling Price - Cost) / Cost] × 100",
        explanationText = "Calculate percentage mark up added on top of cost price."
    ) {
        CalcInputField(value = costStr, onValueChange = { costStr = it }, label = "Cost Price", prefix = "₹")
        CalcInputField(value = sellingStr, onValueChange = { sellingStr = it }, label = "Selling Price", prefix = "₹")

        if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error)

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate Markup")
        }

        ResultCard(primaryValue = primaryVal, details = details, onReset = { costStr = ""; sellingStr = ""; primaryVal = ""; details = emptyList(); errorMsg = null })
    }
}
