package com.example.assignme.DataClass

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?,
    val isNewUser: Boolean = false // New field to indicate if the user is new
)

data class UserData(
    val userId: String,
    val username: String?,
    val profilePictureUrl: String?,
    var userType: String? = null
)