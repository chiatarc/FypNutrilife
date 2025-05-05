package com.example.assignme.GUI.AccountProfile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageUserAdmin(
    navController: NavController
) {
    val firestore = FirebaseFirestore.getInstance()

    var admins by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var users by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }

    // Real-time listener for Admins
    LaunchedEffect(Unit) {
        firestore.collection("admin").addSnapshotListener { result, _ ->
            result?.let {
                admins = it.documents.mapNotNull { doc ->
                    doc.getString("email")?.let { email ->
                        email to (doc.getString("password") ?: "******")
                    }
                }
            }
        }
    }

    // Real-time listener for Users
    LaunchedEffect(Unit) {
        firestore.collection("users").addSnapshotListener { result, _ ->
            result?.let {
                users = it.documents.mapNotNull { doc ->
                    doc.getString("email")?.let { email ->
                        email to (doc.getString("password") ?: "******")
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Manage Admin & Users") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_admin") },
                modifier = Modifier.padding(16.dp),
                containerColor = Color(0xFFE23E3E) // Red background from your color code
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = "Add Admin/User",
                    tint = Color.White // White icon
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Admin Section Card
            Card(
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Admins",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LazyColumn {
                        items(admins) { (email, _) ->
                            UserRow(email = email, onDelete = {
                                firestore.collection("admin")
                                    .whereEqualTo("email", email)
                                    .get().addOnSuccessListener { result ->
                                        result.documents.forEach { doc ->
                                            deleteDocumentAndSubcollections(doc.reference, listOf("profile_picture"))
                                        }
                                    }
                            })
                        }
                    }
                }
            }

            // User Section Card
            Card(
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Users",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LazyColumn {
                        items(users) { (email, _) ->
                            UserRow(email = email, onDelete = {
                                firestore.collection("users")
                                    .whereEqualTo("email", email)
                                    .get().addOnSuccessListener { result ->
                                        result.documents.forEach { doc ->
                                            doc.reference.delete()
                                        }
                                    }
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserRow(email: String, onDelete: () -> Unit) {
    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            Text(
                text = email,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

// Helper function to delete a document along with its specified subcollections
fun deleteDocumentAndSubcollections(
    docRef: DocumentReference,
    subcollectionNames: List<String>,
    onComplete: (() -> Unit)? = null
) {
    if (subcollectionNames.isEmpty()) {
        docRef.delete().addOnSuccessListener { onComplete?.invoke() }
    } else {
        val deletionTasks = mutableListOf<com.google.android.gms.tasks.Task<Void>>()
        val subcollectionDeletionCount = subcollectionNames.size
        var completedSubcollections = 0

        for (name in subcollectionNames) {
            docRef.collection(name).get().addOnSuccessListener { snapshot ->
                snapshot.documents.forEach { document ->
                    deletionTasks.add(document.reference.delete())
                }
                completedSubcollections++
                if (completedSubcollections == subcollectionDeletionCount) {
                    Tasks.whenAllComplete(deletionTasks).addOnSuccessListener {
                        docRef.delete().addOnSuccessListener { onComplete?.invoke() }
                    }
                }
            }
        }
    }
}
