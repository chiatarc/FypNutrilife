package com.example.assignme.ViewModel

// MockUserViewModel.kt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.assignme.DataClass.Recipes

class MockUserViewModel : UserProfileProvider {

    private val _savedRecipes = MutableLiveData<List<Recipes>>()
    override val savedRecipes: LiveData<List<Recipes>> = _savedRecipes
    private val _userId = MutableLiveData<String>()
    override val userId: LiveData<String> get() = _userId

    private val _userProfile = MutableLiveData<UserProfile>()
    override val userProfile: LiveData<UserProfile> get() = _userProfile

    private val _adminProfile = MutableLiveData<AdminProfile>()
    override val adminProfile: LiveData<AdminProfile> get() = _adminProfile

    override fun setUserId(id: String) {
        // Mock implementation
        _userId.value = id
        _userProfile.value = UserProfile(
            name = "John Doe",
            email = "johndoe@example.com",
            phoneNumber = "123-456-7890",
            profilePictureUrl = "https://example.com/profile.jpg",
            gender = "Male",
            country = "USA"
        )
    }

    override fun fetchUserProfile(it: String) {
        TODO("Not yet implemented")
    }
    override fun fetchAdminProfile(it: String) {
        TODO("Not yet implemented")
    }

    override fun fetchSavedRecipes(s: String){

    }
}


