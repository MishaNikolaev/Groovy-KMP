package com.nmichail.groovy_kmp.feature.core.viewmodel

import com.nmichail.groovy_kmp.domain.models.AuthResponse
import com.nmichail.groovy_kmp.domain.models.User
import kotlinx.coroutines.launch

open class SessionViewModel : BaseViewModel() {
    
    private var _currentUser: User? = null
    private var _currentToken: String? = null
    private var _isSessionLoaded = false
    
    val currentUser: User? get() = _currentUser
    val currentToken: String? get() = _currentToken
    val isSessionLoaded: Boolean get() = _isSessionLoaded

    var onSaveSession: ((User, String) -> Unit)? = null
    var onLoadSession: ((User?, String?) -> Unit)? = null
    var onClearSession: (() -> Unit)? = null

    fun saveSession(authResponse: AuthResponse) {
        val user = authResponse.user
        val token = authResponse.token
        if (user != null && token != null) {
            _currentUser = user
            _currentToken = token
            onSaveSession?.invoke(user, token)
            println("🔐 SessionViewModel: Session saved for user: ${user.email}")
        }
    }

    fun loadSession(callback: (Boolean) -> Unit) {
        launch {
            onLoadSession = { user, token ->
                _currentUser = user
                _currentToken = token
                _isSessionLoaded = true
                val hasSession = user != null && token != null
                println("🔐 SessionViewModel: Session loaded, hasSession: $hasSession")
                callback(hasSession)
            }

            requestSessionLoad()
        }
    }

    fun clearSession() {
        _currentUser = null
        _currentToken = null
        onClearSession?.invoke()
        println("🔐 SessionViewModel: Session cleared")
    }

    protected open fun requestSessionLoad() {
        onLoadSession?.invoke(null, null)
    }

    fun hasValidSession(): Boolean {
        return _currentUser != null && _currentToken != null
    }
}