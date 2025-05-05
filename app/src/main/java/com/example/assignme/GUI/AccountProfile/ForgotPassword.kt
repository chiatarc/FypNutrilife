package com.example.assignme.GUI.AccountProfile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.assignme.AndroidBar.AppTopBar
import com.example.assignme.R
import com.example.assignme.ViewModel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

@Composable
fun ForgotPasswordPage(navController: NavController, userViewModel: UserViewModel = viewModel()){

    var email by remember { mutableStateOf("") }
    var dialogMessage by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var sent by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Forgot Password", // Updated the title
                navController = navController,
                modifier = Modifier
            )
        },
        content = { paddingValues -> // Use paddingValues for inner padding

            // Top-level Box to hold all the components
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues) // Apply scaffold's inner padding
                    .safeContentPadding()
                    .statusBarsPadding()
            ) {
                // LazyColumn for scrollable content
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 15.dp, vertical = 20.dp),
                    contentPadding = PaddingValues(16.dp) // Inner content padding
                ) {
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.Top
                        ) {
                            Text(
                                text = "OTP Verification",
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(18.dp))
                            Text(
                                text = "Don't worry! It occurs. Please enter the email address linked with your account.",
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 15.dp, top = 45.dp)
                        ) {
                            TextField(
                                value = email,
                                onValueChange = { newText -> email = newText },
                                placeholder = { Text(text = "Enter your email") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 18.dp) // Adds space between text fields
                            )
                        }
                    }

                    item {
                        Button(
                            onClick = {
                                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                    dialogMessage = "Invalid email format."
                                    showDialog = true
                                } else {
                                    sendPasswordResetEmail(
                                        email,
                                        onSuccess = {
                                            dialogMessage = "A password reset email has been sent to $email if it exists."
                                            showDialog = true
                                            sent = true
                                        },
                                        onFailure = { error ->
                                            dialogMessage = error
                                            showDialog = true
                                        }
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE23E3E)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Verify")
                        }
                    }

                    item {
                        if (sent) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally // Center content horizontally
                            ) {
                                Text(
                                    text = "Didn't receive the code? ",
                                    fontSize = 15.sp,
                                    maxLines = 2, // Allow text to wrap if needed
                                    overflow = TextOverflow.Ellipsis // Use ellipsis if it overflows
                                )
                                Text(
                                    text = "Resend",
                                    fontSize = 15.sp,
                                    color = Color(0xFF0000FF), // You can customize the color
                                    modifier = Modifier.clickable {
                                        // Validate email format
                                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                            dialogMessage = "Invalid email format."
                                            showDialog = true
                                        } else {
                                            sendPasswordResetEmail(
                                                email,
                                                onSuccess = {
                                                    dialogMessage = "A password reset email has been sent to $email if it exists."
                                                    showDialog = true
                                                },
                                                onFailure = { error ->
                                                    dialogMessage = error
                                                    showDialog = true
                                                }
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(50.dp))
                    }
                }

                // Dialog for messages
                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Notification") },
                        text = { Text(dialogMessage) },
                        confirmButton = {
                            Button(
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE23E3E)),
                                onClick = { showDialog = false }
                            ) {
                                Text("OK")
                            }
                        }
                    )
                }
            }
        }
    )
}

fun sendPasswordResetEmail(
    email: String,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    auth.sendPasswordResetEmail(email)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Email sent successfully
                onSuccess()
            } else {
                // Error occurred
                val errorMessage = when (task.exception) {
                    is FirebaseAuthInvalidUserException -> "The email address is not registered."
                    is FirebaseAuthInvalidCredentialsException -> "Invalid email format."
                    else -> "Failed to send reset email. Please try again."
                }
                onFailure(errorMessage)
            }
        }
}

@Preview(showBackground = true)
@Composable
fun PreviewForgotPassword() {
    val navController = rememberNavController() // Mock NavController for preview
    ForgotPasswordPage(navController)
}