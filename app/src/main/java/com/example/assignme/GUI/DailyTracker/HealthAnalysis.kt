package com.example.assignme.GUI.DailyTracker

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.assignme.AndroidBar.AppBottomNavigation
import com.example.assignme.AndroidBar.AppTopBar
import com.example.assignme.ViewModel.UserViewModel

@Composable
fun HealthDataPage(
    navController: NavController,
    userViewModel: UserViewModel
) {
    Scaffold(
        topBar = { AppTopBar(title = "Health Data", navController = navController) },
        bottomBar = { AppBottomNavigation(navController = navController) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {
                // Title at the top
                Text(
                    text = "Enter Health Data",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                val textFields = listOf(
                    "Systolic Blood Pressure (mmHg)" to "Enter systolic pressure",
                    "Diastolic Blood Pressure (mmHg)" to "Enter diastolic pressure",
                    "Fasting Blood Glucose (mg)" to "Enter blood glucose level",
                    "Triglycerides (mg)" to "Enter triglycerides",
                    "HDL Cholesterol (mg)" to "Enter HDL cholesterol",
                    "Waist Circumference (cm)" to "Enter waist length"
                )

                val textStates = remember { textFields.map { mutableStateOf("") } }

                textFields.forEachIndexed { index, (label, placeholder) ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        OutlinedTextField(
                            value = textStates[index].value,
                            onValueChange = { newValue ->
                                // Ensure only numbers are entered
                                if (newValue.all { it.isDigit() }) {
                                    textStates[index].value = newValue
                                }
                            },
                            placeholder = { Text(placeholder) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Check if all fields are filled
                val allFieldsFilled = textStates.all { it.value.isNotEmpty() }
                val context = LocalContext.current  // Get context inside the Composable

                // Save Data Button
                Button(
                    onClick = {
                        userViewModel.saveHealthData(
                            systolicBP = textStates[0].value,
                            diastolicBP = textStates[1].value,
                            glucose = textStates[2].value,
                            triglycerides = textStates[3].value,
                            hdlCholesterol = textStates[4].value,
                            waistCircumference = textStates[5].value
                        ) { success ->
                            if (success) {
                                Toast.makeText(context, "Health data saved!", Toast.LENGTH_SHORT).show()
                                navController.navigate("health_summary_page") // Navigate after saving
                            } else {
                                Toast.makeText(context, "Failed to save data.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    enabled = allFieldsFilled, // Button is only enabled when all fields are filled
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (allFieldsFilled) Color.Red else Color.Gray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(text = "Save Data", color = Color.White, style = MaterialTheme.typography.bodyLarge)
                }

                // Privacy disclaimer
                Text(
                    text = "Enter your health data to continue. We prioritize your privacy and ensure your information remains secure.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(25.dp)) // Add padding at the bottom
            }
        }
    }
}




