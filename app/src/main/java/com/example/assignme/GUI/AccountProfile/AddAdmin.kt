package com.example.assignme.GUI.AccountProfile

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.assignme.AndroidBar.AppTopBar
import com.example.assignme.ViewModel.MockUserViewModel
import com.example.assignme.ViewModel.UserProfileProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue

@Composable
fun AddAdmin(navController: NavController, userViewModel: UserProfileProvider) {

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmpassword by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    fun handleRegistration() {
        // Email validation using a regular expression pattern
        val emailPattern = Regex("^[A-Za-z0-9+_.-]+@(.+)$")

        // Validate each field
        when {
            username.isBlank() -> {
                dialogMessage = "Username cannot be empty."
                showErrorDialog = true
                return
            }

            email.isBlank() -> {
                dialogMessage = "Email cannot be empty."
                showErrorDialog = true
                return
            }

            !email.matches(emailPattern) -> {
                dialogMessage = "Invalid email format."
                showErrorDialog = true
                return
            }

            password.isBlank() -> {
                dialogMessage = "Password cannot be empty."
                showErrorDialog = true
                return
            }

            password.length < 6 -> {
                dialogMessage = "Password must be at least 6 characters."
                showErrorDialog = true
                return
            }

            confirmpassword.isBlank() -> {
                dialogMessage = "Confirm password cannot be empty."
                showErrorDialog = true
                return
            }

            password != confirmpassword -> {
                dialogMessage = "Passwords do not match."
                showErrorDialog = true
                return
            }
        }

        // If all validations pass, proceed to submit
        submitAdmin(
            name = username,
            email = email,
            pswd = password,
            pswd2 = confirmpassword,
            onValidationError = {
                dialogMessage = it
                showErrorDialog = true
            },
            onSuccess = {
                dialogMessage = "User registered successfully."
                println("Success callback triggered.")
                showSuccessDialog = true
            },
            onFailure = {
                dialogMessage = it
                showErrorDialog = true
            }
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Add Admin",
                navController = navController,
                modifier = Modifier
            )
        },
    ) { innerPadding ->

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding()
                .statusBarsPadding()
        ) {

            // Define variables to hold screen width and height
            constraints.maxHeight
            constraints.maxWidth

            // LazyColumn containing text fields and other components
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .safeContentPadding()
                    .statusBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    // Text Fields
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .safeContentPadding()
                            .statusBarsPadding()
                            .padding(end = 15.dp, top = 20.dp, start = 15.dp)
                    ) {
                        TextField(
                            value = username,
                            onValueChange = { newText -> username = newText },
                            placeholder = { Text(text = "Username") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 18.dp)
                        )

                        TextField(
                            value = email,
                            onValueChange = { newText -> email = newText },
                            placeholder = { Text(text = "Email") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 18.dp)
                        )

                        TextField(
                            value = password,
                            onValueChange = { newText -> password = newText },
                            placeholder = { Text(text = "Password") },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Password
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 18.dp)
                        )

                        TextField(
                            value = confirmpassword,
                            onValueChange = { newText -> confirmpassword = newText },
                            placeholder = { Text(text = "Confirm Password") },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Password
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 18.dp)
                        )
                    }
                }

                item {
                    // Button
                    Button(
                        onClick = {
                            handleRegistration()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                        .padding(start = 15.dp, end = 15.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE23E3E)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Register")
                    }
                }

                item {
                    // Dialog logic
                    if (showErrorDialog) {
                        ErrorDialogAdmin(
                            errorMessage = dialogMessage,
                            onDismiss = { showErrorDialog = false }
                        )
                    }

                    if (showSuccessDialog) {
                        SuccessDialogAdmin(
                            message = dialogMessage,
                            onDismiss = {
                                showSuccessDialog = false
                                navController.navigate("manage_admin_page")
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    // Divider section with "You are held responsible for creating an account."
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .safeContentPadding()
                            .statusBarsPadding()
                            .padding(start = 15.dp, end = 15.dp)
                    ) {
                        HorizontalDivider(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp),
                            thickness = 1.dp,
                            color = Color.Gray
                        )
                        Text(
                            text = "You are held responsible for creating an account.",
                            fontSize = 16.sp
                        )
                        HorizontalDivider(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp),
                            thickness = 1.dp,
                            color = Color.Gray
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

}

fun submitAdmin(
    name: String,
    email: String,
    pswd: String,
    pswd2: String,
    phoneNumber: String? = null,
    profilePictureUrl: String? = null,
    gender: String? = null,
    country: String? = null,
    onValidationError: (String) -> Unit,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit
) {
    println("Starting registration process...")

    // Check if passwords match
    if (pswd != pswd2) {
        onValidationError("Passwords do not match")
        return
    }

    // Initialize Firebase Auth
    val auth = FirebaseAuth.getInstance()
    println("Firebase Auth instance obtained.")

    // Create a new user
    auth.createUserWithEmailAndPassword(email, pswd)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                println("User registration successful.")
                val userId = task.result?.user?.uid ?: run {
                    onFailure("Failed to get user ID")
                    return@addOnCompleteListener
                }
                Log.d("SubmitRegistration", "User ID: $userId")

                // Add user data to Firestore
                val db = FirebaseFirestore.getInstance()
                println("Firestore instance obtained.")

                // Create a map for user data including profilePictureUrl even if it's null
                val user = hashMapOf(
                    "name" to name,
                    "email" to email,
                    "userId" to userId,
                    "phoneNumber" to phoneNumber,
                    "profilePictureUrl" to profilePictureUrl, // This will be null if not provided
                    "gender" to gender,
                    "country" to country,
                    "authmethod" to "firebase",
                    "type" to "admin"
                )

                db.collection("admin").document(userId)
                    .set(user)
                    .addOnSuccessListener {
                        println("User data added to Firestore.")

                        // Add profile picture URL to the 'profile_picture' collection
                        val profilePicture = hashMapOf(
                            "profilePictureUrl" to profilePictureUrl, // You can add more fields if needed
                            "timestamp" to FieldValue.serverTimestamp() // Optional: Adds a timestamp
                        )

                        db.collection("admin").document(userId)
                            .collection("profile_picture")
                            .add(profilePicture)
                            .addOnSuccessListener {
                                println("Profile picture added to Firestore.")
                                onSuccess() // Notify success
                            }
                            .addOnFailureListener { e ->
                                println("Error adding profile picture: ${e.message}")
                                onFailure("Error adding profile picture: ${e.message}")
                            }
                    }
                    .addOnFailureListener { e ->
                        println("Error adding user to Firestore: ${e.message}")
                        onFailure("Error adding user: ${e.message}")
                    }
            } else {
                println("Registration failed: ${task.exception?.message}")
                onFailure("Registration failed: ${task.exception?.message}")
            }
        }
}

@Composable
fun ErrorDialogAdmin(
    errorMessage: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Error") },
        text = { Text(errorMessage) },
        confirmButton = {
            Button(colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE23E3E)),
                onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

@Composable
fun SuccessDialogAdmin(
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Success") },
        text = { Text(message) },
        confirmButton = {
            Button(colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE23E3E)),
                onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewAddAdmin() {

    AddAdmin(
        navController = rememberNavController(),
        userViewModel = MockUserViewModel()
    )
}