package com.example.assignme.AndroidBar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.assignme.DataClass.BottomNavItem
import com.example.assignme.R

@Composable
fun AppBottomNavigation(navController: NavController) {

    val items = listOf(
        BottomNavItem("Home", R.drawable.home, R.drawable.home_selected,"recipe_main_page"),
        BottomNavItem("Calendar", R.drawable.calendar, R.drawable.calendar_selected,"schedule_page"),
        BottomNavItem("Add", R.drawable.ic_add, R.drawable.ic_add,"create_recipe", showBadge = true), // Set showBadge to true
        BottomNavItem("Health", R.drawable.heart, R.drawable.heart_selected,"tracker_page"), // daily_analysis tracker_page
        BottomNavItem("Chat", R.drawable.chat, R.drawable.chat_selected,"chat")
    )

    Box(

        modifier = Modifier.fillMaxWidth()
    ){

        NavigationBar(
            modifier = Modifier
                .clip(BottomNavWithCurveShape()), // Apply the custom shape
            contentColor = Color.Black
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            items.forEach { item ->
                NavigationBarItem(
                    icon = {
                        if (item.title == "Add") {
                            Box(
                                modifier = Modifier
                                    .size(0.dp) // Prevent the "Add" item from taking space here
                            )
                        } else {
                            val iconRes = if (currentRoute == item.route) item.selectedIconRes else item.iconRes
                            Icon(
                                painter = painterResource(id = iconRes),
                                contentDescription = item.title,
                                modifier = Modifier.size(24.dp),
                                tint = Color.Unspecified // Use Unspecified to avoid tint affecting the icon
                            )
                        }
                    },
                    selected = currentRoute == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }

                )
            }
        }

        // Manually position the HelipadIcon at the center top of the curve
        // Manual positioning of HelipadIcon above the curve
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-32).dp) // Adjust this value to move the HelipadIcon up or down
        ) {
            HelipadIcon(
                modifier = Modifier
                    .size(72.dp)
                    .clickable { // Only the HelipadIcon is clickable
                        navController.navigate("create_recipe") {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
            )
        }
    }
}

@Composable
fun HelipadIcon(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(72.dp) // Size of the helipad
            .background(Color(0xFFE23E3E), CircleShape) // Red circle background
            .padding(5.dp), // Padding for the icon within the circle
        contentAlignment = Alignment.Center // Center the icon within the box
    ) {
        Icon(
            imageVector = Icons.Filled.Add, // Use the built-in plus icon
            contentDescription = "Add",
            modifier = Modifier.size(40.dp), // Size of the plus icon
            tint = Color.White // Color of the plus icon
        )
    }
}

fun BottomNavWithCurveShape() = GenericShape { size, _ ->
    // Move to the leftmost point
    moveTo(0f, 0f)

    // Draw a line to the left of the curve
    lineTo(size.width / 2 - 140, 0f)

    // Define the curve with wider sides and a deeper center for the HelipadIcon
    quadraticBezierTo(
        size.width / 2, 240f,  // Control point (the dip of the curve)
        size.width / 2 + 140, 0f // Widen the right side by increasing the x-coordinate
    )

    // Continue drawing the rest of the bar to the right
    lineTo(size.width, 0f)
    lineTo(size.width, size.height)
    lineTo(0f, size.height)
    close()
}
