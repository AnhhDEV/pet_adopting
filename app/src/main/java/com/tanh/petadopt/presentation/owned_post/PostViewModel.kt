package com.tanh.petadopt.presentation.owned_post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tanh.petadopt.data.GoogleAuthUiClient
import com.tanh.petadopt.data.PetRepository
import com.tanh.petadopt.domain.model.onError
import com.tanh.petadopt.domain.model.onSuccess
import com.tanh.petadopt.presentation.OneTimeEvent
import com.tanh.petadopt.util.Util
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val auth: GoogleAuthUiClient,
    private val petRepository: PetRepository
): ViewModel() {

    private val _state = MutableStateFlow(PostUiScreen())
    val state = _state.asStateFlow()

    private val _channel = Channel<OneTimeEvent>()
    val channel = _channel.receiveAsFlow()

    fun onNavToProfile() {
        sendEvent(OneTimeEvent.Navigate(Util.PROFILE))
    }

    fun onNavToDetail(petId: String) {
        sendEvent(OneTimeEvent.Navigate(Util.DETAIL + "/$petId"))
    }

    fun getPets() {
        _state.value = state.value.copy(
            isLoading = true
        )
        viewModelScope.launch {
            petRepository.getPetsByUser(
                userId = auth.getSignedInUser()?.userId ?: ""
            ).collect {
                it.onSuccess { pets ->
                    _state.value = state.value.copy(
                        pets = pets,
                        isLoading = false
                    )
                }
                it.onError {e ->
                    _state.value = state.value.copy(
                        error = e.message,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun sendEvent(event: OneTimeEvent) {
        viewModelScope.launch {
            _channel.send(event)
        }
    }

}