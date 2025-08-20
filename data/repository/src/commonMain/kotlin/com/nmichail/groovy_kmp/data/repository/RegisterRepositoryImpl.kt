package com.nmichail.groovy_kmp.data.repository

import com.nmichail.groovy_kmp.data.remote.AuthApi
import com.nmichail.groovy_kmp.domain.models.AuthRequest
import com.nmichail.groovy_kmp.domain.models.AuthResponse
import com.nmichail.groovy_kmp.domain.repository.RegisterRepository

class RegisterRepositoryImpl(private val api: AuthApi) : RegisterRepository {
    override suspend fun register(email: String, password: String, username: String): AuthResponse {
        val request = AuthRequest(email = email, password = password, username = username)
        return api.register(request)
    }
} 