package com.example.assignme.GUI.AccountProfile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.assignme.ViewModel.MockUserViewModel
import com.example.assignme.R
import com.example.assignme.ViewModel.UserProfileProvider
import com.example.assignme.ViewModel.UserViewModel

@Composable
fun FirstPage(navController: NavController, userViewModel: UserProfileProvider) {
    // Create an instance of UserViewModel
    val userViewModel: UserViewModel = viewModel()

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
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding()
                .statusBarsPadding(),
            contentScale = ContentScale.Crop
        )

        // Top content: Premium Recipe row
        Column(
            modifier = Modifier
                .safeContentPadding()
                .fillMaxSize()
                .statusBarsPadding()
                .padding(top = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
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

        // Center content: Main Text Content
        MainTextContent(modifier = Modifier.align(Alignment.Center))

        // Bottom content: Login and Register buttons
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 120.dp)
                .safeContentPadding()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Button(
                onClick = { navController.navigate("login_page") },
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .size(width = 206.dp, height = 54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE23E3E)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Login")
            }
            Spacer(modifier = Modifier.padding(15.dp))
            Button(
                onClick = { navController.navigate("register_page") },
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .size(width = 206.dp, height = 54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE23E3E)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Register")
            }
        }
    }
}

@Composable
fun MainTextContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Let's",
            fontSize = 56.sp,
            color = Color.White
        )
        Text(
            text = "Cooking",
            fontSize = 56.sp,
            color = Color.White
        )
        Text(
            text = "Find healthy recipes for cooking",
            fontSize = 15.sp,
            modifier = Modifier.padding(top = 30.dp),
            color = Color.White
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewFirstPage() {

    FirstPage(
        navController = rememberNavController(),
        userViewModel = MockUserViewModel()
    )
}

