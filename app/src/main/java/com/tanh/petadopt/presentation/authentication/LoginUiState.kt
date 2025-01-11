package com.tanh.petadopt.presentation.authentication

data class LoginUiState(
    val isLoginSuccessful: Boolean = false,
    val signInError: String? = null
)
