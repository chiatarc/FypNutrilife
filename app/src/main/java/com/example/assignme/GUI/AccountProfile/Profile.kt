package com.example.assignme.GUI.AccountProfile

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.assignme.AndroidBar.AppBottomNavigation
import com.example.assignme.R
import com.example.assignme.DataClass.Recipe
import com.example.assignme.DataClass.Recipes
import com.example.assignme.GUI.Recipe.ui.theme.Orange
import com.example.assignme.ViewModel.MockThemeViewModel
import com.example.assignme.ViewModel.UserProfile
import com.example.assignme.ViewModel.UserProfileProvider
import com.example.assignme.ViewModel.MockUserViewModel
import com.example.assignme.ViewModel.RecipeViewModel
import com.example.assignme.ViewModel.ThemeViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ProfilePage(
    navController: NavController,
    userViewModel: UserProfileProvider,
    themeViewModel: ThemeViewModel,
    recipeViewModel: RecipeViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") } // State for search query

    val savedRecipes by recipeViewModel.savedRecipes.collectAsState()
    val userId by userViewModel.userId.observeAsState()

    // Fetch the saved recipes when the screen is displayed
    LaunchedEffect(userId) {
        userId?.let { recipeViewModel.fetchSavedRecipes(it) } // Ensure userId is not null
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(
                        text = "My Profile",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                },
                actions = {
                    IconButton(onClick = { showDialog = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.moreoptions2),
                            contentDescription = "More actions",
                            modifier = Modifier.size(25.dp)
                        )
                    }
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
                },
                modifier = Modifier
                    .background(Color.Black) // Set the background color to black
                    .statusBarsPadding() // Apply padding for the status bar
            )
        },
        bottomBar = { AppBottomNavigation(navController) }
    ) { paddingValues ->

        // Wrap everything in a LazyColumn for vertical scrolling
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding(), // Adjusts for keyboard,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Profile Header
                ProfileHeader(navController, userViewModel)
            }

            item {
                RecipeTabSwitcherProfilePage(navController)
            }

            item {
                SearchBar(searchQuery) { newQuery -> searchQuery = newQuery }
            }

            val filteredRecipes = savedRecipes.filter {
                it.recipe.title.contains(searchQuery, ignoreCase = true)
            }

            items(filteredRecipes) { savedUserRecipe ->
                RecipeCard(
                    recipe = savedUserRecipe.recipe,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    onClick = {
                        navController.navigate("recipe_detail_page2/${savedUserRecipe.recipe.id}")
                    }
                )
            }
        }
    }
}

@Composable
fun RecipeCard(recipe: Recipes, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .fillMaxWidth()  // Ensures the card width is responsive
            .padding(2.dp)   // Adds spacing between items
            .aspectRatio(1.5f)
            .height(500.dp)  // Adjust the card height as needed
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(recipe.imageUrl),
                contentDescription = "Recipe Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = recipe.title,
                    fontSize = 16.sp,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "${recipe.totalCalories} kCal")
                    Text(text = recipe.cookTime)
                }
            }
        }
    }
}

@Composable
fun RecipeTabSwitcherProfilePage(navController: NavController) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Saved, 1: Created

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        // Saved Recipes (Default Active)
        OutlinedButton(
            onClick = { selectedTab = 0 },
            modifier = Modifier
                .weight(1f)
                .padding(end = 4.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = if (selectedTab == 0) Color.Red else Color.Transparent,
                contentColor = if (selectedTab == 0) Color.White else Color.Red
            ),
            border = BorderStroke(1.dp, Color.Red),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Saved Recipe", fontWeight = FontWeight.Bold)
        }

        // Created Recipes - Now Navigates on Click
        OutlinedButton(
            onClick = {
                selectedTab = 1
                navController.navigate("my_recipe_page") // Navigate when clicked
            },
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = if (selectedTab == 1) Color.Red else Color.Transparent,
                contentColor = if (selectedTab == 1) Color.White else Color.Red
            ),
            border = BorderStroke(1.dp, Color.Red),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("My Recipe", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ProfileHeader(navController: NavController, userViewModel: UserProfileProvider) {
    val userId by userViewModel.userId.observeAsState()
    val userProfile by userViewModel.userProfile.observeAsState(UserProfile())

    Log.d("ProfileSection", "Current User ID: $userId")
    Log.d("ProfileSection", "User Profile: Name: ${userProfile.name}, Profile Picture URL: ${userProfile.profilePictureUrl}")

    // Fetch user profile if not already loaded
    LaunchedEffect(userId) {
        userId?.let { userViewModel.fetchUserProfile(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .safeContentPadding()
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Picture (Left)
            Image(
                painter = if (!userProfile.profilePictureUrl.isNullOrEmpty()) {
                    rememberImagePainter(userProfile.profilePictureUrl)
                } else {
                    painterResource(id = R.drawable.google)
                },
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(110.dp) // Matches the size in your image
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(45.dp)) // Pushes the button to the right

            OutlinedButton(
                onClick = { navController.navigate("edit_profile") },
                modifier = Modifier
                    .height(40.dp), // Set height properly
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent, // Remove background color
                    contentColor = Color.Red // Set text color to red
                ),
                border = BorderStroke(1.dp, Color.Red), // Set border color
                shape = RoundedCornerShape(12.dp) // Rounded corners
            ) {
                Text(
                    "Edit profile",
                    color = Color.Red, // Ensure text is red
                    fontWeight = FontWeight.Bold // Bold text
                )
            }

        }
        // User Name Below Profile Picture
        Spacer(modifier = Modifier.height(8.dp)) // Spacing between picture & name
        Text(
            text = userProfile.name ?: "User",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = { newQuery -> onQueryChange(newQuery) },
        placeholder = { Text("Search recipes") },
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { layoutCoordinates ->
                // Debugging: Check position and size
                Log.d("SearchBar", "Position: ${layoutCoordinates.positionInRoot()}, Size: ${layoutCoordinates.size}")
            }
    )
}

@Composable
fun MoreSettings(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    navController: NavController,
    onThemeSelected: (String) -> Unit
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = "More Features") },
            text = {
                Column {
                    TextButton(onClick = { onThemeSelected("Dark") }) {
                        Text(text = "Light Theme")
                    }
                    TextButton(onClick = { onThemeSelected("Light") }) {
                        Text(text = "Dark Theme")
                    }
                    TextButton(onClick = {
                        navController.navigate("grocery_list_page")}) {
                        Text(text = "Grocery List")
                    }
                    TextButton(onClick = {
                        navController.navigate("health_summary_page")}) {
                        Text(text = "Health Analysis")
                    }
                    TextButton(onClick = {
                        navController.navigate("chatbot_service_page")}) {
                        Text(text = "Chatbot Service")
                    }
                    TextButton(onClick = { FirebaseAuth.getInstance().signOut()
                        navController.navigate("main_page")}){
                        Text(text = "Sign Out")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}



