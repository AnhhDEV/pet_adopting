package com.tanh.petadopt.domain.model

data class SignInResult(
    val data: UserData?,
    val errorMessage: String? = null
)

data class UserData(
    val userId: String = "",
    val username: String? = "",
    val profilePictureUrl: String? = "",
    val gmail: String? = "",
    val messages: List<String>? = emptyList(),
    val preferences: List<String>? = emptyList()
)

