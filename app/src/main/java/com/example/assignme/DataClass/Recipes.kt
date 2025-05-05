package com.example.assignme.DataClass

data class Recipes(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val ingredients: List<Ingredient> = emptyList(),
    val cookTime: String = "", // e.g. "45 min"
    val servings: Int = 0,
    val totalCalories: Int = 0,
    val totalProtein: Int = 0,
    val totalFat: Int = 0,
    val totalSaturatedFat: Int = 0,
    val totalCarbs: Int = 0,
    val totalFiber: Int = 0,
    val totalSugar: Int = 0,
    val totalSodium: Int = 0,       // in mg
    val totalPotassium: Int = 0,    // in mg
    val totalCholesterol: Int = 0,  // in mg
    val authorId: String = "",
    val imageUrl: String = "",
    val category: String = "", // breakfast, lunch, dinner, etc.
    val instructions: List<String> = emptyList()
)
