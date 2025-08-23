package com.nmichail.groovy_kmp.presentation.session

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.nmichail.groovy_kmp.domain.models.AuthResponse
import com.nmichail.groovy_kmp.domain.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class SessionViewModel {
    var currentUser: User? by mutableStateOf(null)
        private set
    
    var currentToken: String? by mutableStateOf(null)
        private set
    
    var isSessionLoaded by mutableStateOf(false)
        private set

    var onSaveSession: ((User, String) -> Unit)? = null
    var onLoadSession: ((User?, String?) -> Unit)? = null
    var onClearSession: (() -> Unit)? = null

    fun saveSession(authResponse: AuthResponse) {
        val user = authResponse.user
        val token = authResponse.token
        if (user != null && token != null) {
            currentUser = user
            currentToken = token
            onSaveSession?.invoke(user, token)
            println("🔐 SessionViewModel: Session saved for user: ${user.email}")
        }
    }

    fun loadSession(callback: (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            onLoadSession = { user, token ->
                currentUser = user
                currentToken = token
                isSessionLoaded = true
                val hasSession = user != null && token != null
                println("🔐 SessionViewModel: Session loaded, hasSession: $hasSession")
                callback(hasSession)
            }
            
            requestSessionLoad()
        }
    }

    fun clearSession() {
        currentUser = null
        currentToken = null
        onClearSession?.invoke()
        println("🔐 SessionViewModel: Session cleared")
    }

    protected open fun requestSessionLoad() {
        onLoadSession?.invoke(null, null)
    }

    fun hasValidSession(): Boolean {
        return currentUser != null && currentToken != null
    }
}
