package com.nmichail.groovy_kmp.feature.auth.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.nmichail.groovy_kmp.domain.usecases.RegisterUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase
) {
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun register(email: String, password: String, username: String, onResult: (Boolean) -> Unit) {
        isLoading = true
        errorMessage = null
        
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = registerUseCase(email, password, username)
                
                if (response.token != null || response.user != null) {
                    onResult(true)
                } else {
                    errorMessage = response.error ?: "Unknown error"
                    onResult(false)
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Network error"
                onResult(false)
            } finally {
                isLoading = false
            }
        }
    }
}
