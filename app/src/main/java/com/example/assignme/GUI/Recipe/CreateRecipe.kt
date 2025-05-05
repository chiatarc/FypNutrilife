package com.example.assignme.GUI.Recipe

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.assignme.AndroidBar.AppBottomNavigation
import com.example.assignme.AndroidBar.AppTopBar
import com.example.assignme.DataClass.Ingredient
import com.example.assignme.DataClass.Recipes
import com.example.assignme.DataClass.fetchNutritionForIngredient
import com.example.assignme.GUI.Recipe.ui.theme.Orange
import com.example.assignme.R
import com.example.assignme.ViewModel.RecipeViewModel
import com.example.assignme.ViewModel.UserViewModel
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRecipe(
    navController: NavController,
    viewModel: RecipeViewModel = viewModel(),
    userModel: UserViewModel,
    onBackClick: () -> Unit,
) {
    val context = LocalContext.current
    var hasCameraPermission by remember { mutableStateOf(false) }
    // State variables for the recipe fields
    var recipeTitle by remember { mutableStateOf("") }
    var serves by remember { mutableStateOf(1) }
    var cookTime by remember { mutableStateOf(45) }
    var ingredients by remember { mutableStateOf(listOf(Ingredient())) }
    var description by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf(listOf("")) }  // Step-by-step instructions
    var selectedCategory by remember { mutableStateOf("Lunch") } // Default category
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val categories = listOf("Breakfast", "Lunch", "Dinner", "Snack") // Category options

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> imageUri = uri }
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bitmap ->
            if (bitmap != null) {
                // Save the bitmap to internal storage and update the imageUri
                val savedUri = saveImageToInternalStorage(context, bitmap)
                imageUri = savedUri // Set the imageUri to the saved image location
            } else {
                Log.e("CameraError", "Failed to capture image")
            }
        }
    )

    LaunchedEffect(Unit) {
        hasCameraPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    Scaffold(
        topBar = { AppTopBar(title = "Create Recipe", navController = navController) },
        bottomBar = { AppBottomNavigation(navController = navController) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Image Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = "Recipe Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        "Upload or Take Photo",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(
                    onClick = { showDialog = true },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(40.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Image",
                        tint = Orange
                    )
                }
            }

            if (showDialog) {
                showImageSourceDialog(
                    showDialog = showDialog,
                    onDismiss = { showDialog = false },
                    onPickFromGallery = { imagePickerLauncher.launch("image/*") },
                    onTakePhoto = {
                        if (hasCameraPermission) {
                            cameraLauncher.launch(null)
                        } else {
                            // Request camera permission if not granted
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
                )
            }

            // Recipe Title and Description
            OutlinedTextField(
                value = recipeTitle,
                onValueChange = { recipeTitle = it },
                label = { Text("Recipe Title", color = MaterialTheme.colorScheme.onSurface) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Orange,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    cursorColor = Orange,
                    focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    focusedTextColor = MaterialTheme.colorScheme.onSurface, // Use focusedTextColor for text color
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface, // Use unfocusedTextColor for text color when not focused
                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), // Placeholder color when focused
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) // Placeholder color when not focused
                )
            )


            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description", color = MaterialTheme.colorScheme.onSurface) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Orange,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface, // Use focusedTextColor for text color
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    cursorColor = Orange,
                    focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), // Placeholder color when focused
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            )

            // Category Selector
            Text(
                "Category",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            CategorySelector(
                selectedCategory = selectedCategory,
                categories = categories,
                onCategorySelected = { selectedCategory = it }
            )

            // Serves and Cook time section
            ServesItem(serves = serves, onServeChange = { serves = it })
            CookTimeItem(cookTime = cookTime, onCookTimeChange = { cookTime = it })

            // Ingredients Section
            Text(
                "Ingredients",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Gray
            )
            ingredients.forEachIndexed { index, ingredient ->
                IngredientItem(
                    name = ingredient.name,
                    quantity = ingredient.amount,
                    onNameChange = { newName ->
                        ingredients = ingredients.toMutableList().also {
                            it[index] = it[index].copy(name = newName)
                        }
                    },
                    onQuantityChange = { newQuantity ->
                        ingredients = ingredients.toMutableList().also {
                            it[index] = it[index].copy(amount = newQuantity)
                        }
                    },
                    onRemove = {
                        ingredients = ingredients.toMutableList().also {
                            it.removeAt(index)
                        }
                    }
                )
            }

            val context = LocalContext.current

            AddNewIngredientButton {
                Log.d("IngredientDebug", "Add button pressed")
                if (ingredients.none { it.name.isBlank() && it.amount.isBlank() }) {
                    ingredients = ingredients + Ingredient("", "", 0.0)
                } else {
                    Toast.makeText(context, "Please fill in the current empty ingredient first", Toast.LENGTH_SHORT).show()
                }
            }


            // Instructions Section
            Text(
                "Instructions",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            instructions.forEachIndexed { index, instruction ->
                InstructionItem(
                    instruction = instruction,
                    onInstructionChange = { newInstruction ->
                        instructions = instructions.toMutableList().also {
                            it[index] = newInstruction
                        }
                    },
                    onRemove = {
                        instructions = instructions.toMutableList().also {
                            it.removeAt(index)
                        }
                    }
                )
            }

            AddNewInstructionButton {
                instructions = instructions + ""
            }

            // Save Button
            Button(
                onClick = {
                    // Perform validation and show Toast messages if any field is missing
                    when {
                        recipeTitle.isBlank() -> {
                            Toast.makeText(context, "Recipe title cannot be empty", Toast.LENGTH_SHORT).show()
                        }
                        description.isBlank() -> {
                            Toast.makeText(context, "Description cannot be empty", Toast.LENGTH_SHORT).show()
                        }
                        ingredients.isEmpty() || ingredients.any { it.name.isBlank() } -> {
                            Toast.makeText(context, "Please add at least one ingredient", Toast.LENGTH_SHORT).show()
                        }
                        instructions.isEmpty() || instructions.any { it.isBlank() } -> {
                            Toast.makeText(context, "Please add at least one instruction", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            // Save the recipe if validation passes
                            saveRecipe(
                                context = context,
                                viewModel = viewModel,
                                recipeViewModel = userModel,
                                recipeTitle = recipeTitle,
                                description = description,
                                ingredients = ingredients,
                                cookTime = "$cookTime min",
                                servings = serves,
                                selectedCategory = selectedCategory,
                                instructions = instructions,
                                imageUri = imageUri
                            ) { success ->
                                if (success) {
                                    navController.popBackStack() // Navigate back on success
                                } else {
                                    Toast.makeText(context, "Failed to save the recipe", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Orange)
            ) {
                Text("Save my recipe", color = Color.White)
            }

            Spacer(modifier = Modifier.height(25.dp))
        }
    }
}

fun saveRecipe(
    context: Context,
    viewModel: RecipeViewModel,
    recipeViewModel: UserViewModel,
    recipeTitle: String,
    description: String,
    ingredients: List<Ingredient>,
    cookTime: String,
    servings: Int,
    selectedCategory: String,
    instructions: List<String>,
    imageUri: Uri?,
    onRecipeSaved: (Boolean) -> Unit
)   {
    // Initialize accumulators for each nutrient
    var totalCalories = 0.0
    var totalProtein = 0.0
    var totalFat = 0.0
    var totalSaturatedFat = 0.0
    var totalCarbs = 0.0
    var totalFiber = 0.0
    var totalSugar = 0.0
    var totalSodium = 0.0
    var totalPotassium = 0.0
    var totalCholesterol = 0.0

    val enrichedIngredients = mutableListOf<Ingredient>()

    ingredients.forEach { ingredient ->
        fetchNutritionForIngredient(ingredient.name) { foodItem ->
            val amountInGrams = ingredient.amount.toDoubleOrNull() ?: 0.0
            val scale = amountInGrams / foodItem.serving_size_g

            val enriched = Ingredient(
                name = foodItem.name,
                amount = "$amountInGrams g",
                calories = foodItem.calories * scale,
                protein = foodItem.protein_g * scale,
                fat = foodItem.fat_total_g * scale,
                saturatedFat = foodItem.fat_saturated_g * scale,
                carbs = foodItem.carbohydrates_total_g * scale,
                fiber = foodItem.fiber_g * scale,
                sugar = foodItem.sugar_g * scale,
                sodium = foodItem.sodium_mg * scale,
                potassium = foodItem.potassium_mg * scale,
                cholesterol = foodItem.cholesterol_mg * scale
            )

            enrichedIngredients.add(enriched)

            // Accumulate totals from this ingredient
            totalCalories += enriched.calories
            totalProtein += enriched.protein
            totalFat += enriched.fat
            totalSaturatedFat += enriched.saturatedFat
            totalCarbs += enriched.carbs
            totalFiber += enriched.fiber
            totalSugar += enriched.sugar
            totalSodium += enriched.sodium
            totalPotassium += enriched.potassium
            totalCholesterol += enriched.cholesterol

            // When all ingredients have been processed, create and save the recipe
            if (enrichedIngredients.size == ingredients.size) {
                val createAndSave = { imageUrl: String ->
                    val recipe = Recipes(
                        id = UUID.randomUUID().toString(),
                        title = recipeTitle,
                        description = description,
                        ingredients = enrichedIngredients,
                        cookTime = cookTime,
                        servings = servings,
                        totalCalories = totalCalories.toInt(),
                        totalProtein = totalProtein.toInt(),
                        totalFat = totalFat.toInt(),
                        totalSaturatedFat = totalSaturatedFat.toInt(),
                        totalCarbs = totalCarbs.toInt(),
                        totalFiber = totalFiber.toInt(),
                        totalSugar = totalSugar.toInt(),
                        totalSodium = totalSodium.toInt(),
                        totalPotassium = totalPotassium.toInt(),
                        totalCholesterol = totalCholesterol.toInt(),
                        authorId = recipeViewModel.userId.value.orEmpty(),
                        imageUrl = imageUrl,
                        category = selectedCategory,
                        instructions = instructions
                    )

                    viewModel.addRecipe(
                        recipe,
                        onSuccess = {
                            Toast.makeText(context, "Recipe saved!", Toast.LENGTH_SHORT).show()
                            onRecipeSaved(true)
                        },
                        onFailure = {
                            Toast.makeText(context, "Failed to save recipe.", Toast.LENGTH_SHORT).show()
                            onRecipeSaved(false)
                        }
                    )
                }

                // Upload image first, then save the recipe
                if (imageUri != null) {
                    uploadImageToStorage(
                        context = context,
                        imageUri = imageUri,
                        onSuccess = { imageUrl -> createAndSave(imageUrl) },
                        onFailure = {
                            Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show()
                            onRecipeSaved(false)
                        }
                    )
                } else {
                    Toast.makeText(context, "Image is required", Toast.LENGTH_SHORT).show()
                    onRecipeSaved(false)
                }
            }
        }
    }
}


fun compressBitmap(bitmap: Bitmap, quality: Int = 75): ByteArray {
    val byteArrayOutputStream = ByteArrayOutputStream()
    // Compress the bitmap to JPEG format with the specified quality (0-100)
    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
    return byteArrayOutputStream.toByteArray()
}

fun uploadImageToStorage(context: Context, imageUri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
    // First, get the bitmap from the imageUri
    val bitmap = getBitmapFromUri(context, imageUri)

    // Compress the bitmap
    if (bitmap != null) {
        val compressedImage = compressBitmap(bitmap)

        // Firebase Storage reference
        val storageReference = FirebaseStorage.getInstance().reference.child("recipeImg/${UUID.randomUUID()}.jpg")

        // Upload the compressed image as a ByteArray
        val uploadTask = storageReference.putBytes(compressedImage)

        uploadTask.addOnSuccessListener {
            // Get the image URL from Firebase
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                onSuccess(uri.toString())  // Return the download URL as a String
            }
        }.addOnFailureListener { exception ->
            onFailure(exception)  // Handle failure by passing the exception
        }
    } else {
        // Handle error if bitmap retrieval fails
        onFailure(Exception("Failed to retrieve Bitmap from Uri"))
    }
}

fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        null
    }
}

@Composable
fun CategorySelector(
    selectedCategory: String,
    categories: List<String>,
    onCategorySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    // Ensure that the DropdownMenu width matches the width of the OutlinedButton
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = selectedCategory,
                modifier = Modifier.weight(1f),
                color = Color.Gray
                 // Set the text color to orange
            )
            Icon(Icons.Filled.ArrowDropDown, contentDescription = null, tint=Orange)
        }

        // DropdownMenu with the same width as the OutlinedButton
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()// Ensure the dropdown width matches the button width
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(text = category) },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}

// Instruction Item Composable (For each step)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstructionItem(
    instruction: String,
    onInstructionChange: (String) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = instruction,
            onValueChange = onInstructionChange,
            modifier = Modifier.weight(1f),
            label = { Text("Instruction") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFFE23E3E), // Set the focused border color to orange
                unfocusedBorderColor = Color.Gray, // Unfocused border color
                focusedLabelColor = Color(0xFFE23E3E), // Set the label color to orange when focused
                cursorColor = Color(0xFFE23E3E) // Set the cursor color to orange
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = onRemove) {
            Icon(
                painter = painterResource(id = R.drawable.remove),
                contentDescription = "Remove instruction",
                tint = Orange,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

// Add New Instruction Button
@Composable
fun AddNewInstructionButton(onAdd: () -> Unit) {
    TextButton(onClick = onAdd, modifier = Modifier.fillMaxWidth()) {
        Icon(
            painter = painterResource(id = R.drawable.addbtn),
            contentDescription = "Add new instruction",
            tint = Orange,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Add new Instruction", color = Orange)
    }
}

// Save bitmap image to internal storage and return the URI
fun saveImageToInternalStorage(context: Context, bitmap: Bitmap): Uri {
    val filename = "recipe_${UUID.randomUUID()}.jpg"
    val file = File(context.filesDir, filename)
    val fos = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
    fos.flush()
    fos.close()
    return Uri.fromFile(file)
}

@SuppressLint("DefaultLocale")
@Composable
fun ServesItem(serves: Int, onServeChange: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF424242) // Light gray background
        )// Light gray background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // Padding inside the card
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.serve), // Serves icon
                    contentDescription = "Serves",
                    tint = Color(0xFFE23E3E), // Your red tint color
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Serves", fontWeight = FontWeight.Medium, color = Color.White)
            }

            // Decrease Button, Serve Count, and Increase Button
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    if (serves > 1) onServeChange(serves - 1) // Prevent going below 1
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_left),
                        contentDescription = "Decrease serves",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = String.format("%02d", serves), // Show number in two digits (e.g., 01)
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                IconButton(onClick = {
                    onServeChange(serves + 1) // Increase serves
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_right),
                        contentDescription = "Increase serves",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CookTimeItem(cookTime: Int, onCookTimeChange: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF424242) // Light gray background
        ) // Light gray background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // Padding inside the card
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.clock), // Clock icon for cook time
                    contentDescription = "Cook time",
                    tint = Color(0xFFE23E3E), // Your red tint color
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cook time", fontWeight = FontWeight.Medium, color = Color.White)
            }

            // Decrease Button, Cook Time, and Increase Button
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    if (cookTime > 1) onCookTimeChange(cookTime - 5) // Prevent going below 1 minute
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_left),
                        contentDescription = "Decrease cook time",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = "$cookTime min", // Show cook time in minutes
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                IconButton(onClick = {
                    onCookTimeChange(cookTime + 5) // Increase cook time
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_right),
                        contentDescription = "Increase cook time",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientItem(
    name: String,
    quantity: String,
    onNameChange: (String) -> Unit,
    onQuantityChange: (String) -> Unit,
    onRemove: () -> Unit
) {
    // Holds the per-100g calories fetched from your API.
    var fetchedCalories by remember { mutableStateOf(0.0) }
    // Holds the total calories calculated from the current quantity.
    var totalCalories by remember { mutableStateOf(0.0) }

    // When the name changes, fetch the nutritional data.
    LaunchedEffect(name) {
        if (name.isNotBlank()) {
            fetchNutritionForIngredient(name) { foodItem ->
                // Assume foodItem.calories represents calories per 100g.
                fetchedCalories = foodItem.calories
            }
        } else {
            fetchedCalories = 0.0
        }
    }

    // Recalculate total calories when either the quantity or fetchedCalories changes.
    LaunchedEffect(quantity, fetchedCalories) {
        val grams = quantity.toDoubleOrNull() ?: 0.0
        totalCalories = (fetchedCalories / 100.0) * grams
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ingredient Name Input
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            modifier = Modifier.weight(1f),
            label = { Text("Name", fontSize = 12.sp) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFFE23E3E),
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color(0xFFE23E3E),
                cursorColor = Color(0xFFE23E3E)
            )
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Ingredient Quantity Input
        OutlinedTextField(
            value = quantity,
            onValueChange = onQuantityChange,
            modifier = Modifier.weight(1f),
            label = { Text("Amount (g)", fontSize = 12.sp) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFFE23E3E),
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color(0xFFE23E3E),
                cursorColor = Color(0xFFE23E3E)
            )
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Calories Display (Automatically recalculated)
        OutlinedTextField(
            value = String.format("%.1f", totalCalories),
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.weight(1f),
            label = { Text("Calories", fontSize = 12.sp) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFFE23E3E),
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color(0xFFE23E3E),
                cursorColor = Color(0xFFE23E3E)
            )
        )

        // Remove Ingredient Button
        IconButton(onClick = onRemove) {
            Icon(
                painter = painterResource(id = R.drawable.remove),
                contentDescription = "Remove ingredient",
                tint = Color.Red,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
@Composable
fun AddNewIngredientButton(onAdd: () -> Unit) {
    TextButton(
        onClick = onAdd,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            painter = painterResource(id = R.drawable.addbtn),
            contentDescription = "Add new ingredient",
            tint = Orange,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Add new Ingredient", color = Orange)
    }
}

// Only one definition of showImageSourceDialog
@Composable
fun showImageSourceDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onPickFromGallery: () -> Unit,
    onTakePhoto: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Choose Image Source") },
            text = { Text("Pick an image from gallery or take a new photo.") },
            confirmButton = {
                TextButton(onClick = {
                    onPickFromGallery() // Launch the gallery picker
                    onDismiss()
                }) {
                    Text("Gallery")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onTakePhoto() // Launch the camera picker
                    onDismiss()
                }) {
                    Text("Camera")
                }
            }
        )
    }
}




