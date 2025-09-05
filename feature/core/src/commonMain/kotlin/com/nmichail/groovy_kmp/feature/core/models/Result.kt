package com.nmichail.groovy_kmp.feature.core.models


sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable? = null, val message: String? = null) : Result<Nothing>()
    object Loading : Result<Nothing>()
    
    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isLoading: Boolean get() = this is Loading
    
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }
    
    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw exception ?: RuntimeException(message ?: "Unknown error")
        is Loading -> throw IllegalStateException("Result is still loading")
    }
    
    fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }
    
    fun onError(action: (Error) -> Unit): Result<T> {
        if (this is Error) action(this)
        return this
    }
    
    fun onLoading(action: () -> Unit): Result<T> {
        if (this is Loading) action()
        return this
    }
}

fun <T> Result<T>.toUiState(): com.nmichail.groovy_kmp.feature.core.state.UiState<T> = when (this) {
    is Result.Success -> com.nmichail.groovy_kmp.feature.core.state.UiState.Success(data)
    is Result.Error -> com.nmichail.groovy_kmp.feature.core.state.UiState.Error(message ?: "Unknown error")
    is Result.Loading -> com.nmichail.groovy_kmp.feature.core.state.UiState.Loading
}
