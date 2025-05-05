package com.example.assignme.ViewModel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel


open class ThemeViewModel(private val themePreference: ThemeInterface) : ViewModel() {

    var isDarkTheme = mutableStateOf(themePreference.isDarkTheme())

    fun toggleTheme() {
        isDarkTheme.value = !isDarkTheme.value
        themePreference.setDarkTheme(isDarkTheme.value)
    }
}
