package com.example.assignme.ViewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.assignme.DataClass.GroceryListData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import android.content.Context
import android.content.SharedPreferences

class GroceryListViewModel(private val auth: FirebaseAuth, context: Context) : ViewModel() {

    private val db = FirebaseFirestore.getInstance().apply {
        firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
    }
    private val groceryCollection = db.collection("groceryItems")

    private val _groceryItems = MutableStateFlow<List<GroceryListData>>(emptyList())
    val groceryItems: StateFlow<List<GroceryListData>> = _groceryItems.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val userId: String?
        get() = auth.currentUser?.uid

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("GroceryListPrefs", Context.MODE_PRIVATE)

    init {
        Log.d("GroceryListViewModel", "ViewModel initialized")
        fetchGroceryItems()
    }

    private fun fetchGroceryItems() {
        viewModelScope.launch {
            userId?.let { uid ->
                Log.d("GroceryListViewModel", "Fetching items for user: $uid")
                _isLoading.value = true
                try {
                    val snapshot = groceryCollection.whereEqualTo("userId", uid).get().await()
                    if (snapshot.isEmpty) {
                        Log.d("GroceryListViewModel", "No grocery items found for user: $uid")
                        _groceryItems.value = emptyList()
                    } else {
                        val allItems = snapshot.toObjects(GroceryListData::class.java)
                        val sortedItems = allItems.map { item ->
                            val isChecked = sharedPreferences.getBoolean(item.id, item.isChecked)
                            item.copy(isChecked = isChecked)
                        }.sortedBy { it.isChecked }
                        _groceryItems.value = sortedItems
                        Log.d("GroceryListViewModel", "Updated grocery lists with ${allItems.size} items")
                    }
                } catch (e: Exception) {
                    Log.e("GroceryListViewModel", "Error fetching grocery items", e)
                } finally {
                    _isLoading.value = false
                }
            } ?: run {
                Log.w("GroceryListViewModel", "User ID is null, cannot fetch items")
                _isLoading.value = false
            }
        }
    }

    fun addItem(name: String) {
        if (name.isBlank()) {
            Log.w("GroceryListViewModel", "Attempted to add blank item")
            return
        }

        userId?.let { uid ->
            val newItem = GroceryListData(
                id = System.currentTimeMillis().toString(),
                name = name,
                userId = uid,
                isChecked = false
            )

            groceryCollection.document(newItem.id).set(newItem)
                .addOnSuccessListener {
                    Log.d("GroceryListViewModel", "Item '${newItem.name}' added successfully")
                    fetchGroceryItems()
                }
                .addOnFailureListener { e ->
                    Log.e("GroceryListViewModel", "Failed to add item: ${e.localizedMessage}", e)
                }
        } ?: Log.w("GroceryListViewModel", "User ID is null, cannot add item")
    }

    fun deleteItem(id: String) {
        groceryCollection.document(id).delete()
            .addOnSuccessListener {
                Log.d("GroceryListViewModel", "Successfully deleted item: $id")
                fetchGroceryItems()
                sharedPreferences.edit().remove(id).apply()
            }
            .addOnFailureListener { e ->
                Log.e("GroceryListViewModel", "Failed to delete item: $id", e)
            }
    }

    fun toggleItemChecked(id: String) {
        viewModelScope.launch {
            val currentList = _groceryItems.value.toMutableList()
            val index = currentList.indexOfFirst { it.id == id }
            if (index == -1) return@launch  // If item not found, do nothing

            val currentItem = currentList[index]
            val newCheckedState = !currentItem.isChecked

            // Update Firestore first to ensure persistence
            groceryCollection.document(id).update("isChecked", newCheckedState)
                .addOnSuccessListener {
                    viewModelScope.launch {
                        val updatedList = _groceryItems.value.toMutableList()
                        updatedList[index] = currentItem.copy(isChecked = newCheckedState)
                        _groceryItems.value = updatedList.sortedBy { it.isChecked }  // Keep sorting in sync

                        // Save the new checked state in SharedPreferences
                        sharedPreferences.edit().putBoolean(id, newCheckedState).apply()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("GroceryListViewModel", "Error updating Firestore", e)
                }
        }
    }
}