package com.example.model

object CalculatorsRegistry {

    val categories = listOf(
        CategoryModel(
            id = "math",
            name = "🔢 Math",
            iconName = "Functions",
            description = "Algebra, GCD, LCM, Square root, Powers, Fractions",
            count = 15
        ),
        CategoryModel(
            id = "finance",
            name = "💰 Finance",
            iconName = "AttachMoney",
            description = "EMI, GST, SIP, FD, Interest, Taxes & Loans",
            count = 13
        ),
        CategoryModel(
            id = "business",
            name = "🛒 Business",
            iconName = "ShoppingBag",
            description = "KG to Gram, Wholesale, Margin, Bill & Retail",
            count = 10
        ),
        CategoryModel(
            id = "student",
            name = "🎓 Student",
            iconName = "School",
            description = "CGPA, Marks %, GPA, Grades, Physics & Geometry",
            count = 12
        ),
        CategoryModel(
            id = "electrical",
            name = "⚡ Electrical",
            iconName = "ElectricalServices",
            description = "Ohm's Law, Power, Voltage, Series & Parallel R",
            count = 9
        ),
        CategoryModel(
            id = "converters",
            name = "📏 Converters",
            iconName = "Straighten",
            description = "Length, Weight, Temp, Currency, Volume, Storage",
            count = 9
        ),
        CategoryModel(
            id = "daily_life",
            name = "🏠 Daily Life",
            iconName = "Home",
            description = "Age, Date Diff, Tip, Split Bill, Fuel & Time",
            count = 10
        ),
        CategoryModel(
            id = "vehicle",
            name = "🚗 Vehicle",
            iconName = "DirectionsCar",
            description = "Fuel Mileage, Distance, Trip Cost & Mileage Compare",
            count = 5
        ),
        CategoryModel(
            id = "health",
            name = "❤️ Health",
            iconName = "Favorite",
            description = "BMI, BMR, Calories, Ideal Weight, Water Intake",
            count = 6
        ),
        CategoryModel(
            id = "tools",
            name = "🧰 Tools",
            iconName = "Build",
            description = "Number to Words, Binary, Roman, Passwords & Checker",
            count = 10
        )
    )

    val calculators = listOf(
        // Math (15)
        CalculatorModel("std_calc", "Standard Calculator", "math", "🔢 Math", "Basic arithmetic calculations (+, -, ×, ÷)", listOf("calculator", "standard", "basic", "math", "add", "subtract"), "calc/std_calc"),
        CalculatorModel("sci_calc", "Scientific Calculator", "math", "🔢 Math", "Trigonometric, logarithmic, and power functions", listOf("scientific", "sin", "cos", "tan", "log", "math"), "calc/sci_calc"),
        CalculatorModel("percent_calc", "Percentage Calculator", "math", "🔢 Math", "Calculate percentage, increase, decrease & parts", listOf("percentage", "percent", "math", "%", "part"), "calc/percent_calc"),
        CalculatorModel("fraction_calc", "Fraction Calculator", "math", "🔢 Math", "Add, subtract, multiply and divide fractions", listOf("fraction", "math", "numerator", "denominator"), "calc/fraction_calc"),
        CalculatorModel("ratio_calc", "Ratio Calculator", "math", "🔢 Math", "Simplify and solve ratios (A:B = C:D)", listOf("ratio", "proportion", "math"), "calc/ratio_calc"),
        CalculatorModel("avg_calc", "Average Calculator", "math", "🔢 Math", "Find mean, sum, count of numbers", listOf("average", "mean", "sum", "math"), "calc/avg_calc"),
        CalculatorModel("lcm_calc", "LCM Calculator", "math", "🔢 Math", "Lowest Common Multiple of numbers", listOf("lcm", "least common multiple", "math"), "calc/lcm_calc"),
        CalculatorModel("hcf_calc", "HCF / GCD Calculator", "math", "🔢 Math", "Highest Common Factor / Greatest Common Divisor", listOf("hcf", "gcd", "greatest common divisor", "factor", "math"), "calc/hcf_calc"),
        CalculatorModel("power_calc", "Power Calculator", "math", "🔢 Math", "Calculate exponent (x^y)", listOf("power", "exponent", "base", "math"), "calc/power_calc"),
        CalculatorModel("sqrt_calc", "Square Root Calculator", "math", "🔢 Math", "Calculate square root (√x) and higher roots", listOf("square root", "sqrt", "math"), "calc/sqrt_calc"),
        CalculatorModel("cbrt_calc", "Cube Root Calculator", "math", "🔢 Math", "Calculate cube root (∛x)", listOf("cube root", "cbrt", "math"), "calc/cbrt_calc"),
        CalculatorModel("prime_calc", "Prime Number Checker", "math", "🔢 Math", "Check if a number is prime and list factors", listOf("prime", "factor", "number", "math"), "calc/prime_calc"),
        CalculatorModel("algebra_calc", "Algebra Solver (Quadratic)", "math", "🔢 Math", "Solve quadratic equations ax² + bx + c = 0", listOf("algebra", "quadratic", "equation", "math", "roots"), "calc/algebra_calc"),
        CalculatorModel("log_calc", "Logarithm Calculator", "math", "🔢 Math", "Calculate log₁₀, logₑ (ln) and custom base log", listOf("logarithm", "log", "ln", "natural log", "math"), "calc/log_calc"),
        CalculatorModel("age_calc", "Age Calculator", "math", "🔢 Math", "Exact age in years, months, days, hours", listOf("age", "dob", "birthday", "math"), "calc/age_calc"),

        // Finance (13)
        CalculatorModel("emi_calc", "EMI Calculator", "finance", "💰 Finance", "Equated Monthly Installment for home/car/personal loan", listOf("emi", "loan", "bank", "monthly", "finance", "interest"), "calc/emi_calc"),
        CalculatorModel("loan_calc", "Loan Eligibility Calculator", "finance", "💰 Finance", "Total payable amount and loan eligibility", listOf("loan", "finance", "principal", "bank"), "calc/loan_calc"),
        CalculatorModel("simple_int_calc", "Simple Interest Calculator", "finance", "💰 Finance", "Interest = (P × R × T) / 100", listOf("simple interest", "si", "finance", "interest"), "calc/simple_int_calc"),
        CalculatorModel("compound_int_calc", "Compound Interest Calculator", "finance", "💰 Finance", "A = P(1 + r/n)^(nt) compound interest", listOf("compound interest", "ci", "finance", "growth"), "calc/compound_int_calc"),
        CalculatorModel("gst_calc", "GST Calculator", "finance", "💰 Finance", "Indian GST rates (5%, 12%, 18%, 28%, custom)", listOf("gst", "tax", "finance", "price", "vat"), "calc/gst_calc"),
        CalculatorModel("discount_calc", "Discount Calculator", "finance", "💰 Finance", "Calculate original price, discount % and final price", listOf("discount", "sale", "offer", "finance", "shopping"), "calc/discount_calc"),
        CalculatorModel("pnl_calc", "Profit & Loss Calculator", "finance", "💰 Finance", "Cost price, selling price, profit/loss percentage", listOf("profit", "loss", "finance", "business", "margin"), "calc/pnl_calc"),
        CalculatorModel("salary_calc", "Salary In-Hand Calculator", "finance", "💰 Finance", "Gross salary to net in-hand salary estimate", listOf("salary", "paycheck", "income", "tax", "finance"), "calc/salary_calc"),
        CalculatorModel("sip_calc", "SIP Calculator", "finance", "💰 Finance", "Systematic Investment Plan returns estimator", listOf("sip", "mutual fund", "investment", "finance", "returns"), "calc/sip_calc"),
        CalculatorModel("fd_calc", "FD Calculator", "finance", "💰 Finance", "Fixed Deposit maturity amount and interest", listOf("fd", "fixed deposit", "bank", "finance"), "calc/fd_calc"),
        CalculatorModel("rd_calc", "RD Calculator", "finance", "💰 Finance", "Recurring Deposit maturity value", listOf("rd", "recurring deposit", "bank", "finance"), "calc/rd_calc"),
        CalculatorModel("investment_calc", "Investment Return Calculator", "finance", "💰 Finance", "Lumpsum investment growth and CAGR", listOf("investment", "return", "cagr", "lumpsum", "finance"), "calc/investment_calc"),
        CalculatorModel("tax_calc", "Income Tax Calculator", "finance", "💰 Finance", "Income tax calculation based on tax slabs", listOf("tax", "income tax", "tds", "finance", "slabs"), "calc/tax_calc"),

        // Business (10)
        CalculatorModel("kg_gram_calc", "Price per KG → Gram", "business", "🛒 Business", "Quick grocery/shop calculation for weight to price", listOf("kg", "gram", "shop", "price", "business", "groceries"), "calc/kg_gram_calc"),
        CalculatorModel("unit_price_calc", "Unit Price Calculator", "business", "🛒 Business", "Compare cost per unit for smart shopping", listOf("unit price", "comparison", "shopping", "business"), "calc/unit_price_calc"),
        CalculatorModel("qty_price_calc", "Quantity × Price Calculator", "business", "🛒 Business", "Total price for items in bulk or retail", listOf("quantity", "price", "item", "total", "business"), "calc/qty_price_calc"),
        CalculatorModel("sp_calc", "Selling Price Calculator", "business", "🛒 Business", "Calculate SP from CP and target profit %", listOf("selling price", "cost price", "profit", "business"), "calc/sp_calc"),
        CalculatorModel("profit_margin_calc", "Profit Margin Calculator", "business", "🛒 Business", "Gross margin % and profit margin calculation", listOf("margin", "profit margin", "business"), "calc/profit_margin_calc"),
        CalculatorModel("wholesale_calc", "Wholesale Price Calculator", "business", "🛒 Business", "Bulk discount and wholesale margin pricing", listOf("wholesale", "bulk", "business", "trade"), "calc/wholesale_calc"),
        CalculatorModel("retail_calc", "Retail Price Calculator", "business", "🛒 Business", "Retail price with markup percentage", listOf("retail", "price", "business", "markup"), "calc/retail_calc"),
        CalculatorModel("disc_gst_calc", "Discount + GST Calculator", "business", "🛒 Business", "Combined discount and GST invoice calculator", listOf("discount gst", "gst", "business", "invoice"), "calc/disc_gst_calc"),
        CalculatorModel("bill_calc", "Bill Calculator", "business", "🛒 Business", "Quick shop billing and change counter", listOf("bill", "shop", "receipt", "cash", "business"), "calc/bill_calc"),
        CalculatorModel("markup_calc", "Markup Calculator", "business", "🛒 Business", "Calculate markup percentage from cost and price", listOf("markup", "business", "cost"), "calc/markup_calc"),

        // Student (12)
        CalculatorModel("marks_percent_calc", "Marks Percentage Calculator", "student", "🎓 Student", "Convert obtained/total marks to percentage", listOf("marks", "percentage", "exam", "student", "score"), "calc/marks_percent_calc"),
        CalculatorModel("cgpa_calc", "CGPA Calculator", "student", "🎓 Student", "Calculate CGPA from grade points and credits", listOf("cgpa", "grades", "student", "college", "university"), "calc/cgpa_calc"),
        CalculatorModel("cgpa_percent_calc", "CGPA → Percentage Converter", "student", "🎓 Student", "Convert CGPA to percentage (e.g., 9.5 multiplier)", listOf("cgpa to percentage", "convert", "student", "board"), "calc/cgpa_percent_calc"),
        CalculatorModel("gpa_calc", "GPA Calculator", "student", "🎓 Student", "Calculate GPA for semester courses", listOf("gpa", "semester", "student", "credits"), "calc/gpa_calc"),
        CalculatorModel("avg_marks_calc", "Average Marks Calculator", "student", "🎓 Student", "Average score across multiple subjects", listOf("average marks", "subject", "student"), "calc/avg_marks_calc"),
        CalculatorModel("req_marks_calc", "Required Marks Calculator", "student", "🎓 Student", "Calculate score needed in final exam for target grade", listOf("required marks", "target", "exam", "student"), "calc/req_marks_calc"),
        CalculatorModel("grade_calc", "Grade Calculator", "student", "🎓 Student", "Map percentage to letter grade (A+, A, B, etc.)", listOf("grade", "letter grade", "student"), "calc/grade_calc"),
        CalculatorModel("physics_calc", "Physics Calculator", "student", "🎓 Student", "Force (F=ma), Kinetic Energy, Velocity", listOf("physics", "force", "velocity", "energy", "student"), "calc/physics_calc"),
        CalculatorModel("geometry_calc", "Geometry Calculator", "student", "🎓 Student", "Area, perimeter, volume for 2D & 3D shapes", listOf("geometry", "area", "volume", "circle", "triangle", "student"), "calc/geometry_calc"),
        CalculatorModel("trig_calc", "Trigonometry Calculator", "student", "🎓 Student", "Sin, Cos, Tan, Hypotenuse and angles", listOf("trigonometry", "sin", "cos", "tan", "triangle", "student"), "calc/trig_calc"),
        CalculatorModel("sdt_calc", "Speed-Distance-Time", "student", "🎓 Student", "Calculate speed, distance or time required", listOf("speed", "distance", "time", "student", "physics"), "calc/sdt_calc"),
        CalculatorModel("work_power_calc", "Work & Power Calculator", "student", "🎓 Student", "Work = Force × Distance, Power = Work / Time", listOf("work", "power", "student", "physics"), "calc/work_power_calc"),

        // Electrical (9)
        CalculatorModel("ohms_law_calc", "Ohm's Law Calculator", "electrical", "⚡ Electrical", "V = I × R relationships", listOf("ohm", "voltage", "current", "resistance", "electrical", "v=ir"), "calc/ohms_law_calc"),
        CalculatorModel("voltage_calc", "Voltage Calculator", "electrical", "⚡ Electrical", "Voltage from Current & Resistance or Power", listOf("voltage", "volts", "electrical"), "calc/voltage_calc"),
        CalculatorModel("current_calc", "Current Calculator", "electrical", "⚡ Electrical", "Current from Voltage & Resistance or Power", listOf("current", "amperes", "amps", "electrical"), "calc/current_calc"),
        CalculatorModel("resistance_calc", "Resistance Calculator", "electrical", "⚡ Electrical", "Resistance from Voltage & Current", listOf("resistance", "ohms", "electrical"), "calc/resistance_calc"),
        CalculatorModel("elec_power_calc", "Electrical Power Calculator", "electrical", "⚡ Electrical", "Power P = V × I = I²R = V²/R", listOf("power", "watts", "kw", "electrical"), "calc/elec_power_calc"),
        CalculatorModel("elec_energy_calc", "Electrical Energy Calculator", "electrical", "⚡ Electrical", "Energy in kWh (Units) = Power (W) × Hours / 1000", listOf("energy", "kwh", "units", "electrical"), "calc/elec_energy_calc"),
        CalculatorModel("series_r_calc", "Series Resistance", "electrical", "⚡ Electrical", "Total resistance R_total = R1 + R2 + ...", listOf("series", "resistor", "electrical"), "calc/series_r_calc"),
        CalculatorModel("parallel_r_calc", "Parallel Resistance", "electrical", "⚡ Electrical", "Total resistance 1/R_total = 1/R1 + 1/R2 + ...", listOf("parallel", "resistor", "electrical"), "calc/parallel_r_calc"),
        CalculatorModel("elec_cost_calc", "Electricity Cost Calculator", "electrical", "⚡ Electrical", "Estimate appliance electricity bill cost", listOf("electricity bill", "cost", "kwh", "units", "electrical"), "calc/elec_cost_calc"),

        // Converters (9)
        CalculatorModel("unit_converters", "Unit Converters (All Units)", "converters", "📏 Converters", "Convert Length, Weight, Area, Volume, Temp, Speed, Time, Data", listOf("converter", "length", "weight", "mass", "temperature", "speed", "volume", "data"), "calc/unit_converters"),
        CalculatorModel("currency_converter", "Currency Converter", "converters", "📏 Converters", "INR, USD, EUR, GBP, JPY, AUD, CAD exchange rates", listOf("currency", "rupee", "dollar", "euro", "exchange", "converter"), "calc/currency_converter"),

        // Daily Life (10)
        CalculatorModel("daily_age_calc", "Age Calculator", "daily_life", "🏠 Daily Life", "Exact age and next birthday countdown", listOf("age", "birthday", "daily"), "calc/daily_age_calc"),
        CalculatorModel("date_diff_calc", "Date Difference Calculator", "daily_life", "🏠 Daily Life", "Number of days/weeks between two dates", listOf("date difference", "days", "calendar", "daily"), "calc/date_diff_calc"),
        CalculatorModel("time_duration_calc", "Time Duration Calculator", "daily_life", "🏠 Daily Life", "Hours and minutes between start & end time", listOf("time duration", "hours", "clock", "daily"), "calc/time_duration_calc"),
        CalculatorModel("daily_elec_calc", "Electricity Bill Calculator", "daily_life", "🏠 Daily Life", "Monthly household bill based on slab units", listOf("electricity bill", "bill", "units", "daily"), "calc/daily_elec_calc"),
        CalculatorModel("daily_fuel_calc", "Fuel Cost Calculator", "daily_life", "🏠 Daily Life", "Cost of fuel for a given distance and mileage", listOf("fuel cost", "petrol", "diesel", "daily"), "calc/daily_fuel_calc"),
        CalculatorModel("trip_cost_calc", "Trip Cost Calculator", "daily_life", "🏠 Daily Life", "Total cost for travel (fuel, toll, food, extra)", listOf("trip cost", "travel", "vacation", "daily"), "calc/trip_cost_calc"),
        CalculatorModel("tip_calc", "Tip Calculator", "daily_life", "🏠 Daily Life", "Calculate tip amount and total bill at restaurant", listOf("tip", "restaurant", "bill", "daily"), "calc/tip_calc"),
        CalculatorModel("split_bill_calc", "Split Bill Calculator", "daily_life", "🏠 Daily Life", "Divide bill evenly among group of friends", listOf("split bill", "friends", "group", "daily"), "calc/split_bill_calc"),
        CalculatorModel("daily_percent_calc", "Percentage Increase/Decrease", "daily_life", "🏠 Daily Life", "Percentage change from initial to final value", listOf("percentage increase", "percentage decrease", "change"), "calc/daily_percent_calc"),
        CalculatorModel("countdown_calc", "Date Countdown Calculator", "daily_life", "🏠 Daily Life", "Days remaining for an upcoming event or date", listOf("countdown", "event", "date", "daily"), "calc/countdown_calc"),

        // Vehicle (5)
        CalculatorModel("mileage_calc", "Fuel Mileage Calculator", "vehicle", "🚗 Vehicle", "Mileage in km/L or L/100km from distance and fuel used", listOf("mileage", "fuel mileage", "km/l", "vehicle", "car", "bike"), "calc/mileage_calc"),
        CalculatorModel("vehicle_fuel_cost", "Fuel Cost Calculator", "vehicle", "🚗 Vehicle", "Calculate fuel needed and total price", listOf("fuel cost", "price", "vehicle", "petrol"), "calc/vehicle_fuel_cost"),
        CalculatorModel("trip_fuel_calc", "Trip Fuel Calculator", "vehicle", "🚗 Vehicle", "Litres of fuel needed for a planned trip", listOf("trip fuel", "litres", "trip", "vehicle"), "calc/trip_fuel_calc"),
        CalculatorModel("distance_calc", "Distance Calculator", "vehicle", "🚗 Vehicle", "Distance readable from fuel budget & mileage", listOf("distance", "budget", "vehicle"), "calc/distance_calc"),
        CalculatorModel("mileage_compare_calc", "Mileage Comparison", "vehicle", "🚗 Vehicle", "Compare fuel cost savings between two vehicles", listOf("mileage compare", "savings", "compare", "vehicle"), "calc/mileage_compare_calc"),

        // Health (6)
        CalculatorModel("bmi_calc", "BMI Calculator", "health", "❤️ Health", "Body Mass Index & weight category", listOf("bmi", "body mass index", "health", "weight", "height"), "calc/bmi_calc"),
        CalculatorModel("bmr_calc", "BMR Calculator", "health", "❤️ Health", "Basal Metabolic Rate (calories burned at rest)", listOf("bmr", "metabolism", "health", "calories"), "calc/bmr_calc"),
        CalculatorModel("ideal_weight_calc", "Ideal Weight Calculator", "health", "❤️ Health", "Ideal weight range based on height and body structure", listOf("ideal weight", "weight", "health"), "calc/ideal_weight_calc"),
        CalculatorModel("calorie_calc", "Calorie Calculator", "health", "❤️ Health", "Daily calorie needs for maintenance or weight loss", listOf("calorie", "daily calories", "health", "diet"), "calc/calorie_calc"),
        CalculatorModel("water_calc", "Water Intake Calculator", "health", "❤️ Health", "Recommended daily water intake in litres", listOf("water intake", "hydration", "health"), "calc/water_calc"),
        CalculatorModel("body_fat_calc", "Body Fat Estimate", "health", "❤️ Health", "Estimated body fat percentage", listOf("body fat", "fat percentage", "health"), "calc/body_fat_calc"),

        // Tools (10)
        CalculatorModel("num_to_words_calc", "Number to Words Converter", "tools", "🧰 Tools", "Convert numbers to words (Rupees & Lakhs/Crores)", listOf("number to words", "words", "cheque", "tools"), "calc/num_to_words_calc"),
        CalculatorModel("roman_calc", "Roman Number Converter", "tools", "🧰 Tools", "Convert between Roman numerals and Decimals", listOf("roman numeral", "roman", "converter", "tools"), "calc/roman_calc"),
        CalculatorModel("binary_calc", "Binary Converter", "tools", "🧰 Tools", "Convert binary, decimal, octal and hex", listOf("binary", "bits", "bytes", "tools"), "calc/binary_calc"),
        CalculatorModel("decimal_calc", "Decimal Base Converter", "tools", "🧰 Tools", "Convert numbers between base 2, 8, 10, 16", listOf("decimal", "hexadecimal", "octal", "binary", "tools"), "calc/decimal_calc"),
        CalculatorModel("hex_calc", "Hexadecimal Converter", "tools", "🧰 Tools", "Convert text/number to hexadecimal & RGB colors", listOf("hex", "hexadecimal", "tools"), "calc/hex_calc"),
        CalculatorModel("random_num_calc", "Random Number Generator", "tools", "🧰 Tools", "Generate random numbers or dice rolls within range", listOf("random", "dice", "generator", "tools"), "calc/random_num_calc"),
        CalculatorModel("tools_percent_calc", "Percentage Increase/Decrease", "tools", "🧰 Tools", "Calculate percentage change", listOf("percentage change", "tools"), "calc/tools_percent_calc"),
        CalculatorModel("password_gen_calc", "Password Generator", "tools", "🧰 Tools", "Generate secure random passwords", listOf("password", "security", "generator", "tools"), "calc/password_gen_calc"),
        CalculatorModel("num_checker_calc", "Number Checker", "tools", "🧰 Tools", "Check Even/Odd, Prime, Armstrong, Palindrome", listOf("number checker", "even odd", "armstrong", "palindrome", "tools"), "calc/num_checker_calc"),
        CalculatorModel("history_tool_calc", "Calculation History", "tools", "🧰 Tools", "Access local history log of past calculations", listOf("history", "past", "log", "tools"), "calc/history_tool_calc")
    )

    fun searchCalculators(query: String): List<CalculatorModel> {
        if (query.isBlank()) return emptyList()
        val q = query.trim().lowercase()
        return calculators.filter { calc ->
            calc.name.lowercase().contains(q) ||
            calc.categoryName.lowercase().contains(q) ||
            calc.description.lowercase().contains(q) ||
            calc.keywords.any { it.lowercase().contains(q) }
        }
    }
}
