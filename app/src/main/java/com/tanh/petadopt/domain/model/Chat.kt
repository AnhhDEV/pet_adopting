package com.tanh.petadopt.domain.model

import com.google.firebase.Timestamp

data class Chat(
    val chatId: String? = "",
    val fromId: String? = "",
    val toId: String? = "",
    val lastMessage: String? = "",
    val lastTime: Timestamp? = null,
    val id: List<String> = emptyList()
)
