package com.example.model

import androidx.compose.ui.graphics.vector.ImageVector

data class CalculatorModel(
    val id: String,
    val name: String,
    val categoryId: String,
    val categoryName: String,
    val description: String,
    val keywords: List<String>,
    val route: String
)

data class CategoryModel(
    val id: String,
    val name: String,
    val iconName: String,
    val description: String,
    val count: Int
)
