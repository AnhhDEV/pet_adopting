package com.tanh.petadopt.presentation.add

data class AddUiState(
    val uri: String = "",
    val name: String = "",
    val category: String = "",
    val age: String = "",
    val weight: String = "",
    val breed: String = "",
    val gender: Boolean = false,
    val address: String = "",
    val about: String = "",

    val photoUrl: String? = null,
    val nameError: String? = null,
    val ageError: String? = null,
    val weightError: String? = null,
    val breedError: String? = null,
    val addressError: String? = null,
    val aboutError: String? = null,
    val categoryError: String? = null,
    val genderError: String? = null,
    val error: String? = null
)