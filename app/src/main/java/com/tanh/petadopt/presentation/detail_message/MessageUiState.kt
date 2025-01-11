package com.tanh.petadopt.presentation.detail_message

import com.tanh.petadopt.domain.model.Message
import com.tanh.petadopt.domain.model.UserData

data class MessageUiState(
    val isLoading: Boolean? = false,
    val error: String? = "",
    val messages: List<Message> = emptyList(),
    val receiver: UserData? = null,
    val onMessageChange: String = "",
    val newChatId: String = "",
    val userId: String = ""
)