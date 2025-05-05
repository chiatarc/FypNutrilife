package com.example.assignme.DataClass

data class Ingredient(
    val name: String = "",
    val amount: String = "",               // Display string like "100g"
    val calories: Double = 0.0,
    val protein: Double = 0.0,
    val fat: Double = 0.0,
    val saturatedFat: Double = 0.0,
    val carbs: Double = 0.0,
    val fiber: Double = 0.0,
    val sugar: Double = 0.0,
    val sodium: Double = 0.0,              // in mg
    val potassium: Double = 0.0,           // in mg
    val cholesterol: Double = 0.0          // in mg
)
