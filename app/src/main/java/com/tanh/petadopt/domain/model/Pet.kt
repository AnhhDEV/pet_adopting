package com.tanh.petadopt.domain.model

data class Pet(
    val animalId: String? = "",
    val ownerId: String? = "",
    val name: String? = "",
    val age: Double? = 0.0,
    val weight: Double? = 0.0,
    val breed: String? = "",
    val category: String? = "",
    val gender: Boolean? = false,
    val photoUrl: String? = "",
    val address: String? = "",
    val about: String? = ""
)
