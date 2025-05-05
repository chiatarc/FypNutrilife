package com.example.assignme.GUI.Recipe

import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.assignme.DataClass.Recipes
import com.example.assignme.GUI.Recipe.ui.theme.Orange
import com.example.assignme.R
import com.example.assignme.ViewModel.RecipeViewModel
import com.example.assignme.ViewModel.UserViewModel
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeScreen(recipe: Recipes, userModel:UserViewModel, viewModel: RecipeViewModel = viewModel(),  onBackClick: () -> Unit) {
    val userId by userModel.userId.observeAsState()
    var isSaved by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = recipe.id) {
        // Call the suspend function from your ViewModel
        isSaved = viewModel.isRecipeSaved(userId.toString(), recipe.id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(recipe.title) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },

                //mh
                actions = {
                    IconButton(onClick = {
                        println(isSaved)
                        if (isSaved) {
                            // Handle unsaving the recipe
                            viewModel.removeRecipeFromSavedRecipes(
                                recipe.id,
                                userId.toString(),
                                onSuccess = {
                                    isSaved = false // Update state
                                    println("Recipe removed from saved recipes.")
                                },
                                onFailure = { exception ->
                                    println("Failed to remove recipe: ${exception.message}")
                                }
                            )
                        } else {
                            // Handle saving the recipe
                            viewModel.saveRecipeToSavedRecipes(
                                recipe = recipe,
                                userId = userId.toString(), // Pass the userId value here
                                onSuccess = {
                                    isSaved = true // Update state
                                    println("Recipe saved successfully.")
                                },
                                onFailure = { exception ->
                                    println("Failed to save recipe: ${exception.message}")
                                }
                            )
                        }
                    }) {
                        // Change the icon based on the saved state
                        if (isSaved) {
                            Icon(Icons.Default.Favorite, contentDescription = "Saved") // Filled icon
                        } else {
                            Icon(Icons.Default.FavoriteBorder, contentDescription = "Save Recipe") // Outlined icon
                        }
                    }
                }
            )
        },
        bottomBar = {
            BottomBar(recipe.id,
                userModel.userId.value.toString(),
                onAddToSchedule = { /* Handle Add to Schedule */ },
                onShare = { /* Handle Share */ })

        },
        contentWindowInsets = WindowInsets.navigationBars

    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            item {
                Image(
                    painter = rememberAsyncImagePainter(recipe.imageUrl),
                    contentDescription = recipe.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoCard(title = "${recipe.totalCalories}kCal", subtitle = "Calories")
                    InfoCard(title = recipe.cookTime, subtitle = "Total Time")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Ingredients", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("${recipe.ingredients.size} items", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))

                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Item",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Amount",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }


            items(recipe.ingredients) { ingredient ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp, horizontal = 4.dp), // Tighter spacing
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("•", fontSize = 18.sp, color = Orange)
                        Spacer(modifier = Modifier.width(6.dp)) // Slightly closer bullet
                        Text(
                            text = ingredient.name.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), // BOLD!
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = ingredient.amount,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))

                Text("Instructions", fontSize = 20.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(8.dp))
            }

            items(recipe.instructions.withIndex().toList()) { (index, instruction) ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(vertical = 12.dp, horizontal = 16.dp)
                ) {
                    Column {
                        Text(
                            text = "Step ${index + 1}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = instruction,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 15.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                NutritionDonutChart(
                    totalCalories = recipe.totalCalories,
                    protein = recipe.totalProtein,
                    fat = recipe.totalFat,
                    saturatedFat = recipe.totalSaturatedFat,
                    carbs = recipe.totalCarbs,
                    fiber = recipe.totalFiber,
                    sugar = recipe.totalSugar,
                    sodium = recipe.totalSodium,
                    potassium = recipe.totalPotassium,
                    cholesterol = recipe.totalCholesterol
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun InfoCard(title: String, subtitle: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(subtitle, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
    }
}

@Composable
fun AddToScheduleDialog(
    recipeId: String,
    userId: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    val date = remember { mutableStateOf("") }
    val mealType = remember { mutableStateOf("Breakfast") }
    val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snack")
    val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
    val context = LocalContext.current

    val datePickerDialog = remember {
        DatePickerDialog(context, { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            date.value = "$dayOfMonth/${month + 1}/$year"
        }, currentYear, currentMonth, currentDay).apply {
            setOnShowListener {
                this.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Orange.toArgb())
                this.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Orange.toArgb())
            }
        }
    }

    // 控制DropdownMenu展开状态的mutableState
    val expanded = remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        if (date.value.isEmpty()) {
                            Toast.makeText(context, "Please select a date", Toast.LENGTH_SHORT).show()
                        } else {
                            onConfirm(date.value, mealType.value)
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Orange,
                        contentColor = Color.White
                    )
                ) {
                    Text("Add to Schedule")
                }
            }
        },

        title = { Text(text = "Add to Schedule") },
        text = {
            Column {
                Text(text = "Select Date")
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { datePickerDialog.show() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Orange,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = if (date.value.isEmpty()) "Pick a Date" else date.value)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Select Meal Type")
                Button(
                    onClick = { expanded.value = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Orange,
                        contentColor = Color.White
                    )
                ) {
                    Text(mealType.value)
                }

                DropdownMenu(
                    expanded = expanded.value,
                    onDismissRequest = { expanded.value = false }
                ) {
                    mealTypes.forEach { type ->
                        DropdownMenuItem(onClick = {
                            mealType.value = type
                            expanded.value = false
                        }) {
                            Text(text = type)
                        }
                    }
                }
            }
        }
    )
}


fun addScheduleToFirestore(
    context: Context,
    userId: String,
    recipeId: String,
    selectedDate: String,
    mealType: String
) {
    val db = FirebaseFirestore.getInstance()
    val scheduleData = hashMapOf(
        "userId" to userId,
        "recipeId" to recipeId,
        "date" to selectedDate,
        "mealType" to mealType
    )

    db.collection("schedule")
        .add(scheduleData)
        .addOnSuccessListener { documentReference ->
            Log.d("Firestore", "Schedule added with ID: ${documentReference.id}")
            Toast.makeText(context, "Add successfully", Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener { e ->
            Log.w("Firestore", "Error adding schedule", e)
            Toast.makeText(context, "Failed to add", Toast.LENGTH_SHORT).show()
        }
}

@Composable
fun BottomBar(recipeId: String,
              userId: String,
              onAddToSchedule: () -> Unit,
              onShare: () -> Unit) {
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val showDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current
    if (showDialog.value) {
        AddToScheduleDialog(
            recipeId = recipeId,
            userId = userId,
            onDismiss = { showDialog.value = false },
            onConfirm = { selectedDate, mealType ->
                addScheduleToFirestore(context, userId, recipeId, selectedDate, mealType)
            }
        )
    }


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = bottomPadding + 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = { showDialog.value = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = Orange, // Set the orange color here
                contentColor = Color.White // Set the text color to white
            ),
            modifier = Modifier.weight(1f),

        ) {
            Text(text = "Add to Schedule")
        }



    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionDonutChart(
    totalCalories: Int,
    protein: Int,
    fat: Int,
    saturatedFat: Int,
    carbs: Int,
    fiber: Int,
    sugar: Int,
    sodium: Int,
    potassium: Int,
    cholesterol: Int
) {
    // Original nutrient values (for display in the details list)
    val nutrients = listOf(
        protein, fat, saturatedFat, carbs, fiber, sugar, sodium, potassium, cholesterol
    )
    val labels = listOf(
        "Protein", "Fat", "Saturated Fat", "Carbs", "Fiber", "Sugar",
        "Sodium", "Potassium", "Cholesterol"
    )
    val colors = listOf(
        Color(0xFF4285F4), // Protein - Blue
        Color(0xFFEA4335), // Fat - Red
        Color(0xFFB00020), // Saturated Fat - Dark Red
        Color(0xFFFBBC05), // Carbs - Yellow
        Color(0xFFAA46BB), // Fiber - Purple
        Color(0xFF00ACC1), // Sugar - Cyan
        Color(0xFFFF7043), // Sodium - Orange
        Color(0xFF34A853), // Potassium - Green
        Color(0xFF7E57C2)  // Cholesterol - Indigo
    )

    // Normalize nutrient values: macros are already in grams;
    // For Sodium, Potassium, Cholesterol convert mg to g.
    val nutrientValuesInGrams = listOf(
        protein.toFloat(),
        fat.toFloat(),
        saturatedFat.toFloat(),
        carbs.toFloat(),
        fiber.toFloat(),
        sugar.toFloat(),
        sodium / 1000f,
        potassium / 1000f,
        cholesterol / 1000f
    )
    // Total of all normalized nutrients in grams.
    val totalNutrients = nutrientValuesInGrams.sum().takeIf { it > 0 } ?: 1f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Nutritional Breakdown",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(15.dp))

        // Donut Chart Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp), // Adjusted height for a smaller circle
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(160.dp)) { // Smaller diameter
                var startAngle = -90f
                val strokeWidth = 32f
                val radius = size.minDimension / 2
                // Effective radius to draw the arcs.
                val arcRadius = radius - strokeWidth / 2

                nutrientValuesInGrams.forEachIndexed { index, valueInGrams ->
                    val sweepAngle = (valueInGrams / totalNutrients) * 360f

                    drawArc(
                        color = colors[index],
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                        size = Size(size.width, size.height)
                    )

                    startAngle += sweepAngle
                }
            }
            // Center Total Calories display
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Total", fontSize = 14.sp, color = Color.Gray)
                Text("$totalCalories kcal", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Nutrient Details List
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, start = 8.dp, end = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            nutrients.forEachIndexed { index, originalValue ->
                val label = labels[index]
                val isMg = label in listOf("Sodium", "Potassium", "Cholesterol")
                val unit = if (isMg) "mg" else "g"
                val valueText = "$originalValue $unit"
                // Calculate percentage based on normalized values.
                val percentage = (nutrientValuesInGrams[index] * 100f / totalNutrients).toInt()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(vertical = 8.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(colors[index], shape = CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = label,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(valueText, fontSize = 14.sp)
                        Text("$percentage%", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}




