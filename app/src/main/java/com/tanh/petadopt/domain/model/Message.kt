package com.tanh.petadopt.domain.model

import com.google.firebase.Timestamp

data class Message(
    val content: String? = "",
    val uid:  String? = "",
    val time: Timestamp? = null,
    val isRead: Boolean = false
)
