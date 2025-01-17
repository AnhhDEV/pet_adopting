package com.tanh.petadopt.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tanh.petadopt.data.PetRepository
import com.tanh.petadopt.domain.model.onError
import com.tanh.petadopt.domain.model.onSuccess
import com.tanh.petadopt.presentation.OneTimeEvent
import com.tanh.petadopt.util.Util
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val petRepository: PetRepository
): ViewModel() {

    private val _state = MutableStateFlow(MapUiState())
    val state = _state.asStateFlow()

    private val _channel = Channel<OneTimeEvent>()
    val channel = _channel.receiveAsFlow()

    fun initPetList() {
        _state.value = _state.value.copy(
            isLoading = true
        )
        viewModelScope.launch {
            petRepository.getAllPets().collect { result ->
                result.run {
                    onSuccess { pets ->
                        _state.value = _state.value.copy(
                            isLoading = false,
                            pets = pets
                        )
                    }
                    onError {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = it.message
                        )
                    }
                }
            }
        }
    }

    fun navToDetailPet(petId: String) {
        sendEvent(OneTimeEvent.Navigate(Util.DETAIL + "/$petId"))
    }

    private fun sendEvent(event: OneTimeEvent) {
        viewModelScope.launch {
            _channel.send(event)
        }
    }

}