package com.example.assignme.ViewModel

// MockUserViewModel.kt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MockThemeViewModel : ThemeViewModel(MockThemePreference()) {

    init {
        // Initialize with a default theme
        isDarkTheme.value = false // Light theme
    }
}




