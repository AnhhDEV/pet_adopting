package com.tanh.petadopt.presentation.home

import android.util.Log
import androidx.compose.runtime.saveable.autoSaver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tanh.petadopt.data.ChatRepository
import com.tanh.petadopt.data.GoogleAuthUiClient
import com.tanh.petadopt.data.PetRepository
import com.tanh.petadopt.data.PreferenceRepository
import com.tanh.petadopt.data.UserRepository
import com.tanh.petadopt.domain.model.UserData
import com.tanh.petadopt.domain.model.onError
import com.tanh.petadopt.domain.model.onSuccess
import com.tanh.petadopt.presentation.OneTimeEvent
import com.tanh.petadopt.util.Util
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
class HomeViewModel @Inject constructor(
    private val googleAuth: GoogleAuthUiClient,
    private val chatRepository: ChatRepository,
    private val preferenceRepository: PreferenceRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUIState())
    val state = _state.asStateFlow()

    private val _totalUnreadMessage = MutableStateFlow<Int>(0)
    val totalUnreadMessage = _totalUnreadMessage.asStateFlow()

    private val _channel = Channel<OneTimeEvent>()
    val channel = _channel.receiveAsFlow()

    fun getTotalUnreadMessage(): Unit {
        viewModelScope.launch {
            try {
                chatRepository.getUnreadMessage(
                    userId = googleAuth.getSignedInUser()?.userId ?: ""
                ).collect { result ->
                    result.run {
                        onSuccess {value ->
                            Log.d("total", "success: $value")
                            _totalUnreadMessage.update {
                                value
                            }
                        }
                        onError {
                            Log.d("total", "error: ${it.localizedMessage}")
                            _totalUnreadMessage.update {
                                0
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(
                        errorMessage = e.localizedMessage
                    )
                }
            }
        }
    }

    fun onNavToAdd() {
        sendEvent(OneTimeEvent.Navigate(Util.ADD))
    }

    fun onNavToDetail(petId: String) {
        if (petId.isNotEmpty()) {
            sendEvent(OneTimeEvent.Navigate(Util.DETAIL + "/$petId"))
        } else {
            sendEvent(OneTimeEvent.ShowToast("Pet id is empty"))
        }
    }

    fun getUser() {
        val user = googleAuth.getSignedInUser()
        _state.update {
            it.copy(
                userData = user
            )
        }
    }

    suspend fun getAllPetsByCategory(category: String) {
        _state.update {
            it.copy(
                isLoading = true
            )
        }
        preferenceRepository.getPetPreferencesByCategory(
            userId = googleAuth.getSignedInUser()?.userId ?: "",
            category = category
        ).collect { result ->
            result.run {
                onSuccess { list ->
                    _state.update {
                        it.copy(
                            pets = list,
                            isLoading = false
                        )
                    }
                }
                onError {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = it.errorMessage
                        )
                    }
                }
            }
        }
    }

    suspend fun getAllPets() {
        _state.update {
            it.copy(
                isLoading = true
            )
        }
        preferenceRepository.getPreferenceByUser(
            userId = googleAuth.getSignedInUser()?.userId ?: ""
        ).collect { result ->
            result.run {
                onSuccess { list ->
                    _state.update {
                        it.copy(
                            pets = list,
                            isLoading = false
                        )
                    }
                }
                onError {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = it.errorMessage
                        )
                    }
                }
            }
        }
    }

    fun addFavorite(petId: String) {
        viewModelScope.launch {
            preferenceRepository.addToFavorite(
                userId = googleAuth.getSignedInUser()?.userId ?: "",
                petId = petId
            )
        }
    }

    fun removeFavorite(petId: String) {
        viewModelScope.launch {
            preferenceRepository.removeFromFavorite(
                userId = googleAuth.getSignedInUser()?.userId ?: "",
                petId = petId
            )
        }
    }

    private fun sendEvent(event: OneTimeEvent) {
        viewModelScope.launch {
            _channel.send(event)
        }
    }

}