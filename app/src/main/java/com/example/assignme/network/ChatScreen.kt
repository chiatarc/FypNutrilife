package com.example.assignme.network

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.assignme.AndroidBar.AppBottomNavigation
import com.example.assignme.AndroidBar.AppTopBar
import com.example.assignme.R
import com.example.assignme.ViewModel.UserViewModel
import com.example.assignme.network.RetrofitClient.BASE_URL
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatbotScreen(navController: NavController, userViewModel: UserViewModel = viewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val userProfile by userViewModel.userProfile.observeAsState()
    var userInput by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf("Rasa: Hi! I'm your personal assistant. How can I help you today?")) }
    var showFAQs by remember { mutableStateOf(true) }
    val scrollState = rememberLazyListState()

    // Check if the system is in dark mode
    val isDarkTheme = isSystemInDarkTheme()

    val faqQuestions = listOf(
        "What functionalities are available in this app?",
        "Can you explain what metabolic syndrome is?",
        "What changes in daily habits benefit metabolic syndrome?",
        "Can metabolic syndrome be reversed?"
    )

    Scaffold(
        topBar = { AppTopBar(title = "Chatbot", navController = navController) },
        bottomBar = { AppBottomNavigation(navController = navController) }
    ) { paddingValues ->
        // Wrap the content in a container that uses the theme background
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background) // Light: white, Dark: black (per your theme)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Hi ${userProfile?.name ?: "User"}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            LaunchedEffect(messages) {
                scrollState.animateScrollToItem(messages.size - 1)
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                state = scrollState
            ) {
                items(messages) { message ->
                    val isUser = message.startsWith("You:")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!isUser) {
                            Icon(
                                imageVector = Icons.Default.Android,
                                contentDescription = "Bot Icon",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .padding(end = 8.dp),
                                tint = Color.Green
                            )
                        }
                        Text(
                            text = message,
                            modifier = Modifier
                                .background(
                                    // For bot messages, use dark gray in dark theme; otherwise, light gray
                                    if (isUser) Color.Blue
                                    else if (isDarkTheme) Color.DarkGray
                                    else Color.LightGray,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(12.dp)
                                .widthIn(max = 250.dp)
                                .padding(end = if (isUser) 8.dp else 0.dp),
                            // For bot messages, if dark theme, use white text for contrast
                            color = if (isUser) Color.White else if (isDarkTheme) Color.White else Color.Black
                        )
                        if (isUser) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Image(
                                painter = if (!userProfile?.profilePictureUrl.isNullOrEmpty()) {
                                    rememberImagePainter(userProfile?.profilePictureUrl)
                                } else {
                                    painterResource(id = R.drawable.google)
                                },
                                contentDescription = "User Profile Picture",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            // Show FAQ buttons if they haven't been clicked
            if (showFAQs) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "FAQ Questions",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Column {
                        for (i in faqQuestions.chunked(2)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                i.forEach { question ->
                                    Button(
                                        onClick = {
                                            userInput = question
                                            showFAQs = false
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(8.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFE23E3E)
                                        )
                                    ) {
                                        Text(
                                            text = question,
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Input field and send button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 45.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    placeholder = { Text("Enter your message", color = MaterialTheme.colorScheme.onSurface) },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFFE23E3E),
                        unfocusedBorderColor = Color(0xFFE23E3E).copy(alpha = 0.5f),
                        cursorColor = Color(0xFFE23E3E)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .border(2.dp, Color(0xFFE23E3E), shape = RoundedCornerShape(8.dp))
                        .padding(4.dp)
                )

                IconButton(onClick = {
                    if (userInput.isNotBlank()) {
                        val userMessage = userInput
                        messages = messages + "You: $userMessage"
                        userInput = ""
                        showFAQs = false

                        coroutineScope.launch {
                            sendMessageToRasa(userMessage) { response ->
                                messages = messages + "Rasa: $response"
                            }
                        }
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = Color(0xFFE23E3E)
                    )
                }
            }
        }
    }
}

fun sendMessageToRasa(userMessage: String, onResponse: (String) -> Unit) {
    val message = Message(sender = "user", message = userMessage)

    // 🚀 Debug: Print URL before sending request
    Log.d("Chatbot", "Sending Message: $userMessage")
    Log.d("Chatbot", "API Base URL: $BASE_URL")
    Log.d("RetrofitClient", "Trying to connect to: $BASE_URL/webhooks/rest/webhook")


    RetrofitClient.instance.sendMessage(message).enqueue(object : Callback<List<ResponseMessage>> {
        override fun onResponse(call: Call<List<ResponseMessage>>, response: Response<List<ResponseMessage>>) {
            if (response.isSuccessful) {
                val botReply = response.body()?.joinToString("\n") { it.text } ?: "Bot didn't reply"
                Log.d("Chatbot", "Received Response: $botReply")
                onResponse(botReply)
            } else {
                Log.e("Chatbot", "Response error: ${response.errorBody()?.string()}")
                onResponse("Error: Couldn't get a response from the server")
            }
        }

        override fun onFailure(call: Call<List<ResponseMessage>>, t: Throwable) {
            Log.e("Chatbot", "API Call Failed: ${t.message}")
            onResponse("Error: No connection to the server")
        }
    })
}
