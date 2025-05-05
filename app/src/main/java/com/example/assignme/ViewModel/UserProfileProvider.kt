package com.example.assignme.ViewModel

// UserProfileProvider.kt
import androidx.lifecycle.LiveData

interface UserProfileProvider {
    abstract val savedRecipes: Any
    val userId: LiveData<String>
    val userProfile: LiveData<UserProfile>
    val adminProfile: LiveData<AdminProfile>
    fun setUserId(id: String)
    abstract fun fetchUserProfile(it: String)
    abstract fun fetchAdminProfile(it: String)
    abstract fun fetchSavedRecipes(s: String)
}
