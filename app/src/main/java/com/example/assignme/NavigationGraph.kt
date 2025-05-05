package com.example.assignme

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.assignme.DataClass.Recipes
import com.example.assignme.DataClass.rememberWidowInfo
import com.example.assignme.GUI.AccountProfile.AddAdmin
import com.example.assignme.GUI.AccountProfile.AdminDashboard
import com.example.assignme.GUI.AccountProfile.AppFirstPage
import com.example.assignme.GUI.AccountProfile.EditAdminProfileScreen
import com.example.assignme.GUI.AccountProfile.EditProfileScreen
import com.example.assignme.GUI.AccountProfile.ForgotPasswordPage
import com.example.assignme.GUI.AccountProfile.LoginPage
import com.example.assignme.GUI.AccountProfile.ManageRecipePage
import com.example.assignme.GUI.AccountProfile.ManageReportPostScreen
import com.example.assignme.GUI.AccountProfile.ManageUserAdmin
import com.example.assignme.GUI.AccountProfile.ProfilePage
import com.example.assignme.GUI.AccountProfile.RecipeAddScreen
import com.example.assignme.GUI.AccountProfile.RegisterPage
import com.example.assignme.GUI.AccountProfile.SocialFeedScreen
import com.example.assignme.GUI.Community.SocialAppUI
import com.example.assignme.GUI.DailyTracker.BarChart
import com.example.assignme.GUI.DailyTracker.DailyAnalysis
import com.example.assignme.GUI.DailyTracker.HealthDataPage
import com.example.assignme.GUI.DailyTracker.HealthDataSummaryPage
import com.example.assignme.GUI.DailyTracker.LineChart
import com.example.assignme.GUI.DailyTracker.SetUpInfo
import com.example.assignme.GUI.DailyTracker.TrackerPage
import com.example.assignme.GUI.DailyTracker.Transformation
import com.example.assignme.GUI.AccountProfile.FirstPage
import com.example.assignme.GUI.Recipe.CreateRecipe
import com.example.assignme.GUI.Recipe.EditMyRecipeScreen
//import com.example.assignme.GUI.Recipe.EditMyRecipeScreen
import com.example.assignme.GUI.Recipe.GroceryListScreen
import com.example.assignme.GUI.Recipe.MyRecipe
import com.example.assignme.GUI.Recipe.RecipeMainPage
import com.example.assignme.GUI.Recipe.RecipeScreen
import com.example.assignme.GUI.Recipe.RecipeUploadPage
import com.example.assignme.GUI.Recipe.SchedulePage
import com.example.assignme.GUI.Recipe.SearchResultsPage
import com.example.assignme.ViewModel.GroceryListViewModel
import com.example.assignme.ViewModel.RecipeViewModel
import com.example.assignme.ViewModel.TrackerViewModel
import com.example.assignme.ViewModel.ThemeViewModel
import com.example.assignme.ViewModel.UserViewModel
import com.example.assignme.network.ChatbotScreen
import com.google.firebase.auth.FirebaseAuth

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun NavigationGraph(navController: NavHostController = rememberNavController(), userViewModel: UserViewModel, themeViewModel: ThemeViewModel){

    NavHost(
        navController = navController,
        startDestination = "main_page",
    ){

        composable("main_page"){

            AppFirstPage(navController, userViewModel)
        }

        composable("edit_admin"){

            EditAdminProfileScreen(navController, userViewModel)
        }

        composable("first_page"){

            FirstPage(navController, userViewModel)
        }

        composable("login_page"){

            LoginPage(navController, userViewModel)
        }

        composable("register_page"){

            RegisterPage(navController, userViewModel)
        }

        composable("forgot_password_page"){

            ForgotPasswordPage(navController, userViewModel)
        }

        composable("profile_page"){
            val viewModel: RecipeViewModel = viewModel()
            ProfilePage(navController, userViewModel, themeViewModel, viewModel)
        }

        composable("edit_profile") {

            EditProfileScreen(navController, userViewModel)
        }

        composable("admin_page"){

            AdminDashboard(userViewModel, navController, themeViewModel)
        }

        composable("add_recipe"){
            val viewModel: RecipeViewModel = viewModel() // Get a ViewModel scoped to CreateRecipe
            RecipeAddScreen(navController, viewModel = viewModel, userViewModel)
        }

        composable("manage_post"){

            SocialFeedScreen(navController, userViewModel)
        }

        composable("add_admin"){

            AddAdmin(navController, userViewModel)
        }

        composable("manage_admin_page"){

            ManageUserAdmin(navController)
        }

        composable("manage_recipe_admin") {
            val recipeViewModel: RecipeViewModel = hiltViewModel()
            ManageRecipePage(navController, recipeViewModel, userViewModel)
        }

        composable("chat") {
            SocialAppUI(navController, userViewModel)
        }

        composable("recipe_main_page") { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("recipe_main_page") }
            val viewModel: RecipeViewModel = viewModel(parentEntry)
            RecipeMainPage(navController = navController, viewModel = viewModel, userViewModel,)
        }

        composable("recipe_upload_page") { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("recipe_main_page") }
            val viewModel: RecipeViewModel = viewModel(parentEntry)
            RecipeUploadPage(navController = navController, viewModel = viewModel)
        }

        composable("search_results") { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("recipe_main_page") }
            val viewModel: RecipeViewModel = viewModel(parentEntry)
            SearchResultsPage(navController = navController, viewModel = viewModel)
        }

        composable("recipe_detail_page/{recipeId}") { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("recipe_main_page") }
            val viewModel: RecipeViewModel = viewModel(parentEntry)
            val recipeId = backStackEntry.arguments?.getString("recipeId") ?: ""
            userViewModel.userId.value

            var recipe by remember { mutableStateOf<Recipes?>(null) }

            LaunchedEffect(recipeId) {
                println("Navigated to recipe detail page with recipeId: $recipeId")
                viewModel.fetchRecipeById(recipeId) {
                    recipe = it
                    println(if (it != null) "Recipe found: ${it.title}" else "Recipe not found for id: $recipeId")
                }
            }

            if (recipe != null) {
                RecipeScreen(
                    recipe = recipe!!,
                    userModel = userViewModel,
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Recipe not found", modifier = Modifier.padding(16.dp))
                }
            }
        }


        composable("recipe_detail_page2/{recipeId}") { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("recipe_main_page") }
            val viewModel: RecipeViewModel = viewModel(parentEntry)
            val recipeId = backStackEntry.arguments?.getString("recipeId") ?: ""
            userViewModel.userId.value

            var recipe by remember { mutableStateOf<Recipes?>(null) }

            LaunchedEffect(recipeId) {
                println("Navigated to recipe detail page with recipeId: $recipeId")
                viewModel.fetchRecipeById(recipeId) {
                    recipe = it
                    println(if (it != null) "Recipe found: ${it.title}" else "Recipe not found for id: $recipeId")
                }
            }

            if (recipe != null) {
                RecipeScreen(
                    recipe = recipe!!,
                    userModel = userViewModel,
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Recipe not found", modifier = Modifier.padding(16.dp))
                }
            }
        }

        composable("create_recipe") {
            val viewModel: RecipeViewModel = viewModel() // Get a ViewModel scoped to CreateRecipe
            CreateRecipe(
                navController = navController,
                viewModel = viewModel,
                userViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("my_recipe_page") { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("profile_page") }
            val viewModel: RecipeViewModel = viewModel(parentEntry)
            val windowInfo = rememberWidowInfo()
            MyRecipe(navController = navController, viewModel = viewModel, userViewModel, windowInfo )
        }

        composable("edit_my_recipe_page") { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("profile_page") }
            val recipeViewModel: RecipeViewModel = viewModel(parentEntry)
            val userViewModel: UserViewModel = viewModel(parentEntry)

            EditMyRecipeScreen(navController = navController, viewModel = recipeViewModel, userModel = userViewModel)
        }

        composable("schedule_page") { backStackEntry ->
            val viewModel: RecipeViewModel = viewModel()
            SchedulePage(
                navController = navController,
                viewModel = viewModel,
                userModel = userViewModel,
                onBackClick = {
                    if (!navController.popBackStack()) {
                        navController.navigateUp()
                    }
                }
            )
        }

        composable("setup_info_page") { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("recipe_main_page") }

            Log.d("Navigation", "Navigating to Setup Info Page")

            SetUpInfo(
                navController = navController,
                userViewModel = userViewModel
            )
        }

        composable("health_data_page") { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("profile_page") }

            HealthDataPage(
                navController = navController,
                userViewModel = userViewModel
            )
        }

        composable("health_summary_page") { backStackEntry ->
            remember(backStackEntry) { navController.getBackStackEntry("profile_page") }

            HealthDataSummaryPage(
                navController = navController,
                userViewModel = userViewModel
            )
        }

        composable("daily_analysis") {
            Log.d("Navigation", "Navigating to Daily Analysis Page")
            val trackerViewModel: TrackerViewModel = hiltViewModel()
            DailyAnalysis(navController,trackerViewModel)
        }

        composable("tracker_page") {
            val trackerViewModel: TrackerViewModel = hiltViewModel()
            TrackerPage(navController, userViewModel, trackerViewModel)
        }

        composable("transformation_page") {
            val trackerViewModel: TrackerViewModel = hiltViewModel()
            Transformation(navController, userViewModel, trackerViewModel)
        }

        composable("lineChart_page") {
            val trackerViewModel: TrackerViewModel = hiltViewModel()
            LineChart(navController, trackerViewModel)
        }

        composable("barChart_page") {
            val trackerViewModel: TrackerViewModel = hiltViewModel()
            BarChart(navController, trackerViewModel)
        }

        composable("manageReportPost"){
            ManageReportPostScreen(navController, userViewModel)
        }

        composable("grocery_list_page") {
            val context = LocalContext.current
            val groceryListViewModel = remember { GroceryListViewModel(FirebaseAuth.getInstance(), context) }
            GroceryListScreen(navController, groceryListViewModel)
        }
        composable("chatbot_service_page") {
            ChatbotScreen(navController, userViewModel)
        }


    }
}

