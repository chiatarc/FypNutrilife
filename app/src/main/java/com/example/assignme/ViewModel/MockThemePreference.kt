package com.example.assignme.ViewModel


class MockThemePreference : ThemeInterface {
    private var darkTheme = false // Default to light theme

    override fun isDarkTheme(): Boolean {
        return darkTheme
    }

    override fun setDarkTheme(isDark: Boolean) {
        darkTheme = isDark
    }
}
