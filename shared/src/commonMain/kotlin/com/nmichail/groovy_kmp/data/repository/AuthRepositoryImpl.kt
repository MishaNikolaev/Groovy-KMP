package com.nmichail.groovy_kmp.data.repositoryImpl

import com.nmichail.groovy_kmp.data.remote.AuthApi
import com.nmichail.groovy_kmp.domain.models.AuthResponse
import com.nmichail.groovy_kmp.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val authApi: AuthApi
) : AuthRepository {
    override suspend fun login(email: String, password: String): AuthResponse {
        return authApi.login(email, password)
    }
}