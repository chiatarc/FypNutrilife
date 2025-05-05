package com.example.assignme.DataClass

data class SavedUserRecipes(
    val recipe: Recipes,  // The original recipe object
    val userId: String = ""
)
