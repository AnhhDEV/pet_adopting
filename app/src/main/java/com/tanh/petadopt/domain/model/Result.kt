package com.tanh.petadopt.domain.model

sealed interface Result<out T, out E> {
    data class Success<out D> (val data: D): Result<D, Nothing>
    data class Error<out E> (val error: E): Result<Nothing, E>
}

fun <T, E> Result<T, E>.onSuccess(action: (T) -> Unit): Result<T, E> {
    return when(this) {
        is Result.Error -> this
        is Result.Success ->{
            action(data)
            this
        }
    }
}

fun <T, E> Result<T, E>.onError(action: (E) -> Unit): Result<T, E> {
    return when(this) {
        is Result.Success -> this
        is Result.Error -> {
            action(error)
            this
        }
    }
}

