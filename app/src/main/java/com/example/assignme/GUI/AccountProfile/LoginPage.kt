package com.example.assignme.GUI.AccountProfile

import android.app.Activity.RESULT_OK
import android.util.Log
import android.util.Patterns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.assignme.AndroidBar.AppTopBar
import com.example.assignme.DataClass.GoogleAuthUiClient
import com.example.assignme.R
import com.example.assignme.DataClass.SignInResult
import com.example.assignme.DataClass.UserData
import com.example.assignme.GUI.Recipe.ui.theme.Orange
import com.example.assignme.ViewModel.MockUserViewModel
import com.example.assignme.ViewModel.UserProfileProvider
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(navController: NavController, userViewModel: UserProfileProvider) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    var showErrorDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    var signInResult by remember { mutableStateOf<SignInResult?>(null) }

    // Initialize GoogleAuthUiClient
    val context = LocalContext.current
    val googleAuthUiClient = remember {
        GoogleAuthUiClient(
            context = context,
            oneTapClient = Identity.getSignInClient(context)
        )
    }

    //Google sign in
    fun handleSignInResult(
        signInResult: SignInResult,
        navController: NavController,
        userViewModel: UserProfileProvider
    ) {
        Log.d(
            "HandleSignInResult",
            "Handling sign-in result. Is new user: ${signInResult.isNewUser}"
        )

        when {
            signInResult.errorMessage != null -> {
                Log.e("HandleSignInResult", "Error during sign-in: ${signInResult.errorMessage}")
                dialogMessage = signInResult.errorMessage
                showErrorDialog = true
            }

            signInResult.data != null -> {
                if (signInResult.isNewUser) {
                    Log.d("HandleSignInResult", "New user detected. Redirecting to registration.")
                    dialogMessage =
                        "You need to register an account first. Redirecting to the registration page."
                    showErrorDialog = true
                    // Ensure this route is correct
                    navController.navigate("registrationScreen")
                } else {
                    Log.d(
                        "HandleSignInResult",
                        "Existing user logged in successfully. Navigating to home."
                    )
                    dialogMessage = "Login successful. Welcome back!"
                    showSuccessDialog = true

                    signInResult.data?.userId?.let {
                        userViewModel.setUserId(it)
                        Log.d("LoginPage", "Setting user ID: ${userViewModel.userId.value}")
                        navController.navigate("recipe_main_page") {
                            popUpTo("main_page") {
                                inclusive = true
                            } // Clear the back stack, removing main_page
                        }
                        // Ensure this route is correct
                    }
                }
            }

            else -> {
                Log.e("HandleSignInResult", "Unknown issue occurred. No data and no error message.")
                dialogMessage = "An unknown issue occurred. Please try again."
                showErrorDialog = true
            }
        }
    }

    val coroutineScope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == RESULT_OK) {
                coroutineScope.launch {
                    val signInResult = googleAuthUiClient.signInWithIntent(
                        intent = result.data ?: return@launch
                    )
                    handleSignInResult(signInResult, navController, userViewModel)
                }
            }
        }
    )

    fun signInGoogle() {
        coroutineScope.launch {
            val signInIntentSender = googleAuthUiClient.signIn()
            signInIntentSender?.let {
                launcher.launch(IntentSenderRequest.Builder(it).build())
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Login Page",
                navController = navController,
                modifier = Modifier
            )
        },
        bottomBar = { /* Add a bottom bar if needed */ }
    ) { innerPadding ->

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // <-- Apply innerPadding here
                .safeContentPadding()
                .statusBarsPadding()
        ) {
            // Define variables to hold screen width and height
            val screenHeight = constraints.maxHeight

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 15.dp, end = 15.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                // Welcome text
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Top
                    ) {
                        Text(
                            text = "Welcome back! Glad to see you, again!",
                            fontSize = 30.sp,
                            lineHeight = 36.sp, // Adjust lineHeight to increase spacing between lines
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 15.dp),
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp)) // Space between texts and text fields
                }

                // Text Fields
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 15.dp, top = 20.dp) // Reduced top padding
                    ) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { newText -> email = newText },
                            placeholder = { Text(text = "Enter your email") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 18.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Orange // Change to your Orange color
                            )
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = { newText -> password = newText },
                            placeholder = { Text(text = "Enter your password") },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Password
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 18.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Orange // Change to your Orange color
                            ),
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                    )
                                }
                            }
                        )

                        // Forgot Password text
                        Row(
                            modifier = Modifier
                                .align(Alignment.End)
                                .padding(top = 8.dp)
                        ) {
                            Text(
                                text = "Forgot Password?",
                                modifier = Modifier.clickable {
                                    // Navigate to ForgotPassword screen
                                    navController.navigate("forgot_password_page")
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height((screenHeight * 0.03f).dp)) // Pushes the button to the bottom
                    }
                }

                // Login Button
                item {
                    Button(
                        onClick = {
                            // Call login function
                            login(
                                auth, db, email, password, onError = {
                                    // On error, show error dialog
                                    errorMessage = it
                                    showErrorDialog = true
                                },
                                onSuccess = { result ->
                                    signInResult = result
                                    // Safely access signInResult
                                    signInResult?.data?.userId?.let { userId ->
                                        println("Sign-in successful: $userId")
                                    } // On success, show success dialog
                                    showSuccessDialog = true
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE23E3E)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Login")
                    }

                    Spacer(modifier = Modifier.height(16.dp)) // Space below the button
                }

                // Divider section with "or login with"
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        HorizontalDivider(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp),
                            thickness = 1.dp,
                        )
                        Text(text = "or login with")
                        HorizontalDivider(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp),
                            thickness = 1.dp,
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp)) // Space below the divider
                }
                // Google Icon Button
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth() // Fill the maximum width of the parent
                            .padding(horizontal = 16.dp) // Optional padding on the sides
                    ) {
                        Button(
                            onClick = { signInGoogle() },
                            modifier = Modifier
                                .align(Alignment.Center) // Center the button in the Box
                                .height(54.dp)
                                .fillMaxWidth(), // Make the button fill the width of the Box
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8ECF4)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.google), // Replace with your drawable resource
                                contentDescription = "Google Login",
                                modifier = Modifier.size(24.dp),
                                tint = Color.Unspecified
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp)) // Space below the Google button
                }

                // Registration Text
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Don't have an account? ",
                            fontSize = 12.sp,
                        )
                        Text(
                            text = "Register now",
                            color = Color.Blue,
                            fontSize = 12.sp,
                            modifier = Modifier.clickable {
                                // Navigate to Register screen
                                navController.navigate("register_page")
                            }
                        )
                    }
                }
            }
        }

        // Success AlertDialog
        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showSuccessDialog = false },
                title = { Text("Success") },
                text = { Text("You have successfully logged in.") },
                confirmButton = {
                    Button(onClick = {
                        // Debugging: Ensure signInResult is not null
                        signInResult?.let { result ->
                            println("Dialog confirm button clicked. signInResult: $signInResult")
                            handleSignInResult2(result, navController, userViewModel)
                            showSuccessDialog = false
                        } ?: run {
                            println("Error: signInResult is null")
                        }
                    },
                        colors = ButtonDefaults.buttonColors(containerColor = Orange)
                    ) {
                        Text("OK")
                    }
                }
            )
        }

        // Error AlertDialog
        if (showErrorDialog) {
            AlertDialog(
                onDismissRequest = { showErrorDialog = false },
                title = { Text("Error") },
                text = { Text(errorMessage ?: "Unknown error") },
                confirmButton = {
                    Button(onClick = { showErrorDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Orange)) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

//Firebase
fun handleSignInResult2(result: SignInResult, navController: NavController, userViewModel: UserProfileProvider) {

    result.data?.userId?.let {
        userViewModel.setUserId(it)
        val userType = result.data.userType
        when (userType) {
            "admin" -> {
                // Navigate to admin dashboard
                navController.navigate("admin_page") {
                    popUpTo("main_page") { inclusive = true } // Clear the back stack, removing main_page
                }
            }
            "normal" -> {
                // Navigate to normal user home screen
                navController.navigate("recipe_main_page") {
                    popUpTo("main_page") { inclusive = true } // Clear the back stack, removing main_page
                }
            }
            else -> {
                // Fallback, if the user type is unknown or not set
                navController.navigate("main_page")
            }
        }
    }
}

fun login(
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    email: String,
    password: String,
    onError: (String) -> Unit,
    onSuccess: (SignInResult) -> Unit
) {
    if (email.isNotEmpty() && password.isNotEmpty()) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            onError("Email format is incorrect.")
            return
        }

        // Check password length before proceeding
        if (password.length < 6) {
            onError("Password must be at least 6 characters long.")
            return
        }

        // Check if the account is locked
        isAccountLocked(email) { isLocked ->
            if (isLocked) {
                // Get the remaining lock time dynamically
                calculateRemainingLockTime(email) { remainingMinutes ->
                    onError("Your account is locked due to multiple failed attempts. Please try again in $remainingMinutes minutes.")
                }
            } else {
                // Proceed with sign in
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = task.result?.user
                            if (user != null) {
                                val userId = user.uid
                                val isNewUser = task.result?.additionalUserInfo?.isNewUser ?: false

                                // Check if the email exists in the "admin" collection
                                db.collection("admin")
                                    .whereEqualTo("email", email)
                                    .get()
                                    .addOnSuccessListener { adminResult ->
                                        val userType = if (!adminResult.isEmpty) {
                                            "admin"
                                        } else {
                                            "normal"
                                        }
                                        // Create the sign-in result
                                        val signInResult = mapAuthResultToSignInResult(user, isNewUser).apply {
                                            data?.userType = userType
                                        }
                                        // Log user data for debugging
                                        signInResult.data?.let { userData ->
                                            Log.d("SignInResult", "User ID: ${userData.userId}")
                                            Log.d("SignInResult", "Username: ${userData.username}")
                                            Log.d("SignInResult", "Profile Picture URL: ${userData.profilePictureUrl}")
                                            Log.d("SignInResult", "Is New User: ${signInResult.isNewUser}")
                                            Log.d("SignInResult", "Error Message: ${signInResult.errorMessage}")
                                        }

                                        // Reset failed login attempts on successful login
                                        resetLoginAttempts(email)

                                        // Proceed with success callback
                                        onSuccess(signInResult)
                                    }
                                    .addOnFailureListener { exception ->
                                        onError("Failed to fetch user data: ${exception.localizedMessage}")
                                    }
                            } else {
                                onError("User not found, Please Register")
                            }
                        } else {
                            // Call updateFailedLoginAttempt with a callback
                            updateFailedLoginAttempt(email) { attemptsLeft ->
                                val error = "Wrong login information. You have $attemptsLeft attempts left."
                                if (attemptsLeft <= 0) {
                                    // If locked, get the remaining lock time
                                    calculateRemainingLockTime(email) { remainingMinutes ->
                                        onError("Your account has been locked due to too many failed attempts. Please try again in $remainingMinutes minutes.")
                                    }
                                } else {
                                    onError(error)
                                }
                            }
                        }
                    }
            }
        }
    } else {
        onError("Please enter email and password")
    }
}

fun isAccountLocked(email: String, callback: (Boolean) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("loginAttempts").document(email).get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                val lastAttemptTime = document.getLong("lastAttemptTime") ?: 0
                val attemptCount = document.getLong("attemptCount") ?: 0
                val currentTime = System.currentTimeMillis()

                // Check if attempts are over the limit and within lockout period
                callback(attemptCount >= 3 && currentTime - lastAttemptTime < 15 * 60 * 1000)
            } else {
                // If the document does not exist, the account is not locked
                callback(false)
            }
        }
        .addOnFailureListener { exception ->
            Log.e("LoginCheck", "Failed to check if account is locked: ${exception.localizedMessage}")
            callback(false) // Assume not locked in case of failure
        }
}

fun resetLoginAttempts(email: String) {
    val db = FirebaseFirestore.getInstance()
    db.collection("loginAttempts").document(email)
        .set(hashMapOf("attemptCount" to 0), SetOptions.merge())
        .addOnSuccessListener {
            Log.d("LoginAttempts", "Reset login attempts for $email")
        }
        .addOnFailureListener { exception ->
            Log.e("LoginAttempts", "Failed to reset login attempts: ${exception.localizedMessage}")
        }
}

fun updateFailedLoginAttempt(email: String, callback: (Int) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val updates = hashMapOf(
        "attemptCount" to FieldValue.increment(1),
        "lastAttemptTime" to System.currentTimeMillis()
    )

    db.collection("loginAttempts").document(email)
        .set(updates, SetOptions.merge())
        .addOnSuccessListener {
            Log.d("LoginAttempts", "Updated login attempts for $email")
            // Now, retrieve the updated attempt count
            db.collection("loginAttempts").document(email).get()
                .addOnSuccessListener { document ->
                    val attemptCount = document.getLong("attemptCount")?.toInt() ?: 0
                    // Calculate remaining attempts (3 is the limit)
                    val remainingAttempts = maxOf(0, 3 - attemptCount)
                    callback(remainingAttempts) // Call the callback with the number of attempts left
                }
        }
        .addOnFailureListener { exception ->
            Log.e("LoginAttempts", "Failed to update login attempts: ${exception.localizedMessage}")
            callback(3) // In case of failure, assume all attempts are available
        }
}

// Calculate remaining lock time
fun calculateRemainingLockTime(email: String, callback: (Int) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    db.collection("loginAttempts").document(email).get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                val lastAttemptTime = document.getLong("lastAttemptTime") ?: 0
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastAttemptTime < 15 * 60 * 1000) {
                    val lockDuration = 15 * 60 * 1000 // 15 minutes in milliseconds
                    val remainingTime = ((lastAttemptTime + lockDuration - currentTime) / (1000 * 60)).toInt()
                    callback(remainingTime) // Call the callback with the remaining time
                } else {
                    callback(0) // Not locked, no remaining time
                }
            } else {
                callback(0) // If document does not exist, not locked
            }
        }
        .addOnFailureListener { exception ->
            Log.e("LoginCheck", "Failed to calculate remaining lock time: ${exception.localizedMessage}")
            callback(0) // In case of failure, assume not locked
        }
}

// Function to convert Firebase AuthResult to your SignInResult
fun mapAuthResultToSignInResult(user: FirebaseUser?, isNewUser: Boolean): SignInResult {
    return SignInResult(
        data = user?.let {
            UserData(
                userId = it.uid,
                username = it.displayName,
                profilePictureUrl = it.photoUrl?.toString()
            )

        },
        errorMessage = null,
        isNewUser = isNewUser

    )
}

@Preview(showBackground = true)
@Composable
fun PreviewLogin() {

    LoginPage(
        navController = rememberNavController(),
        userViewModel = MockUserViewModel()
    )
}