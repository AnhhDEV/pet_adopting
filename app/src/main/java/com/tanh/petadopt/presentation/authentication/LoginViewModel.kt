package com.tanh.petadopt.presentation.authentication

import android.app.Activity
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.auth.User
import com.tanh.petadopt.data.GoogleAuthUiClient
import com.tanh.petadopt.data.UserRepository
import com.tanh.petadopt.domain.model.SignInResult
import com.tanh.petadopt.domain.model.UserData
import com.tanh.petadopt.domain.model.onError
import com.tanh.petadopt.domain.model.onSuccess
import com.tanh.petadopt.presentation.OneTimeEvent
import com.tanh.petadopt.util.Util
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val googleAuth: GoogleAuthUiClient,
    private val userRepository: UserRepository
) : ViewModel() {

    var isLoggedIn by mutableStateOf(false)
        private set

    private val _state = MutableStateFlow(LoginUiState())
    val state = _state.asStateFlow()

    private val _channel = Channel<OneTimeEvent>()
    val channel = _channel.receiveAsFlow()

    private fun createUser(name: String?, id: String, photoUrl: String?) {
        viewModelScope.launch {
            userRepository.insertUser(name, id, photoUrl)
                .onError {
                    sendEvent(OneTimeEvent.ShowToast("unknown error"))
                }
                .onSuccess {
                }
        }
    }

    private suspend fun getUser(userId: String): UserData? {
        var userData: UserData? = null
        userRepository.getUser(userId = userId)
            .onSuccess {
                userData = it
            }
            .onError {
                userData = null
            }
        return userData
    }

    fun loginSuccessfully() {
        if (_state.value.isLoginSuccessful == true) {
            val user = googleAuth.getSignedInUser()
            if (user != null) {
                viewModelScope.launch {
                    val check = getUser(user.userId)
                    if (check == null) {
                        createUser(user.username, user.userId, user.profilePictureUrl)
                    }
                }
            }
            sendEvent(OneTimeEvent.ShowSnackbar(message = "Login successfully"))
            sendEvent(OneTimeEvent.Navigate(route = Util.HOME))
            resetState()
        }
    }

    fun onGetIntent(result: ActivityResult) {
        viewModelScope.launch {
            if (result.resultCode == Activity.RESULT_OK) {
                val signInResult = googleAuth.signInWithIntent(
                    intent = result.data ?: return@launch
                )
                if(signInResult.errorMessage != null) {
                    sendEvent(OneTimeEvent.ShowSnackbar(message = signInResult.errorMessage))
                }
                onSignInResult(result = signInResult)
            }
        }
    }

    fun onLogin(launcher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>) {
        viewModelScope.launch {
            val loginResult = googleAuth.signIn()
            launcher.launch(
                IntentSenderRequest.Builder(
                    loginResult ?: return@launch
                ).build()
            )
        }
    }

    fun onLogout() {
        viewModelScope.launch {
            googleAuth.signOut()
        }
    }

    fun onNavToHome() {
        sendEvent(OneTimeEvent.Navigate(route = Util.HOME))
    }

    fun getCurrentUser(): UserData? {
        return googleAuth.getSignedInUser()
    }

    private fun onSignInResult(result: SignInResult) {
        _state.update {
            it.copy(
                isLoginSuccessful = result.data != null,
                signInError = result.errorMessage
            )
        }
    }


    private fun resetState() {
        _state.update {
            LoginUiState()
        }
    }

    fun logIn() {
        isLoggedIn = true
    }

    fun logOut() {
        isLoggedIn = false
    }

    private fun sendEvent(event: OneTimeEvent) {
        viewModelScope.launch {
            _channel.send(event)
        }
    }

}