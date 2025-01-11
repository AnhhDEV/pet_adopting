package com.tanh.petadopt.presentation.inbox

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tanh.petadopt.converter.TimeDefinition
import com.tanh.petadopt.data.ChatRepository
import com.tanh.petadopt.data.GoogleAuthUiClient
import com.tanh.petadopt.domain.model.UserData
import com.tanh.petadopt.domain.model.onError
import com.tanh.petadopt.domain.model.onSuccess
import com.tanh.petadopt.presentation.OneTimeEvent
import com.tanh.petadopt.util.Util
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InboxViewModel @Inject constructor(
    private val auth: GoogleAuthUiClient,
    private val chatRepository: ChatRepository
): ViewModel() {

    private val _state = MutableStateFlow(InboxUiState())
    val state = _state.asStateFlow()

    private val _channel = Channel<OneTimeEvent>()
    val channel = _channel.receiveAsFlow()

    fun changeStatusMessage(chatId: String) {
        viewModelScope.launch {
            try {
                chatRepository.changeStatusMessage(
                    chatId = chatId,
                    userId = auth.getSignedInUser()?.userId ?: ""
                )
            } catch (e: Exception) {
                Log.d("update", e.localizedMessage!!)
            }
        }
    }

    suspend fun getChats() {
        val userId = auth.getSignedInUser()?.userId ?: ""
        _state.value = _state.value.copy(
            isLoading = true
        )
        Log.d("repo",userId)
        chatRepository.getChats(
            userId = userId
        ).collect { res ->
            res.onSuccess  { chats ->
               _state.value = _state.value.copy(
                   isLoading = false,
                   chats = chats
               )
            }
            res.onError {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = it.message
                )
            }
        }
    }

    suspend fun getReceivers() {
        val currentState = _state.value
        val receivers = currentState.chats.mapNotNull { chat ->
            chat.toId?.let {toId ->
                chatRepository.getUser(toId)
            }
        }
        _state.value = currentState.copy(receivers = receivers)
    }

    fun onNavToDetailChatting(chatId: String, receiverId: String) {
        sendEvent(OneTimeEvent.Navigate(Util.MESSENGER + "/$chatId/$receiverId"))
    }

    fun resetState()  {
        _state.value = InboxUiState()
    }

    private fun sendEvent(event: OneTimeEvent) {
        viewModelScope.launch {
            _channel.send(event)
        }
    }

}