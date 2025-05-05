package com.example.assignme.GUI.Recipe

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.assignme.AndroidBar.AppBottomNavigation
import com.example.assignme.AndroidBar.AppTopBar
import com.example.assignme.DataClass.Recipes
import com.example.assignme.DataClass.WindowInfo
import com.example.assignme.DataClass.rememberWidowInfo
import com.example.assignme.GUI.Recipe.ui.theme.Orange
import com.example.assignme.R
import com.example.assignme.ViewModel.RecipeViewModel
import com.example.assignme.ViewModel.UserProfile
import com.example.assignme.ViewModel.UserViewModel

@Composable
fun RecipeMainPage(navController: NavController, viewModel: RecipeViewModel = viewModel(), userModel: UserViewModel) {
    val windowInfo = rememberWidowInfo()
    val recipes by viewModel.filteredRecipes.collectAsState()
    val categories by viewModel.categories.collectAsState()
    var selectedCategory by remember {
        mutableStateOf(if (categories.isNotEmpty()) categories[0] else "")
    }

    LaunchedEffect(Unit) {
        viewModel.loadUserAndAdminIds {
            viewModel.fetchRecipes(onlyFromAdmins = true)
            viewModel.fetchCategories(includeOnlyAdmin = true)
        }
    }

    LaunchedEffect(categories) {
        if (categories.isNotEmpty() && selectedCategory !in categories) {
            selectedCategory = categories.first()
        }
    }

    Scaffold(
        topBar = { AppTopBar(title = "Recipes", navController = navController) },
        bottomBar = { AppBottomNavigation(navController = navController) }
    ) { paddingValues ->
        // Determine layout based on screen size
        val verticalSpacing = when (windowInfo.screenWidthInfo) {
            WindowInfo.WindowType.Compact -> 8.dp
            WindowInfo.WindowType.Medium -> 16.dp
            WindowInfo.WindowType.Expanded -> 24.dp
        }

        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(verticalSpacing),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Header(navController, userModel)
            }

            item {
                SearchBar2(onSearch = { navController.navigate("search_results") })
            }

            item {
                if (recipes.isNotEmpty()) {
                    TrendingSection(
                        recipes = recipes,
                        navController = navController,
                        windowInfo = windowInfo,
                        viewModel = viewModel
                    )

                } else {
                    Text(
                        text = "No recipes found",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                if (categories.isNotEmpty()) {
                    PopularCategorySection(
                        categories = categories,
                        selectedCategory = selectedCategory,
                        onCategorySelected = { selectedCategory = it },
                        windowInfo = windowInfo
                    )
                }
            }

            if (selectedCategory.isNotEmpty()) {
                val filteredRecipes = if (selectedCategory == "All") {
                    recipes
                } else {
                    recipes.filter { it.category == selectedCategory }
                }

                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(filteredRecipes) { recipe ->
                            RecipeCard(recipe = recipe, onClick = {
                                navController.navigate("recipe_detail_page/${recipe.id}")
                            })
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun Header(navController: NavController, userModel: UserViewModel) {
    val userProfile by userModel.userProfile.observeAsState(UserProfile())
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp), // Added padding to the left and right
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Find best recipes\nfor cooking",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Image(
            painter = rememberImagePainter(
                data = userProfile.profilePictureUrl.takeIf { !it.isNullOrEmpty() } ?: R.drawable.profile
            ),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(64.dp) // Increased size of the image icon
                .clip(CircleShape)
                .clickable {
                    navController.navigate("profile_page")
                },
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun SearchBar2(
    onSearch: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                onSearch()  // 触发搜索跳转
            }
            .height(56.dp) // 搜索栏的高度
            .padding(horizontal = 16.dp), // 内边距
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Search recipes", // 搜索栏的占位符文本
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TrendingSection(
    recipes: List<Recipes>,
    navController: NavController,
    windowInfo: WindowInfo,
    viewModel: RecipeViewModel
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val recipeCardSize = when (windowInfo.screenWidthInfo) {
        WindowInfo.WindowType.Compact -> 120.dp
        WindowInfo.WindowType.Medium -> 160.dp
        WindowInfo.WindowType.Expanded -> 200.dp
    }
    val adminRecipes = recipes.filter { viewModel.getAuthorRole(it.authorId) == "admin" }
    val shuffledRecipes = adminRecipes.shuffled()

    val recipeCount = if (isLandscape &&
        (windowInfo.screenWidthInfo == WindowInfo.WindowType.Medium ||
                windowInfo.screenWidthInfo == WindowInfo.WindowType.Expanded)) {
        7
    } else {
        5
    }
    Column(
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Trending now 🔥",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            TextButton(
                onClick = { navController.navigate("search_results") },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Orange // Set the text color for the button content
                )
            ) {
                Text(
                    "See all",
                    color = Orange // Set the color of the text explicitly
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(shuffledRecipes.take(recipeCount)) { recipe ->
                Box(modifier = Modifier.size(recipeCardSize)) {
                    RecipeCard(recipe = recipe, onClick = {
                        navController.navigate("recipe_detail_page/${recipe.id}")
                    })
                }
            }
        }
    }
}

@Composable
fun PopularCategorySection(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    windowInfo: WindowInfo
) {
    val chipPadding = when (windowInfo.screenWidthInfo) {
        WindowInfo.WindowType.Compact -> 4.dp
        WindowInfo.WindowType.Medium -> 8.dp
        WindowInfo.WindowType.Expanded -> 12.dp
    }

    Column {
        Text(
            text = "Popular category",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow {
            items(categories) { category ->
                FilterChip(
                    onClick = { onCategorySelected(category) },
                    label = { Text(category) },
                    selected = category == selectedCategory,
                    modifier = Modifier.padding(end = chipPadding)
                )
            }
        }
    }
}
