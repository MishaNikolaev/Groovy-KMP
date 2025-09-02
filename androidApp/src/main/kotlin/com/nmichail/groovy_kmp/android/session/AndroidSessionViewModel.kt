package com.nmichail.groovy_kmp.android.session

import com.nmichail.groovy_kmp.data.manager.SessionManager
import com.nmichail.groovy_kmp.data.local.model.UserSession
import com.nmichail.groovy_kmp.domain.models.User
import com.nmichail.groovy_kmp.feature.core.viewmodel.SessionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AndroidSessionViewModel(
    private val sessionManager: SessionManager
) : SessionViewModel() {

    init {
        // Set up the callbacks for platform-specific session operations
        onSaveSession = { user, token ->
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val userSession = UserSession(
                        email = user.email ?: "",
                        username = user.username ?: "",
                        token = token
                    )
                    sessionManager.saveSession(userSession)
                    println("🔐 AndroidSessionViewModel: Session saved to persistent storage")
                } catch (e: Exception) {
                    println("❌ AndroidSessionViewModel: Error saving session: ${e.message}")
                }
            }
        }

        onClearSession = {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    sessionManager.clearSession()
                    println("🔐 AndroidSessionViewModel: Session cleared from persistent storage")
                } catch (e: Exception) {
                    println("❌ AndroidSessionViewModel: Error clearing session: ${e.message}")
                }
            }
        }
    }

    override fun requestSessionLoad() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val savedSession = sessionManager.getSession()
                if (savedSession != null) {
                    val user = User(
                        id = "", // Use empty string for id when loading from session
                        email = savedSession.email,
                        username = savedSession.username
                    )
                    println("🔐 AndroidSessionViewModel: Loaded session for user: ${savedSession.email}")
                    onLoadSession?.invoke(user, savedSession.token)
                } else {
                    println("🔐 AndroidSessionViewModel: No saved session found")
                    onLoadSession?.invoke(null, null)
                }
            } catch (e: Exception) {
                println("❌ AndroidSessionViewModel: Error loading session: ${e.message}")
                onLoadSession?.invoke(null, null)
            }
        }
    }
}