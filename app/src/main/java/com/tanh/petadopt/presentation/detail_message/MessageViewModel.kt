package com.tanh.petadopt.presentation.detail_message

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.tanh.petadopt.data.AzureBlobStorage
import com.tanh.petadopt.data.ChatRepository
import com.tanh.petadopt.data.GoogleAuthUiClient
import com.tanh.petadopt.data.UserRepository
import com.tanh.petadopt.domain.dto.UserDto
import com.tanh.petadopt.domain.model.Message
import com.tanh.petadopt.domain.model.onError
import com.tanh.petadopt.domain.model.onSuccess
import com.tanh.petadopt.presentation.OneTimeEvent
import com.tanh.petadopt.presentation.inbox.InboxUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val auth: GoogleAuthUiClient,
    private val repository: ChatRepository,
    private val azureBlobStorage: AzureBlobStorage
) : ViewModel() {

    private val _state = MutableStateFlow(MessageUiState())
    val state = _state.asStateFlow()

    private val _channel = Channel<OneTimeEvent>()
    val channel = _channel.receiveAsFlow()

    fun upImage(uri: Uri, chatId: String) {
        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(
                    url =  azureBlobStorage.uploadFileToAzureBlob(
                        uri = uri,
                        containerTag = "message"
                    )
                )
            }
            repository.createMessage(
                chatId = _state.value.newChatId,
                message = Message(
                    content = _state.value.url,
                    time = Timestamp.now(),
                    isRead = false,
                    uid = auth.getSignedInUser()?.userId
                )
            )
        }
    }

    suspend fun getMessages(chatId: String) {
        _state.update { currentState ->
            currentState.copy(
                isLoading = true,
                userId = auth.getSignedInUser()?.userId ?: ""
            )
        }
        repository.getMessages(chatId = chatId).collect { message ->
            message.onSuccess {
                _state.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        messages = it
                    )
                }
                Log.d("demo", "abc:" + it.joinToString { mes -> mes.toString() })
            }
            message.onError {
                _state.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        error = it.localizedMessage
                    )
                }
                Log.d("demo", "error: " + it.message)
            }
        }
    }

    suspend fun getUserById(id: String) {
        _state.update { currentState ->
            currentState.copy(
                receiver = repository.getUser(id = id)
            )
        }
    }

    fun createMessage(chatId: String) {
        viewModelScope.launch {
            repository.createMessage(
                chatId = chatId, message = Message(
                    content = _state.value.onMessageChange,
                    uid = auth.getSignedInUser()?.userId ?: "",
                    time = Timestamp.now(),
                    isRead = false
                )
            )
        }
    }

    fun onMessageChange(message: String) {
        _state.update { currentState ->
            currentState.copy(
                onMessageChange = message
            )
        }
    }

    fun createChat(toId: String) {
        viewModelScope.launch {
            val task = async {
                repository.createChat(
                    fromId = auth.getSignedInUser()?.userId ?: "",
                    toId = toId
                )
            }.await()
            _state.update {currentState ->
                currentState.copy(
                    newChatId = task
                )
            }
            Log.d("new chat", _state.value.newChatId)
            Log.d("new chat", "message: " + _state.value.onMessageChange)
            repository.createMessage(
                chatId = _state.value.newChatId, message = Message(
                    content = _state.value.onMessageChange,
                    uid = auth.getSignedInUser()?.userId ?: "",
                    time = Timestamp.now()
                )
            )
            _state.update { currentState ->
                currentState.copy(
                    onMessageChange = ""
                )
            }
        }
    }


    fun updateChat(chatId: String) {
        _state.value = _state.value.copy(
            newChatId = chatId
        )
    }

    fun onNavToInBox() {
        resetState()
        sendEvent(OneTimeEvent.Navigate("back"))
    }

    fun resetState() {
        _state.value = MessageUiState()
    }

    private fun sendEvent(event: OneTimeEvent) {
        viewModelScope.launch {
            _channel.send(event)
        }
    }

}