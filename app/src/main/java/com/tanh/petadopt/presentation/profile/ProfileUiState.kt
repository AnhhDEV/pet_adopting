package com.tanh.petadopt.presentation.profile

import com.tanh.petadopt.domain.model.UserData

data class ProfileUiState(
    val user: UserData? = null,
    val isLoading: Boolean? = false,
    val errorMessage: String? = ""
)
