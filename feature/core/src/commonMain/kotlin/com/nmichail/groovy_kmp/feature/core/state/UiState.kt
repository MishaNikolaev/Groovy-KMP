package com.nmichail.groovy_kmp.feature.core.state

/**
 * Base UI state interface
 */
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
    object Empty : UiState<Nothing>()
}

/**
 * Extension function to check if state is loading
 */
fun <T> UiState<T>.isLoading(): Boolean = this is UiState.Loading

/**
 * Extension function to check if state is success
 */
fun <T> UiState<T>.isSuccess(): Boolean = this is UiState.Success

/**
 * Extension function to check if state is error
 */
fun <T> UiState<T>.isError(): Boolean = this is UiState.Error

/**
 * Extension function to check if state is empty
 */
fun <T> UiState<T>.isEmpty(): Boolean = this is UiState.Empty

/**
 * Extension function to get data from success state
 */
fun <T> UiState<T>.getDataOrNull(): T? = when (this) {
    is UiState.Success -> data
    else -> null
}

/**
 * Extension function to get error message from error state
 */
fun <T> UiState<T>.getErrorMessageOrNull(): String? = when (this) {
    is UiState.Error -> message
    else -> null
}

/**
 * Extension function to map success data
 */
fun <T, R> UiState<T>.map(transform: (T) -> R): UiState<R> = when (this) {
    is UiState.Loading -> UiState.Loading
    is UiState.Success -> UiState.Success(transform(data))
    is UiState.Error -> UiState.Error(message)
    is UiState.Empty -> UiState.Empty
}
