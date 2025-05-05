package com.example.assignme.GUI.DailyTracker

import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.assignme.AndroidBar.AppBottomNavigation
import com.example.assignme.AndroidBar.AppTopBar
import com.example.assignme.ViewModel.HealthData
import com.example.assignme.ViewModel.UserViewModel

@Composable
fun HealthDataSummaryPage(
    navController: NavController,
    userViewModel: UserViewModel
) {
    val userId by userViewModel.userId.observeAsState()
    val healthData = remember { mutableStateOf<HealthData?>(null) }
    val context = LocalContext.current

    // Fetch health data when the Composable loads
    LaunchedEffect(userId) {
        userId?.let { id ->
            userViewModel.fetchHealthData { data ->
                healthData.value = data

                if (data == null) {
                    Toast.makeText(context, "No health data found, please enter your data.", Toast.LENGTH_SHORT).show()
                    navController.navigate("health_data_page") {
                        popUpTo("health_summary_page") { inclusive = true }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = { AppTopBar(title = "Health Summary", navController = navController) },
        bottomBar = { AppBottomNavigation(navController = navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("health_data_page") },
                content = { Icon(Icons.Default.Edit, contentDescription = "Edit Data") },
                modifier = Modifier.padding(16.dp)
            )
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            healthData.value?.let { data ->
                val metrics = listOf(
                    "Systolic BP" to data.systolicBP.toIntOrNull(),
                    "Diastolic BP" to data.diastolicBP.toIntOrNull(),
                    "Glucose" to data.glucose.toIntOrNull(),
                    "Triglycerides" to data.triglycerides.toIntOrNull(),
                    "HDL Cholesterol" to data.hdlCholesterol.toIntOrNull(),
                    "Waist Size" to data.waistCircumference.toIntOrNull()
                ).filter { it.second != null } // Remove null values

                val overallStatus = calculateOverallStatus(metrics)

                StatusBar(overallStatus)

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(metrics.chunked(2)) { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowItems.forEach { (label, value) ->
                                HealthMetricCard(label, value ?: 0, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(150.dp)) // Add padding at the bottom of the last card
                    }
                }
            } ?: Text("Fetching health data...", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

enum class OverallStatus {
    VeryHealthy,
    Healthy,
    Manageable,
    Dangerous,
    VeryDangerous
}

@Composable
fun StatusBar(overallStatus: OverallStatus) {
    val color = when (overallStatus) {
        OverallStatus.VeryHealthy -> Color.Green
        OverallStatus.Healthy -> Color(0xFF388E3C)
        OverallStatus.Manageable -> Color.Yellow
        OverallStatus.Dangerous -> Color.Red
        OverallStatus.VeryDangerous -> Color(0xFFA30404)
    }

    val statusText = when (overallStatus) {
        OverallStatus.VeryHealthy -> "Very Healthy"
        OverallStatus.Healthy -> "Healthy"
        OverallStatus.Manageable -> "Manageable"
        OverallStatus.Dangerous -> "Dangerous"
        OverallStatus.VeryDangerous -> "Very Dangerous"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(color, shape = RoundedCornerShape(12.dp))
            .padding(vertical = 16.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = statusText,
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
fun HealthMetricCard(label: String, value: Int, modifier: Modifier = Modifier) {
    var showDescription by remember { mutableStateOf(false) }

    val (color, status, description) = when (label) {
        "Systolic BP" -> getColorStatusAndDescriptionForSystolicBP(value)
        "Diastolic BP" -> getColorStatusAndDescriptionForDiastolicBP(value)
        "Glucose" -> getColorStatusAndDescriptionForGlucose(value)
        "Triglycerides" -> getColorStatusAndDescriptionForTriglycerides(value)
        "HDL Cholesterol" -> getColorStatusAndDescriptionForHDLCholesterol(value)
        "Waist Size" -> getColorStatusAndDescriptionForWaistSize(value)
        else -> Triple(Color.Gray, "Unknown", "Description not available")
    }

    val padding by animateDpAsState(if (showDescription) 5.dp else 2.dp)

    Card(
        modifier = modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable { showDescription = !showDescription },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(Color.Transparent)
                .then(if (showDescription) Modifier else Modifier.height(180.dp)),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.align(Alignment.Start)
                )

                Text(
                    text = "$value",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.Transparent, shape = CircleShape)
                        .border(8.dp, color, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = status,
                        color = color,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            if (showDescription) {
                Spacer(modifier = Modifier.height(padding))
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Left,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                }
            }
        }
    }
}

@Composable
fun calculateOverallStatus(metrics: List<Pair<String, Int?>>): OverallStatus {
    val statuses = metrics.map { (label, value) ->
        when (label) {
            "Systolic BP" -> getColorStatusAndDescriptionForSystolicBP(value ?: 0).second
            "Diastolic BP" -> getColorStatusAndDescriptionForDiastolicBP(value ?: 0).second
            "Glucose" -> getColorStatusAndDescriptionForGlucose(value ?: 0).second
            "Triglycerides" -> getColorStatusAndDescriptionForTriglycerides(value ?: 0).second
            "HDL Cholesterol" -> getColorStatusAndDescriptionForHDLCholesterol(value ?: 0).second
            "Waist Size" -> getColorStatusAndDescriptionForWaistSize(value ?: 0).second
            else -> "Unknown"
        }
    }

    return when {
        statuses.all { it == "Normal" } -> OverallStatus.VeryHealthy
        statuses.count { it == "High" || it == "Low" } <= 1 -> OverallStatus.Healthy
        statuses.count { it == "High" || it == "Low" } == 2 -> OverallStatus.Manageable
        statuses.count { it == "High" || it == "Low" } == 3 -> OverallStatus.Dangerous
        else -> OverallStatus.VeryDangerous
    }
}

@Composable
fun getColorStatusAndDescriptionForSystolicBP(value: Int): Triple<Color, String, String> {
    return when {
        value < 90 -> Triple(Color.Blue, "Low", "Increase fluid & salt intake (unless advised otherwise). Eat salted nuts, olives, cheese, broth-based soups. Stay hydrated. If dizziness occurs, consult a doctor.")
        value in 90..120 -> Triple(Color(0xFF388E3C), "Normal", "Well done! Keep eating a balanced diet with whole grains, lean proteins, and plenty of water. Stay active!")
        else -> Triple(Color.Red, "High", "Reduce sodium & stress. Eat leafy greens, bananas, beets, garlic, yogurt, and omega-3 rich foods (salmon, flaxseeds). Exercise and manage stress levels.")
    }
}

@Composable
fun getColorStatusAndDescriptionForDiastolicBP(value: Int): Triple<Color, String, String> {
    return when {
        value < 60 -> Triple(Color.Blue, "Low", "Stay hydrated & eat potassium-rich foods. Try coconut water, avocados, spinach, bananas, and lean proteins. Seek medical advice if persistent.")
        value in 60..80 -> Triple(Color(0xFF388E3C), "Normal", "Great job! Keep following a heart-healthy diet with fruits, vegetables, and lean proteins.")
        else -> Triple(Color.Red, "High", "Cut back on salt & unhealthy fats. Eat oats, garlic, dark chocolate (in moderation), and omega3 rich foods. Exercise and manage weight.")
    }
}

@Composable
fun getColorStatusAndDescriptionForGlucose(value: Int): Triple<Color, String, String> {
    return when {
        value < 70 -> Triple(Color.Blue, "Low", "Eat frequent, balanced meals. Include whole grains, nuts, dairy, and protein-rich snacks like peanut butter & Greek yogurt. Avoid long gaps between meals.")
        value in 70..144 -> Triple(Color(0xFF388E3C), "Normal", "Awesome! Keep eating balanced meals with fiber, healthy carbs, and proteins to maintain stable blood sugar.")
        else -> Triple(Color.Red, "High", "Reduce refined sugar & increase fiber. Eat whole grains (quinoa, brown rice), legumes, nuts, green leafy veggies, and cinnamon. Exercise regularly.")
    }
}

@Composable
fun getColorStatusAndDescriptionForTriglycerides(value: Int): Triple<Color, String, String> {
    return when {
        value < 50 -> Triple(Color.Blue, "Low", "Ensure enough healthy fats & calories. Eat avocados, nuts, seeds, fatty fish (salmon, tuna), and olive oil. Monitor for underlying conditions.")
        value in 50..150 -> Triple(Color(0xFF388E3C), "Normal", "Great job! Maintain a diet rich in healthy fats and fiber with regular exercise.")
        else -> Triple(Color.Red, "High", "Limit sugar, alcohol & refined carbs. Eat walnuts, chia seeds, lentils, leafy greens, and fatty fish. Increase fiber intake and physical activity.")
    }
}

@Composable
fun getColorStatusAndDescriptionForHDLCholesterol(value: Int): Triple<Color, String, String> {
    return when {
        value < 45 -> Triple(Color.Blue, "Low", "Boost healthy fats & exercise. Eat olive oil, almonds, walnuts, avocado, flaxseeds, and fatty fish. Engage in regular physical activity.")
        value in 45..59 -> Triple(Color(0xFF388E3C), "Normal", "Perfect! Keep up your heart-healthy habits. Regular exercise and a balanced diet help maintain good HDL levels.")
        else -> Triple(Color.Red, "High", "Reduce excessive healthy fat intake. Eat more lean proteins, whole grains, vegetables, and legumes. Stay active with moderate exercise and stay hydrated.")
    }
}

@Composable
fun getColorStatusAndDescriptionForWaistSize(value: Int): Triple<Color, String, String> {
    return when {
        value < 70 -> Triple(Color.Blue, "Low", "Maintain muscle & weight balance. Eat protein-rich foods (chicken, fish, beans), whole grains, and healthy fats. Avoid excessive calorie restriction.")
        value in 70..101 -> Triple(Color(0xFF388E3C), "Normal", "Well managed! Keep up your current diet and exercise routine to maintain a healthy weight.")
        else -> Triple(Color.Red, "High", "Increase fiber & exercise. Eat vegetables, whole grains, lean proteins, nuts, and seeds. Reduce processed foods and sugary drinks.")
    }
}





