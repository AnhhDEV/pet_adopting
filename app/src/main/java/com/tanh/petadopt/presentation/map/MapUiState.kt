package com.tanh.petadopt.presentation.map

import com.tanh.petadopt.domain.model.Pet

data class MapUiState(
    val isLoading: Boolean = false,
    val pets: List<Pet> = emptyList(),
    val error: String? = null
)
