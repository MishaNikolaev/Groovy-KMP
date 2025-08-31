package com.nmichail.groovy_kmp.feature.core.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Base ViewModel class that provides common functionality
 */
abstract class BaseViewModel : CoroutineScope {
    
    private val job = SupervisorJob()
    override val coroutineContext = Dispatchers.Main + job
    
    /**
     * Loading state
     */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    /**
     * Error state
     */
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    /**
     * Success state
     */
    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess.asStateFlow()
    
    /**
     * Set loading state
     */
    protected fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }
    
    /**
     * Set error message
     */
    protected fun setError(message: String?) {
        _errorMessage.value = message
    }
    
    /**
     * Set success state
     */
    protected fun setSuccess(success: Boolean) {
        _isSuccess.value = success
    }
    
    /**
     * Clear error message
     */
    protected fun clearError() {
        _errorMessage.value = null
    }
    
    /**
     * Clear success state
     */
    protected fun clearSuccess() {
        _isSuccess.value = false
    }
    
    /**
     * Launch coroutine with loading state management
     */
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
    
    /**
     * Clean up resources
     */
    fun onCleared() {
        job.cancel()
    }
}
