package com.example.assignme.GUI.Recipe

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.assignme.AndroidBar.AppBottomNavigation
import com.example.assignme.AndroidBar.AppTopBar
import com.example.assignme.DataClass.WindowInfo
import com.example.assignme.ViewModel.RecipeViewModel
import com.example.assignme.ViewModel.UserViewModel
import kotlinx.coroutines.FlowPreview

@Composable
fun MyRecipe(
    navController: NavController,
    viewModel: RecipeViewModel = viewModel(),
    userModel: UserViewModel,
    windowInfo: WindowInfo
) {
    val filteredRecipes by viewModel.filteredRecipes.collectAsState()
    val userId by userModel.userId.observeAsState()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        userId?.let { id ->
            viewModel.loadUserRecipes(id) // Load only recipes with authorId == userId
        }
    }

    Scaffold(
        topBar = { AppTopBar(title = "My recipes", navController = navController) },
        bottomBar = { AppBottomNavigation(navController = navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("edit_my_recipe_page") },
                containerColor = Color(0xFFE23E3E) // Red background from your color code
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Recipe",
                    tint = Color.White // White icon
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SearchBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = {
                        searchQuery = it
                        viewModel.searchUserRecipes(it, userId ?: "")
                    },
                    onSearch = { viewModel.searchRecipes(searchQuery) },
                )
            }

            val columns = when (windowInfo.screenWidthInfo) {
                WindowInfo.WindowType.Compact -> 2
                WindowInfo.WindowType.Medium -> 3
                WindowInfo.WindowType.Expanded -> 4
            }

            if (filteredRecipes.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredRecipes) { recipe ->
                        RecipeCard(recipe = recipe, onClick = {
                            println("Navigating to recipe_detail_page with id: ${recipe.id}")
                            navController.navigate("recipe_detail_page/${recipe.id}")
                        })
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No recipes found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
