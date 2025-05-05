package com.example.assignme

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.assignme.ViewModel.ThemeInterface
import com.example.assignme.ViewModel.ThemePreference
import com.example.assignme.ViewModel.ThemeViewModel
import com.example.assignme.ViewModel.UserViewModel
import com.example.assignme.network.RetrofitClient
import com.example.assignme.ui.theme.AssignmeTheme
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var themeViewModel: ThemeViewModel

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Rasa Chatbot Server Connection
        RetrofitClient.init(this)

        // Initialize ThemePreference and ThemeViewModel
        val themePreference = ThemePreference(this) // Pass context to ThemePreference
        themeViewModel = ThemeViewModel(themePreference) // Pass the ThemePreference to ThemeViewModel

        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()

        setContent {
            themeViewModel.isDarkTheme.value.let { darkTheme ->
                AssignmeTheme(darkTheme = darkTheme) {
                    // Create a navigation controller
                    val navController = rememberNavController()
                    val userViewModel: UserViewModel = viewModel() // Use Hilt or manually provide the ViewModel
                    Scaffold(modifier = Modifier.fillMaxSize()) {
                        NavigationGraph(navController, userViewModel, themeViewModel = themeViewModel)
                    }
                }
            }
        }
    }
}

