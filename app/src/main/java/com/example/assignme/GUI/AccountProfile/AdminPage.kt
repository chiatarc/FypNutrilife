package com.example.assignme.GUI.AccountProfile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.example.assignme.R
import com.example.assignme.ViewModel.AdminProfile
import com.example.assignme.ViewModel.MockThemeViewModel
import com.example.assignme.ViewModel.MockUserViewModel
import com.example.assignme.ViewModel.ThemeViewModel
import com.example.assignme.ViewModel.UserProfileProvider
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(
    userViewModel: UserProfileProvider,
    navController: NavController,
    themeViewModel: ThemeViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Admin Dashboard") }, // Add a title
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showDialog = true }) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.primary
                        )

                        // Theme selection dialog
                        MoreSettings(
                            isVisible = showDialog,
                            onDismiss = { showDialog = false },
                            navController = navController,
                            onThemeSelected = { selectedTheme ->
                                themeViewModel.isDarkTheme.value = (selectedTheme == "Dark")
                                themeViewModel.toggleTheme()
                                showDialog = false
                            }
                        )
                    }
                }
            )

        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding()
                .statusBarsPadding()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                ProfileSection(navController, userViewModel)
            }
            item {
                AdminActions(navController)
            }
            item {
                // You can add more items here as needed
            }
        }
    }
}

@Composable
fun ProfileSection(navController: NavController, userViewModel: UserProfileProvider) {
    val userId by userViewModel.userId.observeAsState()
    val adminProfile by userViewModel.adminProfile.observeAsState(initial = AdminProfile())

    // Fetch profile data if not available
    userId?.let { userId ->
        if (adminProfile.name == null) {
            userViewModel.fetchAdminProfile(userId)
        }
    }

    // Display centered profile picture and name
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Clickable profile picture
        Image(
            painter = if (!adminProfile.profilePictureUrl.isNullOrBlank()) {
                rememberImagePainter(adminProfile.profilePictureUrl)
            } else {
                painterResource(id = R.drawable.google) // Default image
            },
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .clickable { navController.navigate("edit_admin") }, // Navigate to edit profile
            contentScale = ContentScale.Crop
        )

        // Display the name if available
        Text(
            text = "Welcome ${adminProfile.name.orEmpty()}!",
            fontSize = 18.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }

    Spacer(modifier = Modifier.height(16.dp)) // Space below the profile section

    // Divider section
    HorizontalDivider()
}

@Composable
fun AdminActions(navController: NavController) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ActionButton(text = "Manage Post", onClick = { navController.navigate("manage_post") })
        ActionButton(text = "Manage Recipe", onClick = { navController.navigate("manage_recipe_admin") })
        ActionButton(text = "Manage User", onClick = { navController.navigate("manage_admin_page") })
        ActionButton(text = "Sign out", onClick = {
            FirebaseAuth.getInstance().signOut()
            navController.navigate("main_page")})
    }
}

@Composable
fun ActionButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = text, style = MaterialTheme.typography.titleMedium)
        }
    }

    // Divider section
    HorizontalDivider()
}

@Composable
fun HorizontalDivider() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Divider(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 2.dp),
            thickness = 1.dp,
            color = Color.Gray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAdminDashboard() {

    val mockThemeViewModel = MockThemeViewModel()
    AdminDashboard(
        navController = rememberNavController(),
        userViewModel = MockUserViewModel(),
        themeViewModel = mockThemeViewModel
    )
}