package com.nmichail.groovy_kmp.presentation.screen.login

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
    var lastAuthResponse: AuthResponse? = null

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        println("🔐 LoginViewModel: Starting login process for email: $email")
        isLoading = true
        errorMessage = null
        CoroutineScope(Dispatchers.Main).launch {
            try {
                println("🔐 LoginViewModel: Calling loginUseCase")
                val response = loginUseCase(email, password)
                println("🔐 LoginViewModel: Received response: $response")
                lastAuthResponse = response
                if (response.token != null && response.user != null) {
                    println("🔐 LoginViewModel: Login successful, token: ${response.token}")
                    sessionViewModel.saveSession(response)
                    onResult(true)
                } else {
                    println("🔐 LoginViewModel: Login failed, error: ${response.error}")
                    errorMessage = response.error ?: "Unknown error"
                    onResult(false)
                }
            } catch (e: Exception) {
                println("❌ LoginViewModel: Login exception: ${e.message}")
                e.printStackTrace()
                errorMessage = e.message ?: "Network error"
                onResult(false)
            } finally {
                println("🔐 LoginViewModel: Login process finished")
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
        println("🔐 LoginViewModel: Session cleared")
    }
}