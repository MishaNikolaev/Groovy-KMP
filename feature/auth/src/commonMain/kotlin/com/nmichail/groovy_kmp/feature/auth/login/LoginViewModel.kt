package com.nmichail.groovy_kmp.feature.auth.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.nmichail.groovy_kmp.domain.usecases.LoginUseCase
import com.nmichail.groovy_kmp.domain.models.AuthResponse
import com.nmichail.groovy_kmp.presentation.session.SessionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val sessionViewModel: SessionViewModel
) {
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    private var lastAuthResponse: AuthResponse? = null

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        isLoading = true
        errorMessage = null
        
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = loginUseCase(email, password)
                lastAuthResponse = response
                
                if (response.token != null && response.user != null) {
                    sessionViewModel.saveSession(response)
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

    fun getUser() = sessionViewModel.currentUser ?: lastAuthResponse?.user
    fun getToken() = sessionViewModel.currentToken ?: lastAuthResponse?.token

    fun checkSavedSession(onResult: (Boolean) -> Unit) {
        sessionViewModel.loadSession { hasSession ->
            if (hasSession) {
                lastAuthResponse = AuthResponse(
                    user = sessionViewModel.currentUser,
                    token = sessionViewModel.currentToken,
                    error = null
                )
            }
            onResult(hasSession)
        }
    }

    fun clearSession() {
        sessionViewModel.clearSession()
        lastAuthResponse = null
    }
}
