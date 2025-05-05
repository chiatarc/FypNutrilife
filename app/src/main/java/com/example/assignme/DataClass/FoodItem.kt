package com.example.assignme.DataClass

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import retrofit2.Callback
import retrofit2.Response

// API response model
data class FoodItem(
    val name: String,
    val calories: Double,
    val serving_size_g: Double,
    val protein_g: Double,
    val fat_total_g: Double,
    val fat_saturated_g: Double,
    val carbohydrates_total_g: Double,
    val fiber_g: Double,
    val sugar_g: Double,
    val sodium_mg: Double,
    val potassium_mg: Double,
    val cholesterol_mg: Double
)


// API service interface
interface CalorieNinjasApi {
    @Headers("X-Api-Key: 9Swi22FOhHhLZMEljMqTaA==FEPdS4BVlLsozkY3")
    @GET("v1/nutrition")
    fun getCalories(@Query("query") query: String): Call<CalorieNinjasResponse>
}

// Response model for API
data class CalorieNinjasResponse(
    val items: List<FoodItem>
)

// Retrofit setup
val retrofit = Retrofit.Builder()
    .baseUrl("https://api.calorieninjas.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val calorieNinjasApi = retrofit.create(CalorieNinjasApi::class.java)

fun fetchNutritionForIngredient(
    ingredient: String,
    onFetched: (FoodItem) -> Unit
) {
    val call = calorieNinjasApi.getCalories(ingredient)
    call.enqueue(object : Callback<CalorieNinjasResponse> {
        override fun onResponse(call: Call<CalorieNinjasResponse>, response: Response<CalorieNinjasResponse>) {
            val item = response.body()?.items?.firstOrNull()
            if (item != null) {
                val foodItem = FoodItem(
                    name = item.name,
                    calories = item.calories,
                    serving_size_g = item.serving_size_g,
                    protein_g = item.protein_g,
                    fat_total_g = item.fat_total_g,
                    fat_saturated_g = item.fat_saturated_g,
                    carbohydrates_total_g = item.carbohydrates_total_g,
                    fiber_g = item.fiber_g,
                    sugar_g = item.sugar_g,
                    sodium_mg = item.sodium_mg,
                    potassium_mg = item.potassium_mg,
                    cholesterol_mg = item.cholesterol_mg
                )
                onFetched(foodItem)
            } else {
                onFetched(defaultFoodItem(ingredient))
            }
        }

        override fun onFailure(call: Call<CalorieNinjasResponse>, t: Throwable) {
            onFetched(defaultFoodItem(ingredient))
        }
    })
}

fun defaultFoodItem(name: String) = FoodItem(
    name = name,
    calories = 0.0,
    serving_size_g = 100.0,
    protein_g = 0.0,
    fat_total_g = 0.0,
    fat_saturated_g = 0.0,
    carbohydrates_total_g = 0.0,
    fiber_g = 0.0,
    sugar_g = 0.0,
    sodium_mg = 0.0,
    potassium_mg = 0.0,
    cholesterol_mg = 0.0
)


