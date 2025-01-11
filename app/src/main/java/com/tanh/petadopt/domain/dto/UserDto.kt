package com.tanh.petadopt.domain.dto

import com.google.firebase.firestore.PropertyName

data class UserDto(
    @PropertyName("userId") val id: String? = "",
    @PropertyName("username") val name: String? = "",
    @PropertyName("profilePictureUrl") val avatar: String? = ""
)
