package com.nmichail.groovy_kmp.data.repositoryImpl

import com.nmichail.groovy_kmp.data.remote.AuthApi
import com.nmichail.groovy_kmp.domain.models.AuthResponse
import com.nmichail.groovy_kmp.domain.repository.RegisterRepository

class RegisterRepositoryImpl(
    private val authApi: AuthApi
) : RegisterRepository {
    override suspend fun register(email: String, password: String, username: String): AuthResponse {
        return authApi.register(email, password, username)
    }
} 