package com.example.assignme.GUI.DailyTracker

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.assignme.AndroidBar.AppBottomNavigation
import com.example.assignme.AndroidBar.AppTopBar
import com.example.assignme.ViewModel.TrackerData
import com.example.assignme.ViewModel.TrackerViewModel
import com.example.assignme.ViewModel.UserViewModel

@SuppressLint("DefaultLocale")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Transformation(
    navController: NavController,
    userViewModel: UserViewModel,
    trackerViewModel: TrackerViewModel
) {
    var currentPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var trackerData by remember { mutableStateOf<TrackerData?>(null) }
    val splitRatio by remember { mutableStateOf(0.5f) }  // Adjustable split ratio (0.5 for center)
    var containerWidth by remember { mutableStateOf(0) }  // To store the container width

    // Observe the userId from the ViewModel
    val userId by userViewModel.userId.observeAsState()

    // Fetch tracker data when the composable is first composed and when userId changes
    LaunchedEffect(userId) {
        userId?.let {
            userViewModel.fetchTrackerData(it) { data ->
                trackerData = data // Update tracker data
            }
        }
    }

    // Declare the image picker launcher inside the composable
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                currentPhotoUri = uri
                Log.d("Transformation", "Image picked: $uri")
            } else {
                Log.d("Transformation", "No image was picked.")
            }
        }
    )

    LaunchedEffect(Unit) {
        trackerViewModel.getCurrentWeight()
    }
    // Observe current weight from TrackerViewModel
    val currentWeight by trackerViewModel.currentWeight.observeAsState(0f) // Default to 0f

    Scaffold(
        topBar = { AppTopBar(title = "Transformations", navController = navController) },
        bottomBar = { AppBottomNavigation(navController = navController) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 8.dp), // Reduce horizontal padding for less gap
            contentAlignment = Alignment.Center
        ) {
            if (trackerData != null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (currentPhotoUri == null) {
                        Text(
                            text = "Upload your LOOK!",
                            fontSize = 24.sp, // Adjust the size as needed
                            fontWeight = FontWeight.Bold, // Optional: Make the text bold
                            modifier = Modifier.padding(16.dp) // Optional: Add padding for better spacing
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                imagePickerLauncher.launch("image/*")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE23E3E)) // Set the button background color
                        ) {
                            Text("Upload Photo")
                        }
                        Spacer(modifier = Modifier.height(7.dp))
                    }
                    if (currentPhotoUri != null) {
                        // Image section with slider for adjustment
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.8f),  // Use 80% of the height for the images
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.9f) // Take 90% of available height for the image section
                                    .onGloballyPositioned { layoutCoordinates ->
                                        // Capture the width of the container
                                        containerWidth = layoutCoordinates.size.width
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                // Before Image (left part)
                                Image(
                                    painter = rememberAsyncImagePainter(trackerData?.bodyImageUri),
                                    contentDescription = "Before Image",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight()
                                        .align(Alignment.Center)
                                        .graphicsLayer {
                                            clip = true
                                            shape = androidx.compose.foundation.shape.GenericShape { size, _ ->
                                                moveTo(0f, 0f)
                                                lineTo(size.width * splitRatio, 0f)
                                                lineTo(size.width * splitRatio, size.height)
                                                lineTo(0f, size.height)
                                                close()
                                            }
                                        }
                                )

                                // After Image (right part)
                                Image(
                                    painter = rememberAsyncImagePainter(currentPhotoUri),
                                    contentDescription = "After Image",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight()
                                        .align(Alignment.Center)
                                        .graphicsLayer {
                                            clip = true
                                            shape = androidx.compose.foundation.shape.GenericShape { size, _ ->
                                                moveTo(size.width * splitRatio, 0f)
                                                lineTo(size.width, 0f)
                                                lineTo(size.width, size.height)
                                                lineTo(size.width * splitRatio, size.height)
                                                close()
                                            }
                                        }
                                )

                                // Red Line in the center (adjustable)
                                Box(
                                    modifier = Modifier
                                        .width(4.dp)  // Thicker red line for better visual separation
                                        .fillMaxHeight()
                                        .background(Color.Red)
                                        .align(Alignment.Center)
                                        .offset(x = ((containerWidth * splitRatio).toInt().dp))  // Adjust line position based on container width
                                )
                            }
                        }

                        // Labels and weights for Before and After images
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Before",
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    fontFamily = FontFamily.Serif
                                )
                                Text(
                                    text = "After",
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    fontFamily = FontFamily.Serif
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp)) // Add space between labels and weights

                            // Before and After Weights
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Weight: ${trackerData?.weight} kg",  // Fetch before weight from trackerData
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    fontFamily = FontFamily.Serif
                                )
                                Text(
                                    text = "Weight: ${currentWeight?.let { String.format("%.1f", it) } ?: "No data"} kg",
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    fontFamily = FontFamily.Serif
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "Upload a full body image",
                            fontSize = 15.sp, // Adjust the font size as needed
                            color = Color(0xFF9E9C97) // Set the text color
                        )
                    }
                }
            } else {
                Text("Loading data...")
            }
        }
    }
}






















