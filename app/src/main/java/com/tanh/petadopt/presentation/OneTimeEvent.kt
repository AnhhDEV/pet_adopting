package com.tanh.petadopt.presentation

import kotlinx.serialization.Serializable

sealed class OneTimeEvent {
    data class Navigate(val route: String): OneTimeEvent()
    data class ShowSnackbar(val message: String): OneTimeEvent()
    data class ShowToast(val message: String): OneTimeEvent()
}