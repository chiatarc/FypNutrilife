package com.example.assignme.GUI.DailyTracker

import android.content.ContentValues.TAG
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.assignme.AndroidBar.AppBottomNavigation
import com.example.assignme.AndroidBar.AppTopBar
import com.example.assignme.ViewModel.TrackerViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DailyAnalysis(
    navController: NavController,
    trackerViewModel: TrackerViewModel
) {
    val allEntries by trackerViewModel.allEntries.observeAsState(emptyList())
    var searchDate by remember { mutableStateOf("") }
    var filteredEntries by remember { mutableStateOf(allEntries) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    // Update filteredEntries when the currentMonth changes
    fun updateFilteredEntries() {
        filteredEntries = allEntries.filter { YearMonth.from(it.date) == currentMonth }
    }

    // Observe allEntries to initialize filteredEntries
    LaunchedEffect(allEntries) {
        updateFilteredEntries()
    }

    Scaffold(
        topBar = { AppTopBar(title = "Daily Analysis", navController = navController) },
        bottomBar = { AppBottomNavigation(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                OutlinedTextField(
                    value = searchDate,
                    onValueChange = { searchDate = it },
                    label = { Text("Enter date (YYYY-MM-DD)") },
                    modifier = Modifier
                        .weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        Log.d(TAG, "Search button clicked with date: $searchDate")
                        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        if (searchDate.isNotEmpty()) {
                            try {
                                val date = LocalDate.parse(searchDate, dateFormatter)
                                filteredEntries = allEntries.filter { it.date == date }
                            } catch (e: DateTimeParseException) {
                                Log.e(TAG, "Date parsing failed: ${e.message}")
                                filteredEntries = emptyList() // Return empty if parsing fails
                            }
                        } else {
                            // Reset to show all entries for the current month if search date is empty
                            updateFilteredEntries() // This updates filteredEntries directly
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE23E3E))
                ) {
                    Text("Search", color = Color.White)
                }
            }
                Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE23E3E))
                    .padding(5.dp)
                    .clip(MaterialTheme.shapes.medium)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        currentMonth = currentMonth.minusMonths(1)
                        Log.d(TAG, "Navigated to previous month: $currentMonth")
                        updateFilteredEntries() // Update filtered entries for the previous month
                    }) {
                        Text("<", color = Color.White)
                    }
                    Text(
                        "${currentMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${currentMonth.year}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    IconButton(onClick = {
                        currentMonth = currentMonth.plusMonths(1)
                        Log.d(TAG, "Navigated to next month: $currentMonth")
                        updateFilteredEntries() // Update filtered entries for the next month
                    }) {
                        Text(">", color = Color.White)
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(), // Wrap content based on the table height
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                val tableSizeFactor = 0.6f

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Table header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.LightGray)
                            .border((1 * tableSizeFactor).dp, Color.Gray)
                    ) {
                        listOf("Date", "Weight (kg)", "Water (glass)", "Calories (kcal)").forEach { header ->
                            Box(modifier = Modifier.weight(1f).border((1 * tableSizeFactor).dp, Color.Gray)) {
                                Text(
                                    header,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = (16 * tableSizeFactor).sp,
                                        color = Color.Black // Set the text color to black
                                    ),
                                    modifier = Modifier.padding((8 * tableSizeFactor).dp)
                                )
                            }
                        }
                    }

                    // Table rows (for each entry)
                    if (filteredEntries.isNotEmpty()) {
                        filteredEntries.take(10).forEach { entry ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border((1 * tableSizeFactor).dp, Color.Gray)
                            ) {
                                // Table data for each entry
                                listOf(
                                    entry.date.toString(),
                                    entry.weight.toString(),
                                    entry.waterIntake.toString(),
                                    entry.caloriesIntake.toString()
                                ).forEach { value ->
                                    Box(modifier = Modifier.weight(1f).border((1 * tableSizeFactor).dp, Color.Gray)) {
                                        Text(
                                            value,
                                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = (14 * tableSizeFactor).sp),
                                            modifier = Modifier.padding((8 * tableSizeFactor).dp)
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // No entries case
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding((8 * tableSizeFactor).dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No entries recorded yet for this period.",
                                style = MaterialTheme.typography.bodyLarge.copy(fontSize = (14 * tableSizeFactor).sp),
                                color = Color.Red
                            )
                        }
                        Log.d(TAG, "No entries found for the current month: $currentMonth")
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Card for Water Intake Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Ring to display water intake inside the card
                        WaterIntakeRing(trackerViewModel = trackerViewModel)
                    }
                }

                // Card for Calories Intake Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Ring to display calorie intake inside the card
                        CaloriesIntakeRing(trackerViewModel = trackerViewModel)
                    }
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WaterIntakeRing(
    trackerViewModel: TrackerViewModel = viewModel(),
    textSize: TextUnit = 22.sp // Add a parameter for adjustable text size
) {
    val currentDate = LocalDate.now()

    LaunchedEffect(currentDate) {
        trackerViewModel.fetchWaterIntakeForDate(currentDate)
    }

    val waterIntake by trackerViewModel.currentWaterIntake.observeAsState(0)
    val goal = 2750
    val intakeInMl = waterIntake * 100
    val percentage = (intakeInMl / goal.toFloat()).coerceIn(0f, 1f)

    val ringThickness = 20.dp
    val ringSize = 120.dp

    Log.d("WaterIntakeRing", "Date: $currentDate, Water Intake: $waterIntake glasses, Intake in ml: $intakeInMl ml, Goal: $goal ml, Percentage: ${(percentage * 100).toInt()}%")

    // Row to hold the ring on the left and text progress on the right
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), // Padding for the whole row
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side: Water intake ring
        Box(
            modifier = Modifier
                .size(ringSize)
                .padding(8.dp), // Padding around the ring
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val sweepAngle = 360f * percentage

                // Full circle (Background Arc - White)
                drawArc(
                    color = Color.White,
                    startAngle = -90f,
                    sweepAngle = 360f, // Full circle
                    useCenter = false,
                    style = Stroke(width = ringThickness.toPx(), cap = StrokeCap.Round)
                )

                // Foreground Arc (Percentage - Light Blue)
                drawArc(
                    color = Color(0xFF81D4FA),
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = ringThickness.toPx(), cap = StrokeCap.Round)
                )
            }

            // Percentage in the center of the ring
            Text(
                text = "${(percentage * 100).toInt()}%",
                style = androidx.compose.ui.text.TextStyle(fontSize = textSize, fontWeight = FontWeight.Bold),
                color = Color(0xFF81D4FA)
            )
        }

        Spacer(modifier = Modifier.width(16.dp)) // Space between the ring and the text

        // Right side: Progress information
        Column(
            horizontalAlignment = Alignment.Start, // Align text to the start
            modifier = Modifier.padding(start = 8.dp)
        ) {
            // Title: Water Goal
            Text(
                text = "Water Goal",
                style = TextStyle(fontSize = textSize * 0.8f, fontWeight = FontWeight.Bold), // Adjust size as needed
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Drinking progress
            Text(
                text = "${intakeInMl}ml / ${goal}ml",
                style = TextStyle(fontSize = textSize * 0.7f), // Adjust size as needed
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Achieved or Not Achieved based on percentage
            if (percentage >= 1f) {
                Text(
                    text = "Achieved",
                    style = TextStyle(fontSize = textSize * 0.7f, fontWeight = FontWeight.Bold, color = Color.Green), // Adjust size as needed
                    modifier = Modifier.padding(top = 8.dp)
                )
            } else {
                Text(
                    text = "Not Achieved",
                    style = TextStyle(fontSize = textSize * 0.7f, fontWeight = FontWeight.Bold, color = Color.Red), // Adjust size as needed
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CaloriesIntakeRing(
    trackerViewModel: TrackerViewModel = viewModel(),
    textSize: TextUnit = 22.sp // Adjustable text size
) {
    val currentDate = LocalDate.now()

    // Fetch the calories intake for the current date
    LaunchedEffect(currentDate) {
        trackerViewModel.addCalories(currentDate, 0f) // Ensures calories data is initialized for today
    }

    // Observe the calorie intake and goal from the ViewModel
    val caloriesIntake by trackerViewModel.calorieHistory.observeAsState(emptyList())
    val currentCalories = caloriesIntake.firstOrNull { it.date == currentDate }?.caloriesIntake ?: 0f
    val calorieGoal by trackerViewModel.calorieGoal.observeAsState(null)
    var newGoalText by remember { mutableStateOf("") }
    var isEditingGoal by remember { mutableStateOf(false) } // Track if user is editing the goal

    val percentage = (currentCalories / (calorieGoal?.toFloat() ?: 1f)).coerceIn(0f, 1f)
    val ringThickness = 20.dp
    val ringSize = 120.dp

    // Only show the calorie goal input field and button if there is no goal set
    if (calorieGoal == null) {
        // Prompt the user to set a calorie goal
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Set Your Calorie Goal",
                style = TextStyle(fontSize = textSize, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Input field for setting a new calorie goal
            OutlinedTextField(
                value = newGoalText,
                onValueChange = { newGoalText = it },
                label = { Text("Enter Calorie Goal") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // Button to set the goal
            Button(
                onClick = {
                    newGoalText.toIntOrNull()?.let { newGoal ->
                        trackerViewModel.setCalorieGoal(newGoal) // Save the goal in ViewModel and SharedPreferences
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE23E3E))
            ) {
                Text(text = "Set Goal")
            }
        }
    } else {
        // Show the calorie ring and progress when the goal is set
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Ring display
            Box(
                modifier = Modifier
                    .size(ringSize)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val sweepAngle = 360f * percentage

                    // Background Arc - White
                    drawArc(
                        color = Color.White,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = ringThickness.toPx(), cap = StrokeCap.Round)
                    )

                    // Foreground Arc - Yellow Orange
                    drawArc(
                        color = Color(0xFFFFA500), // Yellow orange color
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = ringThickness.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Display percentage in the center with the same color as the arc
                Text(
                    text = "${(percentage * 100).toInt()}%",
                    style = androidx.compose.ui.text.TextStyle(fontSize = textSize, fontWeight = FontWeight.Bold),
                    color = Color(0xFFFFA500) // Match arc color
                )
            }

            Spacer(modifier = Modifier.width(16.dp)) // Space between ring and text content

            // Right: Title, progress, achievement status
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                // Title: Calories Goal
                Text(
                    text = "Calories Goal",
                    style = TextStyle(fontSize = textSize * 0.8f, fontWeight = FontWeight.Bold),
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Calories progress
                Text(
                    text = "${currentCalories.toInt()} kcal / ${calorieGoal} kcal",
                    style = TextStyle(fontSize = textSize * 0.7f),
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Achieved or Not Achieved
                if (percentage >= 1f) {
                    Text(
                        text = "Achieved",
                        style = TextStyle(fontSize = textSize * 0.7f, fontWeight = FontWeight.Bold, color = Color.Green),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                } else {
                    Text(
                        text = "Not Achieved",
                        style = TextStyle(fontSize = textSize * 0.7f, fontWeight = FontWeight.Bold, color = Color.Red),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // "Edit Goal" button and text box to modify the calorie goal
                if (isEditingGoal) {
                    OutlinedTextField(
                        value = newGoalText,
                        onValueChange = { newGoalText = it },
                        label = { Text("Edit Calorie Goal") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )

                    Button(
                        onClick = {
                            newGoalText.toIntOrNull()?.let { newGoal ->
                                trackerViewModel.setCalorieGoal(newGoal) // Update the goal and save to preferences
                                isEditingGoal = false // Stop editing after saving
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE23E3E))
                    ) {
                        Text(text = "Save Goal")
                    }
                } else {
                    Button(
                        onClick = {
                            isEditingGoal = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE23E3E))
                    ) {
                        Text(text = "Edit Goal")
                    }
                }
            }
        }
    }
}






