package com.tanh.petadopt.presentation.pet_detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tanh.petadopt.data.ChatRepository
import com.tanh.petadopt.data.GoogleAuthUiClient
import com.tanh.petadopt.data.PetRepository
import com.tanh.petadopt.data.PreferenceRepository
import com.tanh.petadopt.data.UserRepository
import com.tanh.petadopt.domain.model.onError
import com.tanh.petadopt.domain.model.onSuccess
import com.tanh.petadopt.presentation.OneTimeEvent
import com.tanh.petadopt.util.Util
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DetailViewModel @Inject constructor (
    private val auth: GoogleAuthUiClient,
    private val petRepository: PetRepository,
    private val preferenceRepository: PreferenceRepository,
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository
): ViewModel() {

    private val _state = MutableStateFlow(DetailUiState())
    val state = _state.asStateFlow()

    private val _channel = Channel<OneTimeEvent> {  }
    val channel = _channel.receiveAsFlow()

    fun isYourself(userId: String): Boolean = userId == (auth.getSignedInUser()?.userId ?: "")

    fun getOwner(ownerId: String) {
        viewModelScope.launch {
            userRepository.getUser(ownerId).onSuccess {
                _state.value = _state.value.copy(
                    user = it
                )
            }.onError {
                Log.d("test3", it.localizedMessage.toString())
            }
        }
    }

    fun getChatId(toId: String) {
        viewModelScope.launch {
            Log.d("detail", "toId: $toId")
            val task = async {
                chatRepository.getChatId(
                    fromId = auth.getSignedInUser()?.userId ?: "",
                    toId = toId
                )
            }.await()
            if(task != null) {
                _state.update { currentState ->
                    currentState.copy(
                        chatId = task
                    )
                }
                Log.d("detail", "chatId: ${_state.value.chatId}")
            } else {
                Log.d("detail", "empty chatId")
            }
        }
    }

    fun onNavToInbox(toId: String) {
        sendEvent(OneTimeEvent.Navigate(Util.MESSENGER + "/${_state.value.chatId}/$toId"))
    }

    fun addToFavorite(petId: String) {
        viewModelScope.launch {
            preferenceRepository.addToFavorite(
                petId = petId,
                userId = auth.getSignedInUser()?.userId ?: ""
            )
            _state.value = _state.value.copy(
                isFavorite = true
            )
        }
    }

    fun removeFromFavorite(petId: String) {
        viewModelScope.launch {
            preferenceRepository.removeFromFavorite(
                petId = petId,
                userId = auth.getSignedInUser()?.userId ?: ""
            )
            _state.value = _state.value.copy(
                isFavorite = false
            )
        }
    }

    fun getPetByAnimalId(petId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true
            )
            delay(500L)
            petRepository.getPetById(
                petId = petId,
                userId = auth.getSignedInUser()?.userId ?: ""
            ).onSuccess {
                _state.value = _state.value.copy(
                    isLoading = false,
                    pet = it,
                    isFavorite = it.isFavorite
                )
                Log.d("test", it.toString())
            }.onError {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = it.message ?: "You get exception!"
                )
            }
        }
    }

    fun navToHome() {
        sendEvent(OneTimeEvent.Navigate("back"))
    }

    private fun sendEvent(event: OneTimeEvent) {
        viewModelScope.launch {
            _channel.send(event)
        }
    }

}