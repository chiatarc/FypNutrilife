package com.example.assignme.GUI.DailyTracker

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.assignme.AndroidBar.AppBottomNavigation
import com.example.assignme.AndroidBar.AppTopBar
import com.example.assignme.ViewModel.TrackerViewModel
import com.example.assignme.ViewModel.UserViewModel
import java.time.YearMonth

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LineChart(
    navController: NavController,
    trackerViewModel: TrackerViewModel = viewModel()
) {
    // Fetch weight history here or pass it from the previous screen if needed
    val weightHistory by trackerViewModel.weightHistory.observeAsState(emptyList())

    // Get the current YearMonth
    val currentMonth = YearMonth.now()

    // Filter weight history to only include records from the current month
    val currentMonthWeightHistory = weightHistory.filter {
        YearMonth.from(it.date) == currentMonth
    }

    Scaffold(
        topBar = { AppTopBar(title = "Weight Chart", navController = navController) },
        bottomBar = { AppBottomNavigation(navController = navController) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background) // Set background color
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp), // Add padding around the column
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = "Your Weight Over Dates",
                    color = Color.Black,
                    fontSize = 20.sp, // Set title font size
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp) // Spacing below the title
                )

                // Card for the line chart
                Card(
                    backgroundColor = Color(0xFFBBBABA), // Use the specified color
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp) // Set height for the card
                        .padding(bottom = 16.dp) // Space below the card
                ) {
                    // Full line chart display
                    WeightChart(currentMonthWeightHistory, lineColor = Color.Blue)
                }

                // Additional descriptive text below the chart
                Text(
                    text = "This chart shows your weight trends for the month.",
                    color = Color.Gray,
                    fontSize = 14.sp, // Set description font size
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

