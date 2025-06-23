package com.nmichail.groovy_kmp.domain.usecases

import com.nmichail.groovy_kmp.data.remote.AuthApi
import com.nmichail.groovy_kmp.domain.models.AuthResponse

class RegisterUseCase(private val authApi: AuthApi) {
    suspend operator fun invoke(email: String, password: String, username: String): AuthResponse {
        return authApi.register(email, password, username)
    }
} 