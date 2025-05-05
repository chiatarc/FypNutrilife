package com.example.assignme.GUI.AccountProfile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.assignme.DataClass.Recipes
import com.example.assignme.ViewModel.RecipeViewModel
import com.example.assignme.ViewModel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageRecipePage(
    navController: NavController,
    viewModel: RecipeViewModel = hiltViewModel(),
    userModel: UserViewModel
) {
    // State for the currently selected recipe (for image preview).
    var selectedRecipe by remember { mutableStateOf<Recipes?>(null) }

    // Fetch the recipes when this screen appears.
    LaunchedEffect(Unit) {
        viewModel.fetchRecipes()
    }

    // Collect the recipe list from the ViewModel’s StateFlow.
    val recipes by viewModel.recipes.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Manage Recipes") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_recipe") },
                containerColor = Color(0xFFE23E3E),
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Recipe"
                )
            }
        }
    ) { innerPadding ->
        // Use a Box with a light-gray background to match the style in your screenshot.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF2F2F2))
        ) {
            // List of recipe cards
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(recipes) { recipe ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { selectedRecipe = recipe },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = recipe.title,
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                                color = Color.Black
                            )
                            IconButton(
                                onClick = {
                                    // Remove the recipe from Firestore
                                    viewModel.removeUserRecipe(
                                        recipeId = recipe.id,
                                        onSuccess = { /* Optionally show a success message */ },
                                        onFailure = { error -> /* Handle error if needed */ }
                                    )
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Recipe",
                                    tint = Color(0xFFE23E3E)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // If a recipe is selected, show a dialog with its image.
    if (selectedRecipe != null) {
        AlertDialog(
            onDismissRequest = { selectedRecipe = null },
            title = { Text(text = selectedRecipe!!.title) },
            text = {
                AsyncImage(
                    model = selectedRecipe!!.imageUrl,
                    contentDescription = selectedRecipe!!.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = { selectedRecipe = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE23E3E))
                ) {
                    Text("Close", color = Color.White)
                }
            }
        )
    }
}
