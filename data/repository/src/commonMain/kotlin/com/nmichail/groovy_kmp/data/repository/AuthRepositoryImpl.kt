package com.nmichail.groovy_kmp.data.repository

import com.nmichail.groovy_kmp.data.remote.AuthApi
import com.nmichail.groovy_kmp.domain.models.AuthRequest
import com.nmichail.groovy_kmp.domain.models.AuthResponse
import com.nmichail.groovy_kmp.domain.repository.AuthRepository

class AuthRepositoryImpl(private val api: AuthApi) : AuthRepository {
    override suspend fun login(email: String, password: String): AuthResponse {
        val request = AuthRequest(email = email, password = password, username = email)
        return api.login(request)
    }

    override suspend fun register(email: String, password: String, username: String): AuthResponse {
        val request = AuthRequest(email = email, password = password, username = username)
        return api.register(request)
    }

    override suspend fun logout(): Boolean {
        return api.logout()
    }
} 