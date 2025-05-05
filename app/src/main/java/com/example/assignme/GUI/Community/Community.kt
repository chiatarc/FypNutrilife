package com.example.assignme.GUI.Community

import android.net.Uri
import android.widget.FrameLayout
import android.widget.ListPopupWindow.MATCH_PARENT
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.assignme.AndroidBar.AppBottomNavigation
import com.example.assignme.AndroidBar.AppTopBar
import com.example.assignme.DataClass.WindowInfo
import com.example.assignme.DataClass.rememberWidowInfo
import com.example.assignme.R
import com.example.assignme.ViewModel.Comment
import com.example.assignme.ViewModel.UserProfile
import com.example.assignme.ViewModel.UserViewModel
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.MediaItem
import kotlinx.coroutines.delay

@Composable
fun SocialAppUI(navController: NavController, userViewModel: UserViewModel) {
    val windowInfo = rememberWidowInfo()
    val userProfile by userViewModel.userProfile.observeAsState(UserProfile())
    val userName = userProfile.name ?: "User"
    var selectedTab by remember { mutableStateOf(0) }
    val profilePictureUrl = userProfile.profilePictureUrl

    Scaffold(
        topBar = { AppTopBar(title = "Social Media", navController = navController) },
        bottomBar = { AppBottomNavigation(navController) }
    ) { innerPadding ->
        when (windowInfo.screenWidthInfo) {
            is WindowInfo.WindowType.Compact -> CompactLayout(
                innerPadding = innerPadding,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                userViewModel = userViewModel,
                userName = userName,
                profilePictureUrl = profilePictureUrl
            )
            is WindowInfo.WindowType.Medium -> MediumLayout(
                innerPadding = innerPadding,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                userViewModel = userViewModel,
                userName = userName,
                profilePictureUrl = profilePictureUrl
            )
            is WindowInfo.WindowType.Expanded -> ExpandedLayout(
                innerPadding = innerPadding,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                userViewModel = userViewModel,
                userName = userName,
                profilePictureUrl = profilePictureUrl
            )
        }
    }
}

@Composable
fun SearchBar(
    query: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
    onSearch: (TextFieldValue) -> Unit,
    onClose: () -> Unit
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search users...") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp), // 将 SearchBar 下移 16dp
        singleLine = true,
        leadingIcon = {
            IconButton(onClick = { onSearch(query) }) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
        },
        trailingIcon = {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        }
    )
}

@Composable
fun TabRowContent(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
) {
    TabRow(
        selectedTabIndex = selectedTab,
//        backgroundColor = backgroundColor,
//        contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.height(50.dp)
    ) {
        Tab(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            text = { Text("Community") }
        )
        Tab(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            text = { Text("My Posts") }
        )
    }
}

@Composable
fun CompactLayout(
    innerPadding: PaddingValues,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    userViewModel: UserViewModel,
    userName: String,
    profilePictureUrl: String?
) {
    Column(modifier = Modifier.padding(innerPadding)
//        .background(MaterialTheme.colors.background)
    ) {
        TabRowContent(selectedTab, onTabSelected)
        SearchSection(userName, userViewModel, profilePictureUrl)
        PostComposer(userViewModel)
        if (selectedTab == 0) {
            PostList(userViewModel)
        } else {
            MyPostList(userViewModel)
        }
    }
}

@Composable
fun MediumLayout(
    innerPadding: PaddingValues,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    userViewModel: UserViewModel,
    userName: String,
    profilePictureUrl: String?
) {
    Column(modifier = Modifier.padding(innerPadding)) {
        TabRowContent(selectedTab, onTabSelected)
        SearchSection(userName, userViewModel, profilePictureUrl)
        Row {
            Column(modifier = Modifier.weight(1f)) {
                PostComposer(userViewModel)
                if (selectedTab == 0) {
                    PostList(userViewModel)
                } else {
                    MyPostList(userViewModel)
                }
            }
            Column(modifier = Modifier.width(200.dp)) {
                // Additional content or controls can be added here
            }
        }
    }
}

@Composable
fun ExpandedLayout(
    innerPadding: PaddingValues,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    userViewModel: UserViewModel,
    userName: String,
    profilePictureUrl: String?
) {
    Column(modifier = Modifier.padding(innerPadding)) {
        TabRowContent(selectedTab, onTabSelected)
        SearchSection(userName, userViewModel, profilePictureUrl)
        Row {
            Column(modifier = Modifier.weight(1f)) {
                PostComposer(userViewModel)
                PostList(userViewModel)
            }
            Column(modifier = Modifier.weight(1f)) {
                MyPostList(userViewModel)
            }
        }
    }
}

@Composable
fun PostComposer(userViewModel: UserViewModel) {
    var selectedMediaUri by remember { mutableStateOf<Uri?>(null) }
    var postText by remember { mutableStateOf("") }
    var mediaType by remember { mutableStateOf<String?>(null) }
    var showMessage by remember { mutableStateOf(false) } // 用于显示消息
    val userProfile by userViewModel.userProfile.observeAsState(UserProfile())
    val profilePictureUrl = userProfile.profilePictureUrl


    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            selectedMediaUri = uri
            mediaType = "image"
        }
    )

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            selectedMediaUri = uri
            mediaType = "video"
        }
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = postText,
                onValueChange = { postText = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                placeholder = { Text("What's on your mind?") }
            )

            IconButton(onClick = { imagePickerLauncher.launch("image/*") }) {
                Icon(
                    painter = painterResource(id = R.drawable.addimage),
                    contentDescription = "Add image",
                    modifier = Modifier.size(30.dp)
                )
            }

            IconButton(onClick = { videoPickerLauncher.launch("video/*") }) {
                Icon(
                    painter = painterResource(id = R.drawable.addvideo),
                    contentDescription = "Add video",
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        if (selectedMediaUri != null) {
            when (mediaType) {
                "image" -> Image(
                    painter = rememberImagePainter(selectedMediaUri),
                    contentDescription = "Selected image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(top = 8.dp)
                        .background(Color.Gray),
                    contentScale = ContentScale.Crop
                )
                "video" -> VideoPlayer(
                    videoUri = selectedMediaUri!!,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(top = 8.dp)
                )
            }
        }

        Button(
            onClick = {
                selectedMediaUri?.let { uri ->
                    userViewModel.addPost(postText, uri, mediaType ?: "")
                    postText = ""
                    selectedMediaUri = null
                    mediaType = null
                    showMessage = true
                } ?: run {
                    userViewModel.addPost(postText, null, "")
                    postText = ""
                    showMessage = true
                }
            },
            enabled = postText.isNotEmpty(),
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 8.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFFE23E3E),
                disabledBackgroundColor = Color(0xFFEA5959),
                contentColor = Color.White,
                disabledContentColor = Color.White.copy(alpha = 0.7f)
            )
        ) {
            Text("Post")
        }
    }
    val context = LocalContext.current
    if (showMessage) {
        LaunchedEffect(showMessage) {
            Toast.makeText(context, "Post successfully!", Toast.LENGTH_SHORT).show()
            delay(2000) // 2秒后隐藏消息
            showMessage = false // 重置状态
        }
    }
}

@Composable
fun VideoPlayer(videoUri: Uri, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val exoPlayer = remember {
        SimpleExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(videoUri)
            setMediaItem(mediaItem)
            prepare()
        }
    }

    // Display the player view using AndroidView
    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            }
        },
        modifier = modifier
    )

    // Use DisposableEffect to release the player when the composable leaves the composition
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
}

@Composable
fun PostItem(
    authorName: String,
    authorImageUrl: String,
    content: String,
    likes: Int,
    comments: Int,
    postId: String,
    userViewModel: UserViewModel,
    imagePath: String? = null,
    videoPath: String? = null,
    mediaType: String? = null,
    likedUsers: List<String>,
    timestamp: Long,
    isMyPost: Boolean
) {
    val context = LocalContext.current
    val currentUserId = userViewModel.userId.value ?: ""
    var liked by remember { mutableStateOf(likedUsers.contains(currentUserId)) }
    var currentLikes by remember { mutableStateOf(likes) }
    var showCommentsDialog by remember { mutableStateOf(false) }
    var currentComments by remember { mutableStateOf(comments) }
    var showImageDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var editedContent by remember { mutableStateOf(content) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var showFullscreenVideo by remember { mutableStateOf(false) }
    var showReportSuccessMessage by remember { mutableStateOf(false) }
    var showEditSuccessMessage by remember { mutableStateOf(false) }
    var showDeleteSuccessMessage by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Row for Author and Post options (edit, delete, report)
            Row {
                Image(
                    painter = rememberImagePainter(authorImageUrl),
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, androidx.compose.material3.MaterialTheme.colorScheme.primary, CircleShape)
                )
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text(
                        text = authorName,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = userViewModel.formatTimestamp(timestamp),
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Box {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(
                            painter = painterResource(id = R.drawable.dot),
                            contentDescription = "More Options",
                            modifier = Modifier.size(25.dp)
                        )
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        if (isMyPost) {
                            DropdownMenuItem(onClick = {
                                showMenu = false
                                showEditDialog = true
                            }) {
                                Text("Edit")
                            }
                            DropdownMenuItem(onClick = {
                                showMenu = false
                                userViewModel.deletePost(postId)
                                showDeleteSuccessMessage = true
                            }) {
                                Text("Delete")
                            }
                        } else {
                            DropdownMenuItem(onClick = {
                                showMenu = false
                                showReportDialog = true
                            }) {
                                Text("Report")
                            }
                        }
                    }
                }
            }
            if (showDeleteSuccessMessage) {
                LaunchedEffect(showDeleteSuccessMessage) {
                    Toast.makeText(context, "Delete successfully!", Toast.LENGTH_SHORT).show()
                    delay(2000)
                    showDeleteSuccessMessage = false
                }
            }
            // Post content
            Text(text = content, modifier = Modifier.padding(vertical = 8.dp))

            // Post Media (Image or Video)
            when (mediaType) {
                "image" -> imagePath?.let { imageUrl ->
                    Image(
                        painter = rememberImagePainter(data = imageUrl),
                        contentDescription = "Post image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clickable { showImageDialog = true },
                        contentScale = ContentScale.Crop
                    )
                }
                "video" -> videoPath?.let { videoUrl ->
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                        VideoPlayer(
                            videoUri = Uri.parse(videoUrl),
                            modifier = Modifier.fillMaxSize()
                        )
                        IconButton(
                            onClick = { showFullscreenVideo = true },
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.fullscreen), // Replace with your drawable name
                                contentDescription = "Fullscreen",
                                modifier = Modifier.size(24.dp)
                                    .graphicsLayer(alpha = 0.5f), // Adjust size as needed
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            // Like and comment buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = {
                    liked = !liked
                    currentLikes = if (liked) currentLikes + 1 else currentLikes - 1
                    userViewModel.toggleLike(postId, liked)
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.like),
                        contentDescription = "Like",
                        tint = if (liked) Color.Blue else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    "$currentLikes likes",
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .align(Alignment.CenterVertically)
                )

                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = { showCommentsDialog = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_comment),
                        contentDescription = "Comment",
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    "$currentComments comments",
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .align(Alignment.CenterVertically)
                )
            }
        }
    }

    if (showFullscreenVideo && videoPath != null) {
        FullscreenVideoDialog(
            videoUri = Uri.parse(videoPath),
            onDismiss = { showFullscreenVideo = false }
        )
    }

    // Image Dialog
    if (showImageDialog && imagePath != null) {
        ImageDialog(
            imageUrl = imagePath,
            onDismiss = { showImageDialog = false }
        )
    }

    // Comments Dialog
    if (showCommentsDialog) {
        CommentsDialog(
            onDismiss = { showCommentsDialog = false },
            postId = postId,
            userViewModel = userViewModel,
            currentUserName = userViewModel.userProfile.value?.name ?: "Anonymous",
            onCommentAdded = {
                currentComments++
            }
        )
    }

    // Edit Post Dialog
    if (showEditDialog) {
        EditPostDialog(
            currentContent = editedContent,
            onDismiss = {
                showEditDialog = false
                editedContent = content
            },
            onConfirm = { newContent ->
                editedContent = newContent
                userViewModel.updatePost(postId, newContent)
                showEditDialog = false
                showEditSuccessMessage = true
            }
        )
    }

    if (showEditSuccessMessage) {
        LaunchedEffect(showEditSuccessMessage) {
            Toast.makeText(context, "Edit successfully!", Toast.LENGTH_SHORT).show()
            delay(2000)
            showEditSuccessMessage = false
        }
    }

    if (showReportDialog) {
        ReportDialog(
            onDismiss = { showReportDialog = false },
            onReport = { reason, reportedBy ->
                userViewModel.reportPost(postId, reason, reportedBy)
                showReportDialog = false
                showReportSuccessMessage = true
            },
            reportedBy = currentUserId
        )
    }

    if (showReportSuccessMessage) {
        LaunchedEffect(showReportSuccessMessage) {
            Toast.makeText(context, "Report successfully!", Toast.LENGTH_SHORT).show()
            delay(2000)
            showReportSuccessMessage = false
        }
    }
}

@Composable
fun FullscreenVideoDialog(videoUri: Uri, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Box(modifier = Modifier.fillMaxSize()) {
            VideoPlayer(videoUri = videoUri, modifier = Modifier.fillMaxSize())
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
            }
        }
    }
}

@Composable
fun ReportDialog(
    onDismiss: () -> Unit,
    onReport: (String, String) -> Unit,
    reportedBy: String
) {
    var reportReason by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Report Post")
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = reportReason,
                    onValueChange = { reportReason = it },
                    label = { Text("Reason for reporting") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onReport(reportReason, reportedBy)
                        },
                        enabled = reportReason.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFFE23E3E),
                            disabledBackgroundColor = Color(0xFFEA5959),
                            contentColor = Color.White,
                            disabledContentColor = Color.White.copy(alpha = 0.7f)
                        )
                    ) {
                        Text("Submit Report")
                    }
                }
            }
        }
    }
}

@Composable
fun ImageDialog(imageUrl: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        val imagePainter = rememberImagePainter(data = imageUrl)

        Box(
            modifier = Modifier
                .wrapContentSize()
                .padding(16.dp)
        ) {
            Image(
                painter = imagePainter,
                contentDescription = "Enlarged post image",
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .scale(1.25f),
                contentScale = ContentScale.Fit
            )

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.LightGray
                )
            }
        }
    }
}

@Composable
fun EditPostDialog(currentContent: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var newContent by remember { mutableStateOf(currentContent) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Edit Post")
                TextField(
                    value = newContent,
                    onValueChange = { newContent = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Update your post...") }
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    TextButton(onClick = {
                        onConfirm(newContent)
                    }) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}

@Composable
fun CommentsDialog(
    onDismiss: () -> Unit,
    postId: String,
    userViewModel: UserViewModel,
    currentUserName: String,
    onCommentAdded: () -> Unit
) {
    var commentText by remember { mutableStateOf("") }
    var showToast by remember { mutableStateOf(false) }
    val comments by userViewModel.getCommentsForPost(postId).observeAsState(emptyList())

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(1.0f)
                .fillMaxHeight(0.8f)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Comments")
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(comments) { comment ->
                        val profileImageUrl =
                            comment.userProfileImage.ifEmpty { R.drawable.profile.toString() }

                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = rememberImagePainter(profileImageUrl),
                                    contentDescription = "Profile picture",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                )
                                Text(
                                    text = comment.userName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                            Text(
                                text = comment.content,
                                modifier = Modifier.padding(start = 48.dp, top = 4.dp)
                            )
                            val formattedDate = userViewModel.formatDate(comment.timestamp)
                            Text(
                                text = formattedDate,
                                color = Color.LightGray,
                                modifier = Modifier.padding(start = 48.dp, top = 4.dp)
                            )
                            val formattedTime = userViewModel.formatTime(comment.timestamp)
                            Text(
                                text = formattedTime,
                                color = Color.LightGray,
                                modifier = Modifier.padding(start = 48.dp, top = 4.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    TextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        label = { Text("Write a comment") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val newComment = Comment(
                                userName = currentUserName,
                                content = commentText,
                                timestamp = System.currentTimeMillis(),
                                userProfileImage = userViewModel.userProfile.value?.profilePictureUrl
                                    ?: ""
                            )
                            userViewModel.addComment(postId, newComment)
                            onCommentAdded()
                            commentText = ""
                            showToast = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFFE23E3E),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Send")
                    }
                }
            }
        }
    }
    val context = LocalContext.current
    if (showToast) {
        LaunchedEffect(showToast) {
            Toast.makeText(context, "Comment successfully!", Toast.LENGTH_SHORT).show()
            delay(2000)
            showToast = false
        }
    }
}

@Composable
fun PostList(userViewModel: UserViewModel) {
    val posts by userViewModel.posts.observeAsState(emptyList())
    val users by userViewModel.users.observeAsState(emptyMap())
    val searchResults by userViewModel.searchResults.observeAsState(emptyList())

    LazyColumn {
        if (searchResults.isNotEmpty()) {
            items(searchResults, key = { it.id }) { post ->
                val author = users[post.userId]
                val authorName = author?.name ?: "User"
                val authorImageUrl = author?.profilePictureUrl ?: R.drawable.profile.toString()

                PostItem(
                    authorName = authorName,
                    authorImageUrl = authorImageUrl,
                    content = post.content,
                    imagePath = post.imagePath,
                    videoPath = post.videoPath,
                    mediaType = post.mediaType,
                    likes = post.likes,
                    comments = post.comments,
                    postId = post.id,
                    userViewModel = userViewModel,
                    likedUsers = post.likedUsers,
                    timestamp = post.timestamp,
                    isMyPost = false
                )
            }
        } else {
            items(posts.reversed(), key = { it.id }) { post ->
                val author = users[post.userId]
                val authorName = author?.name ?: "User"
                val authorImageUrl = author?.profilePictureUrl ?: R.drawable.profile.toString()

                PostItem(
                    authorName = authorName,
                    authorImageUrl = authorImageUrl,
                    content = post.content,
                    imagePath = post.imagePath,
                    videoPath = post.videoPath,
                    mediaType = post.mediaType,
                    likes = post.likes,
                    comments = post.comments,
                    postId = post.id,
                    userViewModel = userViewModel,
                    likedUsers = post.likedUsers,
                    timestamp = post.timestamp,
                    isMyPost = false
                )
            }
        }
    }
}

@Composable
fun MyPostList(userViewModel: UserViewModel) {
    val posts by userViewModel.posts.observeAsState(emptyList())
    val users by userViewModel.users.observeAsState(emptyMap())

    val myPosts = posts.filter { it.userId == userViewModel.userId.value }

    LazyColumn {
        if (myPosts.isEmpty()) {
            item {
                Text("No posts available", modifier = Modifier.padding(16.dp))
            }
        } else {
            items(myPosts.reversed(), key = { it.id }) { post ->
                val author = users[post.userId]
                val authorName = author?.name ?: "User"
                val authorImageUrl = author?.profilePictureUrl ?: R.drawable.profile.toString()

                PostItem(
                    authorName = authorName,
                    authorImageUrl = authorImageUrl,
                    content = post.content,
                    imagePath = post.imagePath,
                    videoPath = post.videoPath,
                    mediaType = post.mediaType,
                    likes = post.likes,
                    comments = post.comments,
                    postId = post.id,
                    userViewModel = userViewModel,
                    likedUsers = post.likedUsers,
                    timestamp = post.timestamp,
                    isMyPost = true
                )
            }
        }
    }
}

@Composable
fun SearchSection(
    userName: String,
    userViewModel: UserViewModel,
    profilePictureUrl: String? // Pass the profile picture URL as a parameter
) {
    var searchQuery by remember { mutableStateOf(TextFieldValue()) }
    var isSearching by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        if (isSearching) {
            SearchBar(
                query = searchQuery,
                onQueryChange = {
                    searchQuery = it
                    if (it.text.isEmpty()) {
                        userViewModel.clearSearchResults()
                    }
                },
                onSearch = {
                    userViewModel.searchUsers(it.text)
                    isSearching = false
                },
                onClose = {
                    isSearching = false
                    searchQuery = TextFieldValue()
                    userViewModel.clearSearchResults()
                }
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) { // Wrap image + text in a row
                    Image(
                        painter = rememberImagePainter(profilePictureUrl ?: R.drawable.profile),
                        contentDescription = "Profile picture",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp)) // Add some space between image and text
                    Text("$userName", style = MaterialTheme.typography.h6)
                }
                IconButton(onClick = { isSearching = true }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            }
        }
    }
}



