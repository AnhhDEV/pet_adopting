package com.tanh.petadopt.presentation.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tanh.petadopt.data.GoogleAuthUiClient
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
class ProfileViewModel @Inject constructor(
    private val auth: GoogleAuthUiClient
): ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state = _state.asStateFlow()

    private val _channel = Channel<OneTimeEvent>()
    val channel = _channel.receiveAsFlow()

    fun getUser() {
        _state.value = state.value.copy(
            isLoading = true
        )
        _state.value = state.value.copy(
            user = auth.getSignedInUser(),
            isLoading = false
        )
        Log.d("repo", "profile: + ${auth.getSignedInUser()?.userId}")
    }

    fun onNavToAdd() {
        sendEvent(OneTimeEvent.Navigate(Util.ADD))
    }

    fun onNavToMyPost() {
        sendEvent(OneTimeEvent.Navigate(Util.MY_POST))
    }

    fun onNavToFavorite() {
        sendEvent(OneTimeEvent.Navigate(Util.FAVORITE))
    }

    fun onNavToInbox() {
        sendEvent(OneTimeEvent.Navigate(Util.INBOX))
    }

    fun logOut() {
        viewModelScope.launch {
            auth.signOut()
            sendEvent(OneTimeEvent.Navigate(Util.LOG_IN))
        }
    }

    private fun sendEvent(event: OneTimeEvent) {
        viewModelScope.launch {
            _channel.send(event)
        }
    }

}