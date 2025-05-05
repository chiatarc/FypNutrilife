package com.example.assignme.GUI.Recipe

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.assignme.AndroidBar.AppBottomNavigation
import com.example.assignme.AndroidBar.AppTopBar
import com.example.assignme.ViewModel.RecipeViewModel
import com.example.assignme.ViewModel.UserViewModel

@Composable
fun EditMyRecipeScreen(
    navController: NavController,
    viewModel: RecipeViewModel = hiltViewModel(),
    userModel: UserViewModel
) {
    // Observe userId and recipes from the view models
    val userId by userModel.userId.observeAsState()
    val userRecipes by viewModel.filteredRecipes.collectAsState()

    // Ensure the UserViewModel has a valid userId.
    // Make sure you have implemented fetchAndSetUserId() in UserViewModel.
    LaunchedEffect(Unit) {
        if (userId.isNullOrEmpty()) {
            userModel.fetchAndSetUserId()
        }
    }

    // Load the user's recipes when userId is available
    LaunchedEffect(userId) {
        userId?.let { viewModel.loadUserRecipes(it) }
    }

    Scaffold(
        topBar = { AppTopBar(title = "Edit Recipe", navController = navController) },
        bottomBar = { AppBottomNavigation(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            if (userRecipes.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(userRecipes) { recipe ->
                        var expanded by remember { mutableStateOf(false) }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expanded = !expanded },
                            elevation = 4.dp
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = recipe.title,
                                        style = MaterialTheme.typography.body1
                                    )

                                    IconButton(
                                        onClick = {
                                            // Check if userId is available before deleting
                                            userId?.let { user ->
                                                if (recipe.id.isNotEmpty()) {
                                                    Log.d("DeleteDebug", "Attempting to delete: ${recipe.id}")
                                                    viewModel.deleteUserCreatedRecipe(
                                                        recipeId = recipe.id,
                                                        userId = user,
                                                        onSuccess = {
                                                            Toast.makeText(
                                                                navController.context,
                                                                "Recipe deleted!",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                            // Refresh recipes list after deletion
                                                            viewModel.loadUserRecipes(user)
                                                        },
                                                        onFailure = { exception ->
                                                            Toast.makeText(
                                                                navController.context,
                                                                "Error: ${exception.message}",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    )
                                                } else {
                                                    Log.e("DeleteDebug", "Invalid recipe ID")
                                                }
                                            } ?: Log.e("DeleteDebug", "User ID is null")
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Recipe",
                                            tint = Color.Red
                                        )
                                    }
                                }

                                if (expanded) {
                                    AsyncImage(
                                        model = recipe.imageUrl,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No recipes found",
                        style = MaterialTheme.typography.body1,
                        color = Color.Red
                    )
                }
            }
        }
    }
}




