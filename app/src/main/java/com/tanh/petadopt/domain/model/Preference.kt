package com.tanh.petadopt.domain.model

data class Preference(
    val pets: List<String> = emptyList(),
    val userId: String = ""
)
    