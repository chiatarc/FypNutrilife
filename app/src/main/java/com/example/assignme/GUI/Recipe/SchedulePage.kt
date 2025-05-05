package com.example.assignme.GUI.Recipe

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.assignme.AndroidBar.AppBottomNavigation
import com.example.assignme.AndroidBar.AppTopBar
import com.example.assignme.GUI.Recipe.ui.theme.Orange
import com.example.assignme.ViewModel.RecipeViewModel
import com.example.assignme.ViewModel.UserViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SchedulePage(navController: NavController, viewModel: RecipeViewModel, userModel: UserViewModel, onBackClick: () -> Unit) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    Scaffold(
        topBar = { AppTopBar(title = "Recipe Schedule", navController = navController) },
        bottomBar = { AppBottomNavigation(navController = navController) }

    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            CalendarSection(selectedDate = selectedDate, onDateSelected = { newDate ->
                selectedDate = newDate
            })
            MealTypesSection(
                selectedDate = selectedDate,
                userModel = userModel,
                navController = navController
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarSection(selectedDate: LocalDate, onDateSelected: (LocalDate) -> Unit) {
    var currentMonth by remember { mutableStateOf(YearMonth.from(selectedDate)) }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        // Month and Year selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                currentMonth = currentMonth.minusMonths(1)
            }) {
                Text("<", style = MaterialTheme.typography.headlineSmall)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault()),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = currentMonth.year.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
            IconButton(onClick = {
                currentMonth = currentMonth.plusMonths(1)
            }) {
                Text(">", style = MaterialTheme.typography.headlineSmall)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Days of week headers
            items(7) { index ->
                val dayName = LocalDate.now().with(java.time.DayOfWeek.of((index + 1) % 7 + 1))
                    .dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                Text(
                    text = dayName,
                    modifier = Modifier.padding(4.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            val firstDayOfWeek = currentMonth.atDay(1).dayOfWeek.value % 7
            val daysInMonth = currentMonth.lengthOfMonth()

            // Empty spaces for days before the 1st of the month
            items(firstDayOfWeek) {
                Box(modifier = Modifier.size(40.dp))
            }

            // Days of the month
            items(daysInMonth) { day ->
                val date = currentMonth.atDay(day + 1)
                val isSelected = date == selectedDate

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = if (isSelected) Orange else Color.Transparent,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable {
                            onDateSelected(date) // 设置选中的日期
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (day + 1).toString(),
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MealTypesSection(selectedDate: LocalDate, userModel: UserViewModel, navController: NavController) {
    val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snacks")

    LazyColumn(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(mealTypes.size) { index ->
            MealTypeItem(
                mealType = mealTypes[index],
                selectedDate = selectedDate,
                userModel = userModel,
                navController = navController
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MealTypeItem(
    mealType: String,
    selectedDate: LocalDate,
    userModel: UserViewModel,
    navController: NavController
) {
    var isExpanded by remember { mutableStateOf(false) }
    var mealsForDate by remember { mutableStateOf<List<String>>(emptyList()) }
    val displayedMeals = remember { mutableStateListOf<String>() }

    val db = FirebaseFirestore.getInstance()
    val dateFormatter = DateTimeFormatter.ofPattern("d/M/yyyy")
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(selectedDate) {
        if (isExpanded) {
            val formattedDate = selectedDate.format(dateFormatter)
            val userId = userModel.userId.value.orEmpty()
            mealsForDate = fetchMealsForDate(db, userId, formattedDate, mealType)
            displayedMeals.clear()
            displayedMeals.addAll(mealsForDate)
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = mealType, style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = {
                    isExpanded = !isExpanded
                    if (isExpanded) {
                        coroutineScope.launch {
                            val formattedDate = selectedDate.format(dateFormatter)
                            val userId = userModel.userId.value.orEmpty()
                            mealsForDate = fetchMealsForDate(db, userId, formattedDate, mealType)
                            displayedMeals.clear()
                            displayedMeals.addAll(mealsForDate)
                        }
                    }
                }) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand"
                    )
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                if (displayedMeals.isEmpty()) {
                    Text("No meals planned")
                } else {
                    displayedMeals.forEach { recipeId ->
                        val dismissState = rememberDismissState(
                            confirmStateChange = { dismissValue ->
                                if (dismissValue == DismissValue.DismissedToEnd) {
                                    // Remove from UI
                                    displayedMeals.remove(recipeId)
                                    // Remove from Firestore
                                    coroutineScope.launch {
                                        val formattedDate = selectedDate.format(dateFormatter)
                                        val userId = userModel.userId.value.orEmpty()
                                        removeScheduledRecipeFromFirestore(db, userId, formattedDate, mealType, recipeId)
                                    }
                                }
                                false
                            }
                        )

                        SwipeToDismiss(
                            state = dismissState,
                            directions = setOf(DismissDirection.StartToEnd),
                            background = {}, // 🔥 clean background, no icon
                            dismissContent = {
                                RecipeCard(recipeId = recipeId, navController = navController)
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}


suspend fun fetchMealsForDate(
    db: FirebaseFirestore,
    userId: String,
    date: String,
    mealType: String
): List<String> {
    return try {
        val snapshot = db.collection("schedule")
            .whereEqualTo("userId", userId)
            .whereEqualTo("date", date)
            .whereEqualTo("mealType", mealType)
            .get()
            .await()

        snapshot.documents.map { document ->
            val recipeId = document.getString("recipeId")
            recipeId ?: "Unknown Recipe"
        }
    } catch (e: Exception) {
        emptyList()
    }
}

@Composable
fun RecipeCard(recipeId: String, navController: NavController) {
    var recipeName by remember { mutableStateOf("") }
    val db = FirebaseFirestore.getInstance()

    // Fetch recipe name based on the recipeId from Firestore
    LaunchedEffect(recipeId) {
        val recipeDocument = db.collection("recipes").document(recipeId).get().await()
        recipeName = recipeDocument.getString("title") ?: "Unknown Recipe"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                // Navigate to RecipeScreen with the recipeId
                navController.navigate("recipe_detail_page/$recipeId")
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Orange // 橙色
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = recipeName,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Click to view recipe, swipe left to remove",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White
            )
        }
    }
}

suspend fun removeScheduledRecipeFromFirestore(
    db: FirebaseFirestore,
    userId: String,
    date: String,
    mealType: String,
    recipeId: String
) {
    try {
        val snapshot = db.collection("schedule")
            .whereEqualTo("userId", userId)
            .whereEqualTo("date", date)
            .whereEqualTo("mealType", mealType)
            .whereEqualTo("recipeId", recipeId)
            .get()
            .await()

        for (doc in snapshot.documents) {
            db.collection("schedule").document(doc.id).delete()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
