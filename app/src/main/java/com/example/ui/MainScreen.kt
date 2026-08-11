package com.example.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.ThemeMode
import com.example.ui.calculators.*
import com.example.ui.screens.*
import com.example.ui.theme.AllInOneCalculatorTheme

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : BottomNavItem("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Favorites : BottomNavItem("favorites", "Favorites", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder)
    object History : BottomNavItem("history", "History", Icons.Filled.History, Icons.Outlined.History)
    object Developer : BottomNavItem("developer", "Developer", Icons.Filled.Code, Icons.Outlined.Code)
    object Settings : BottomNavItem("settings", "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
}

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val themeMode by viewModel.themeMode.collectAsState()

    AllInOneCalculatorTheme(themeMode = themeMode) {
        val navController = rememberNavController()
        val favoritesList by viewModel.favoritesList.collectAsState()
        val historyList by viewModel.historyList.collectAsState()
        val searchQuery by viewModel.searchQuery.collectAsState()
        val searchResults by viewModel.searchResults.collectAsState()
        val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        val bottomNavItems = listOf(
            BottomNavItem.Home,
            BottomNavItem.Favorites,
            BottomNavItem.History,
            BottomNavItem.Developer,
            BottomNavItem.Settings
        )

        val isBottomBarVisible = currentRoute in bottomNavItems.map { it.route }

        Scaffold(
            bottomBar = {
                if (isBottomBarVisible) {
                    NavigationBar {
                        bottomNavItems.forEach { item ->
                            val selected = currentRoute == item.route
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    if (currentRoute != item.route) {
                                        navController.navigate(item.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.title
                                    )
                                },
                                label = { Text(item.title) }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = BottomNavItem.Home.route,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Bottom Nav Tabs
                composable(BottomNavItem.Home.route) {
                    HomeScreen(
                        onCategoryClick = { categoryId ->
                            navController.navigate("category/$categoryId")
                        },
                        onCalculatorClick = { route ->
                            navController.navigate(route)
                        },
                        onSearchClick = {
                            navController.navigate("search")
                        }
                    )
                }

                composable(BottomNavItem.Favorites.route) {
                    FavoritesScreen(
                        favoritesList = favoritesList,
                        onCalculatorClick = { route -> navController.navigate(route) },
                        onRemoveFavorite = { calcId, name, cat -> viewModel.toggleFavorite(calcId, name, cat) }
                    )
                }

                composable(BottomNavItem.History.route) {
                    HistoryScreen(
                        historyList = historyList,
                        onDeleteHistoryItem = { id -> viewModel.deleteHistoryItem(id) },
                        onClearAllHistory = { viewModel.clearAllHistory() }
                    )
                }

                composable(BottomNavItem.Developer.route) {
                    DeveloperScreen()
                }

                composable(BottomNavItem.Settings.route) {
                    SettingsScreen(
                        currentThemeMode = themeMode,
                        onThemeModeChange = { mode -> viewModel.setThemeMode(mode) },
                        notificationsEnabled = notificationsEnabled,
                        onNotificationsEnabledChange = { enabled -> viewModel.setNotificationsEnabled(enabled) },
                        onClearAllHistory = { viewModel.clearAllHistory() }
                    )
                }

                // Search Screen
                composable("search") {
                    SearchScreen(
                        searchQuery = searchQuery,
                        onQueryChange = { query -> viewModel.updateSearchQuery(query) },
                        searchResults = searchResults,
                        onCalculatorClick = { route -> navController.navigate(route) },
                        onBackClick = { navController.popBackStack() }
                    )
                }

                // Category Detail Screen
                composable("category/{categoryId}") { backStackEntry ->
                    val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
                    CategoryDetailScreen(
                        categoryId = categoryId,
                        onBackClick = { navController.popBackStack() },
                        onCalculatorClick = { route -> navController.navigate(route) },
                        isFavorite = { id -> viewModel.isFavorite(id) },
                        onToggleFavorite = { id, name, cat -> viewModel.toggleFavorite(id, name, cat) }
                    )
                }

                // --- 40+ CALCULATOR ROUTES ---
                // Math (15)
                composable("calc/std_calc") {
                    StandardCalculatorScreen(
                        isFavorite = viewModel.isFavorite("std_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("std_calc", "Standard Calculator", "🔢 Math") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/sci_calc") {
                    ScientificCalculatorScreen(
                        isFavorite = viewModel.isFavorite("sci_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("sci_calc", "Scientific Calculator", "🔢 Math") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/percent_calc") {
                    PercentageCalculatorScreen(
                        isFavorite = viewModel.isFavorite("percent_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("percent_calc", "Percentage Calculator", "🔢 Math") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/fraction_calc") {
                    FractionCalculatorScreen(
                        isFavorite = viewModel.isFavorite("fraction_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("fraction_calc", "Fraction Calculator", "🔢 Math") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/ratio_calc") {
                    RatioCalculatorScreen(
                        isFavorite = viewModel.isFavorite("ratio_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("ratio_calc", "Ratio Calculator", "🔢 Math") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/avg_calc") {
                    AverageCalculatorScreen(
                        isFavorite = viewModel.isFavorite("avg_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("avg_calc", "Average Calculator", "🔢 Math") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/lcm_calc") {
                    LCMCalculatorScreen(
                        isFavorite = viewModel.isFavorite("lcm_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("lcm_calc", "LCM Calculator", "🔢 Math") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/hcf_calc") {
                    HCFCalculatorScreen(
                        isFavorite = viewModel.isFavorite("hcf_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("hcf_calc", "HCF / GCD Calculator", "🔢 Math") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/power_calc") {
                    PowerCalculatorScreen(
                        isFavorite = viewModel.isFavorite("power_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("power_calc", "Power Calculator", "🔢 Math") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/sqrt_calc") {
                    SquareRootCalculatorScreen(
                        isFavorite = viewModel.isFavorite("sqrt_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("sqrt_calc", "Square Root Calculator", "🔢 Math") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/cbrt_calc") {
                    CubeRootCalculatorScreen(
                        isFavorite = viewModel.isFavorite("cbrt_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("cbrt_calc", "Cube Root Calculator", "🔢 Math") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/prime_calc") {
                    PrimeCheckerScreen(
                        isFavorite = viewModel.isFavorite("prime_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("prime_calc", "Prime Number Checker", "🔢 Math") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/algebra_calc") {
                    AlgebraCalculatorScreen(
                        isFavorite = viewModel.isFavorite("algebra_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("algebra_calc", "Algebra Solver", "🔢 Math") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/log_calc") {
                    LogarithmCalculatorScreen(
                        isFavorite = viewModel.isFavorite("log_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("log_calc", "Logarithm Calculator", "🔢 Math") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/age_calc") {
                    AgeCalculatorScreen(
                        isFavorite = viewModel.isFavorite("age_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("age_calc", "Age Calculator", "🔢 Math") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }

                // Finance (13)
                composable("calc/emi_calc") {
                    EMICalculatorScreen(
                        isFavorite = viewModel.isFavorite("emi_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("emi_calc", "EMI Calculator", "💰 Finance") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/loan_calc") {
                    LoanCalculatorScreen(
                        isFavorite = viewModel.isFavorite("loan_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("loan_calc", "Loan Eligibility Calculator", "💰 Finance") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/simple_int_calc") {
                    SimpleInterestCalculatorScreen(
                        isFavorite = viewModel.isFavorite("simple_int_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("simple_int_calc", "Simple Interest Calculator", "💰 Finance") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/compound_int_calc") {
                    CompoundInterestCalculatorScreen(
                        isFavorite = viewModel.isFavorite("compound_int_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("compound_int_calc", "Compound Interest Calculator", "💰 Finance") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/gst_calc") {
                    GSTCalculatorScreen(
                        isFavorite = viewModel.isFavorite("gst_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("gst_calc", "GST Calculator", "💰 Finance") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/discount_calc") {
                    DiscountCalculatorScreen(
                        isFavorite = viewModel.isFavorite("discount_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("discount_calc", "Discount Calculator", "💰 Finance") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/pnl_calc") {
                    ProfitLossCalculatorScreen(
                        isFavorite = viewModel.isFavorite("pnl_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("pnl_calc", "Profit & Loss Calculator", "💰 Finance") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/salary_calc") {
                    SalaryCalculatorScreen(
                        isFavorite = viewModel.isFavorite("salary_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("salary_calc", "Salary In-Hand Calculator", "💰 Finance") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/sip_calc") {
                    SIPCalculatorScreen(
                        isFavorite = viewModel.isFavorite("sip_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("sip_calc", "SIP Calculator", "💰 Finance") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/fd_calc") {
                    FDCalculatorScreen(
                        isFavorite = viewModel.isFavorite("fd_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("fd_calc", "FD Calculator", "💰 Finance") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/rd_calc") {
                    RDCalculatorScreen(
                        isFavorite = viewModel.isFavorite("rd_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("rd_calc", "RD Calculator", "💰 Finance") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/investment_calc") {
                    InvestmentReturnCalculatorScreen(
                        isFavorite = viewModel.isFavorite("investment_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("investment_calc", "Investment Return Calculator", "💰 Finance") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/tax_calc") {
                    TaxCalculatorScreen(
                        isFavorite = viewModel.isFavorite("tax_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("tax_calc", "Income Tax Calculator", "💰 Finance") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }

                // Business (10)
                composable("calc/kg_gram_calc") {
                    KGToGramCalculatorScreen(
                        isFavorite = viewModel.isFavorite("kg_gram_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("kg_gram_calc", "Price per KG → Gram", "🛒 Business") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/unit_price_calc") {
                    UnitPriceCalculatorScreen(
                        isFavorite = viewModel.isFavorite("unit_price_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("unit_price_calc", "Unit Price Calculator", "🛒 Business") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/qty_price_calc") {
                    QtyPriceCalculatorScreen(
                        isFavorite = viewModel.isFavorite("qty_price_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("qty_price_calc", "Quantity × Price Calculator", "🛒 Business") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/sp_calc") {
                    SPCalculatorScreen(
                        isFavorite = viewModel.isFavorite("sp_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("sp_calc", "Selling Price Calculator", "🛒 Business") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/profit_margin_calc") {
                    ProfitMarginCalculatorScreen(
                        isFavorite = viewModel.isFavorite("profit_margin_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("profit_margin_calc", "Profit Margin Calculator", "🛒 Business") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/wholesale_calc") {
                    WholesalePriceCalculatorScreen(
                        isFavorite = viewModel.isFavorite("wholesale_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("wholesale_calc", "Wholesale Price Calculator", "🛒 Business") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/retail_calc") {
                    RetailPriceCalculatorScreen(
                        isFavorite = viewModel.isFavorite("retail_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("retail_calc", "Retail Price Calculator", "🛒 Business") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/disc_gst_calc") {
                    DiscGstCalculatorScreen(
                        isFavorite = viewModel.isFavorite("disc_gst_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("disc_gst_calc", "Discount + GST Calculator", "🛒 Business") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/bill_calc") {
                    BillCalculatorScreen(
                        isFavorite = viewModel.isFavorite("bill_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("bill_calc", "Bill Calculator", "🛒 Business") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/markup_calc") {
                    MarkupCalculatorScreen(
                        isFavorite = viewModel.isFavorite("markup_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("markup_calc", "Markup Calculator", "🛒 Business") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }

                // Student (12)
                composable("calc/marks_percent_calc") {
                    MarksPercentCalculatorScreen(
                        isFavorite = viewModel.isFavorite("marks_percent_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("marks_percent_calc", "Marks Percentage Calculator", "🎓 Student") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/cgpa_calc") {
                    CGPACalculatorScreen(
                        isFavorite = viewModel.isFavorite("cgpa_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("cgpa_calc", "CGPA Calculator", "🎓 Student") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/cgpa_percent_calc") {
                    CGPAPercentConverterScreen(
                        isFavorite = viewModel.isFavorite("cgpa_percent_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("cgpa_percent_calc", "CGPA → Percentage Converter", "🎓 Student") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/gpa_calc") {
                    GPACalculatorScreen(
                        isFavorite = viewModel.isFavorite("gpa_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("gpa_calc", "GPA Calculator", "🎓 Student") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/avg_marks_calc") {
                    AvgMarksCalculatorScreen(
                        isFavorite = viewModel.isFavorite("avg_marks_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("avg_marks_calc", "Average Marks Calculator", "🎓 Student") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/req_marks_calc") {
                    ReqMarksCalculatorScreen(
                        isFavorite = viewModel.isFavorite("req_marks_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("req_marks_calc", "Required Marks Calculator", "🎓 Student") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/grade_calc") {
                    GradeCalculatorScreen(
                        isFavorite = viewModel.isFavorite("grade_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("grade_calc", "Grade Calculator", "🎓 Student") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/physics_calc") {
                    PhysicsCalculatorScreen(
                        isFavorite = viewModel.isFavorite("physics_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("physics_calc", "Physics Calculator", "🎓 Student") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/geometry_calc") {
                    GeometryCalculatorScreen(
                        isFavorite = viewModel.isFavorite("geometry_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("geometry_calc", "Geometry Calculator", "🎓 Student") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/trig_calc") {
                    TrigCalculatorScreen(
                        isFavorite = viewModel.isFavorite("trig_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("trig_calc", "Trigonometry Calculator", "🎓 Student") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/sdt_calc") {
                    SDTCalculatorScreen(
                        isFavorite = viewModel.isFavorite("sdt_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("sdt_calc", "Speed-Distance-Time", "🎓 Student") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/work_power_calc") {
                    WorkPowerCalculatorScreen(
                        isFavorite = viewModel.isFavorite("work_power_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("work_power_calc", "Work & Power Calculator", "🎓 Student") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }

                // Electrical (9)
                composable("calc/ohms_law_calc") {
                    OhmsLawCalculatorScreen(
                        isFavorite = viewModel.isFavorite("ohms_law_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("ohms_law_calc", "Ohm's Law Calculator", "⚡ Electrical") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/voltage_calc") {
                    VoltageCalculatorScreen(
                        isFavorite = viewModel.isFavorite("voltage_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("voltage_calc", "Voltage Calculator", "⚡ Electrical") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/current_calc") {
                    CurrentCalculatorScreen(
                        isFavorite = viewModel.isFavorite("current_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("current_calc", "Current Calculator", "⚡ Electrical") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/resistance_calc") {
                    ResistanceCalculatorScreen(
                        isFavorite = viewModel.isFavorite("resistance_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("resistance_calc", "Resistance Calculator", "⚡ Electrical") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/elec_power_calc") {
                    ElecPowerCalculatorScreen(
                        isFavorite = viewModel.isFavorite("elec_power_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("elec_power_calc", "Electrical Power Calculator", "⚡ Electrical") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/elec_energy_calc") {
                    ElecEnergyCalculatorScreen(
                        isFavorite = viewModel.isFavorite("elec_energy_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("elec_energy_calc", "Electrical Energy Calculator", "⚡ Electrical") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/series_r_calc") {
                    SeriesRCalculatorScreen(
                        isFavorite = viewModel.isFavorite("series_r_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("series_r_calc", "Series Resistance", "⚡ Electrical") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/parallel_r_calc") {
                    ParallelRCalculatorScreen(
                        isFavorite = viewModel.isFavorite("parallel_r_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("parallel_r_calc", "Parallel Resistance", "⚡ Electrical") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/elec_cost_calc") {
                    ElectricityCostCalculatorScreen(
                        isFavorite = viewModel.isFavorite("elec_cost_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("elec_cost_calc", "Electricity Cost Calculator", "⚡ Electrical") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }

                // Converters (2)
                composable("calc/unit_converters") {
                    LengthConverterScreen(
                        isFavorite = viewModel.isFavorite("unit_converters"),
                        onToggleFavorite = { viewModel.toggleFavorite("unit_converters", "Unit Converters", "📏 Converters") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/currency_converter") {
                    CurrencyConverterScreen(
                        isFavorite = viewModel.isFavorite("currency_converter"),
                        onToggleFavorite = { viewModel.toggleFavorite("currency_converter", "Currency Converter", "📏 Converters") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }

                // Daily Life (10)
                composable("calc/daily_age_calc") {
                    AgeCalculatorScreen(
                        isFavorite = viewModel.isFavorite("daily_age_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("daily_age_calc", "Age Calculator", "🏠 Daily Life") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/date_diff_calc") {
                    DateDiffCalculatorScreen(
                        isFavorite = viewModel.isFavorite("date_diff_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("date_diff_calc", "Date Difference Calculator", "🏠 Daily Life") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/time_duration_calc") {
                    TimeDurCalculatorScreen(
                        isFavorite = viewModel.isFavorite("time_duration_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("time_duration_calc", "Time Duration Calculator", "🏠 Daily Life") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/daily_elec_calc") {
                    ElecBillEstCalculatorScreen(
                        isFavorite = viewModel.isFavorite("daily_elec_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("daily_elec_calc", "Electricity Bill Calculator", "🏠 Daily Life") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/daily_fuel_calc") {
                    FuelCostCalculatorScreen(
                        isFavorite = viewModel.isFavorite("daily_fuel_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("daily_fuel_calc", "Fuel Cost Calculator", "🏠 Daily Life") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/trip_cost_calc") {
                    FuelCostCalculatorScreen(
                        isFavorite = viewModel.isFavorite("trip_cost_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("trip_cost_calc", "Trip Cost Calculator", "🏠 Daily Life") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/tip_calc") {
                    TipCalculatorScreen(
                        isFavorite = viewModel.isFavorite("tip_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("tip_calc", "Tip Calculator", "🏠 Daily Life") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/split_bill_calc") {
                    TipCalculatorScreen(
                        isFavorite = viewModel.isFavorite("split_bill_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("split_bill_calc", "Split Bill Calculator", "🏠 Daily Life") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/daily_percent_calc") {
                    PercentageCalculatorScreen(
                        isFavorite = viewModel.isFavorite("daily_percent_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("daily_percent_calc", "Percentage Increase/Decrease", "🏠 Daily Life") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/countdown_calc") {
                    DateDiffCalculatorScreen(
                        isFavorite = viewModel.isFavorite("countdown_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("countdown_calc", "Date Countdown Calculator", "🏠 Daily Life") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }

                // Vehicle (5)
                composable("calc/mileage_calc") {
                    VehicleMileageCalculatorScreen(
                        isFavorite = viewModel.isFavorite("mileage_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("mileage_calc", "Fuel Mileage Calculator", "🚗 Vehicle") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/vehicle_fuel_cost") {
                    FuelPerKMCalculatorScreen(
                        isFavorite = viewModel.isFavorite("vehicle_fuel_cost"),
                        onToggleFavorite = { viewModel.toggleFavorite("vehicle_fuel_cost", "Fuel Cost Calculator", "🚗 Vehicle") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/trip_fuel_calc") {
                    FuelCostCalculatorScreen(
                        isFavorite = viewModel.isFavorite("trip_fuel_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("trip_fuel_calc", "Trip Fuel Calculator", "🚗 Vehicle") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/distance_calc") {
                    VehicleMileageCalculatorScreen(
                        isFavorite = viewModel.isFavorite("distance_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("distance_calc", "Distance Calculator", "🚗 Vehicle") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/mileage_compare_calc") {
                    UnitCompCalculatorScreen(
                        isFavorite = viewModel.isFavorite("mileage_compare_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("mileage_compare_calc", "Mileage Comparison", "🚗 Vehicle") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }

                // Health (6)
                composable("calc/bmi_calc") {
                    BMICalculatorScreen(
                        isFavorite = viewModel.isFavorite("bmi_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("bmi_calc", "BMI Calculator", "❤️ Health") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/bmr_calc") {
                    BMRCalculatorScreen(
                        isFavorite = viewModel.isFavorite("bmr_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("bmr_calc", "BMR Calculator", "❤️ Health") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/ideal_weight_calc") {
                    IdealWeightCalculatorScreen(
                        isFavorite = viewModel.isFavorite("ideal_weight_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("ideal_weight_calc", "Ideal Weight Calculator", "❤️ Health") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/calorie_calc") {
                    DailyCalorieCalculatorScreen(
                        isFavorite = viewModel.isFavorite("calorie_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("calorie_calc", "Calorie Calculator", "❤️ Health") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/water_calc") {
                    HealthWaterCalculatorScreen(
                        isFavorite = viewModel.isFavorite("water_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("water_calc", "Water Intake Calculator", "❤️ Health") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/body_fat_calc") {
                    BodyFatCalculatorScreen(
                        isFavorite = viewModel.isFavorite("body_fat_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("body_fat_calc", "Body Fat Estimate", "❤️ Health") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }

                // Tools (10)
                composable("calc/num_to_words_calc") {
                    NumToWordsCalculatorScreen(
                        isFavorite = viewModel.isFavorite("num_to_words_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("num_to_words_calc", "Number to Words Converter", "🧰 Tools") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/roman_calc") {
                    RomanCalculatorScreen(
                        isFavorite = viewModel.isFavorite("roman_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("roman_calc", "Roman Number Converter", "🧰 Tools") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/binary_calc") {
                    BinaryCalculatorScreen(
                        isFavorite = viewModel.isFavorite("binary_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("binary_calc", "Binary Converter", "🧰 Tools") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/decimal_calc") {
                    DecimalCalculatorScreen(
                        isFavorite = viewModel.isFavorite("decimal_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("decimal_calc", "Decimal Base Converter", "🧰 Tools") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/hex_calc") {
                    HexCalculatorScreen(
                        isFavorite = viewModel.isFavorite("hex_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("hex_calc", "Hexadecimal Converter", "🧰 Tools") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/random_num_calc") {
                    RandomNumCalculatorScreen(
                        isFavorite = viewModel.isFavorite("random_num_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("random_num_calc", "Random Number Generator", "🧰 Tools") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/tools_percent_calc") {
                    ToolsPercentCalculatorScreen(
                        isFavorite = viewModel.isFavorite("tools_percent_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("tools_percent_calc", "Percentage Change", "🧰 Tools") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/password_gen_calc") {
                    PasswordGenCalculatorScreen(
                        isFavorite = viewModel.isFavorite("password_gen_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("password_gen_calc", "Password Generator", "🧰 Tools") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/num_checker_calc") {
                    NumCheckerCalculatorScreen(
                        isFavorite = viewModel.isFavorite("num_checker_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("num_checker_calc", "Number Property Checker", "🧰 Tools") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
                composable("calc/history_tool_calc") {
                    HistoryToolCalculatorScreen(
                        isFavorite = viewModel.isFavorite("history_tool_calc"),
                        onToggleFavorite = { viewModel.toggleFavorite("history_tool_calc", "Calculation History", "🧰 Tools") },
                        onBackClick = { navController.popBackStack() },
                        onSaveHistory = { desc, res -> viewModel.saveCalculationHistory(desc, res) }
                    )
                }
            }
        }
    }
}
