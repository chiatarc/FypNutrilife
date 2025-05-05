package com.example.assignme.DataClass

data class BottomNavItem(
    val title: String,
    val iconRes: Int,
    val selectedIconRes: Int,
    val route: String,
    val showBadge: Boolean = false // Add this flag
)