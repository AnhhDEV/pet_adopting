package com.tanh.petadopt.presentation.home

import com.tanh.petadopt.domain.dto.PetDto
import com.tanh.petadopt.domain.model.UserData

data class HomeUIState(
    val pets: List<PetDto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userData: UserData? = null
)
