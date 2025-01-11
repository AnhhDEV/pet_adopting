package com.tanh.petadopt.presentation.pet_detail

import com.tanh.petadopt.domain.dto.PetDto
import com.tanh.petadopt.domain.model.UserData

data class DetailUiState(
    var isLoading: Boolean? = false,
    val pet: PetDto? = null,
    val error: String = "",
    val isFavorite: Boolean? = false,
    val user: UserData? = null,
    val chatId: String = ""
)