package com.nmichail.groovy_kmp.feature.core.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel : CoroutineScope {
    
    private val job = SupervisorJob()
    override val coroutineContext = Dispatchers.Main + job

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess.asStateFlow()

    protected fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }

    protected fun setError(message: String?) {
        _errorMessage.value = message
    }

    protected fun setSuccess(success: Boolean) {
        _isSuccess.value = success
    }

    protected fun clearError() {
        _errorMessage.value = null
    }

    protected fun clearSuccess() {
        _isSuccess.value = false
    }

    protected fun launchWithLoading(
        onStart: () -> Unit = { setLoading(true) },
        onComplete: () -> Unit = { setLoading(false) },
        onError: (Throwable) -> Unit = { error ->
            setError(error.message ?: "Unknown error occurred")
            setLoading(false)
        },
        block: suspend CoroutineScope.() -> Unit
    ) {
        launch {
            try {
                onStart()
                block()
                onComplete()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    fun onCleared() {
        job.cancel()
    }
}
