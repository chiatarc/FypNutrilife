package com.example.assignme.GUI.AccountProfile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.assignme.R
import com.example.assignme.ViewModel.UserProfileProvider
import com.example.assignme.ViewModel.MockUserViewModel

@Composable
fun AppFirstPage(navController: NavController, userViewModel: UserProfileProvider) {

    // Detect screen orientation
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .safeContentPadding()
            .statusBarsPadding()
    ) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Burger layers",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Adjust layout based on orientation
        if (isPortrait) {
            PortraitContent(navController)
        } else {
            LandscapeContent(navController)
        }
    }
}

@Composable
fun PortraitContent(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .safeContentPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ){
        PremiumRecipeRow()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        MainTextContent()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 150.dp),
        contentAlignment = Alignment.BottomCenter
    ){
        StartCookingButton(navController)
    }
}

@Composable
fun LandscapeContent(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            MainTextContent()
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            PremiumRecipeRow()
            StartCookingButton(navController)
        }
    }
}

@Composable
fun PremiumRecipeRow() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(30.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_main_star),
            contentDescription = "Star Icon",
            modifier = Modifier.size(12.dp),
            tint = Color.White
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Premium Recipes",
            style = MaterialTheme.typography.labelMedium,
            color = Color.White
        )
    }
}

@Composable
fun MainTextContent() {
    Text(
        text = "Let's",
        fontSize = 56.sp,
        color = Color.White,
    )
    Text(
        text = "Cooking",
        fontSize = 56.sp,
        color = Color.White,
    )
    Text(
        text = "Find healthy recipes for cooking",
        fontSize = 15.sp,
        modifier = Modifier.padding(top = 30.dp),
        color = Color.White
    )
}

@Composable
fun StartCookingButton(navController: NavController) {
    Button(
        onClick = { navController.navigate("first_page") },
        modifier = Modifier
            .padding(horizontal = 32.dp)
            .size(width = 206.dp, height = 54.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE23E3E)),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(text = "Start Cooking")
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainPage() {
    AppFirstPage(
        navController = rememberNavController(),
        userViewModel = MockUserViewModel()
    )
}