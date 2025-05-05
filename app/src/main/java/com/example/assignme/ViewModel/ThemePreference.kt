package com.example.assignme.ViewModel

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources.Theme

class ThemePreference(context: Context): ThemeInterface {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_DARK_THEME = "is_dark_theme"
    }

    override fun isDarkTheme(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_DARK_THEME, false) // Default to light theme
    }

    override fun setDarkTheme(isDark: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_IS_DARK_THEME, isDark).apply()
    }
}