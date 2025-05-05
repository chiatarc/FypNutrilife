package com.example.assignme.GUI.AccountProfile

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.assignme.AndroidBar.AppTopBar
import com.example.assignme.DataClass.WindowInfo
import com.example.assignme.DataClass.rememberWidowInfo
import com.example.assignme.GUI.Community.VideoPlayer
import com.example.assignme.ViewModel.Post
import com.example.assignme.ViewModel.UserProfile
import com.example.assignme.ViewModel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageReportPostScreen(navController: NavController, userViewModel: UserViewModel) {
    val reportedPosts by userViewModel.reportedPosts.observeAsState(emptyList())
    val users by userViewModel.users.observeAsState(emptyMap())

    // Get window size class
    val windowInfo = rememberWidowInfo()
    val columns = when (windowInfo.screenWidthInfo) {
        WindowInfo.WindowType.Compact -> 1
        WindowInfo.WindowType.Medium -> 2
        WindowInfo.WindowType.Expanded -> 2
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Manage Report") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (reportedPosts.isEmpty()) {
            Text("No reported posts", modifier = Modifier.padding(16.dp))
        } else {
            // Use LazyVerticalGrid to display reported posts in grid layout
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp), // Adjust padding for grid spacing
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(reportedPosts) { post ->
                    ReportedPostItem(post, userViewModel, users)
                }
            }
        }
    }
}

@Composable
fun ReportedPostItem(post: Post, userViewModel: UserViewModel, users: Map<String, UserProfile>) {
    val reportedUser = users[post.userId]
    val reportedByUser = users[post.reportedBy ?: ""]

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Reported user info
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = rememberImagePainter(reportedUser?.profilePictureUrl),
                    contentDescription = "Reported user profile picture",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reported user: ${reportedUser?.name ?: "Unknown"}")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Post content
            Text(post.content)

            // Post image
            post.imagePath?.let { imagePath ->
                Image(
                    painter = rememberImagePainter(imagePath),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .padding(vertical = 8.dp),
                    contentScale = ContentScale.Crop
                )
            }

            // Post Video (if available)
            post.videoPath?.let { videoUrl ->
                Spacer(modifier = Modifier.height(8.dp))
                VideoPlayer(
                    videoUri = Uri.parse(videoUrl),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Report reason and reporter info
            Text("Reported by: ${reportedByUser?.name ?: "Unknown"}")
            Text("Report Reason: ${post.reportReason?.substringAfter(":") ?: "No reason provided"}")

            Spacer(modifier = Modifier.height(8.dp))

            // Post stats
            Text("Likes: ${post.likes}")
            Text("Comments: ${post.comments}")

            Spacer(modifier = Modifier.height(8.dp))

            // Action buttons
            Row {
                Button(
                    onClick = { userViewModel.deletePost(post.id) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE23E3E)),
                ) {
                    Text("Delete Post")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { userViewModel.ignoreReport(post.id) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE23E3E)),
                ) {
                    Text("Ignore Report")
                }
            }
        }
    }
}


