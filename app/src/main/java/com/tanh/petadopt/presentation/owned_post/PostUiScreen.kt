package com.tanh.petadopt.presentation.owned_post

import com.tanh.petadopt.domain.model.Pet

data class PostUiScreen(
    val isLoading: Boolean = false,
    val pets: List<Pet> = emptyList(),
    val error: String? = null
)
