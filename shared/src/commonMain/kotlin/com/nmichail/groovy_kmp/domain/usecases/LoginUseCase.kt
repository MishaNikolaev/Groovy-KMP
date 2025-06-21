package com.nmichail.groovy_kmp.domain.usecases

import com.nmichail.groovy_kmp.data.remote.AuthApi
import com.nmichail.groovy_kmp.domain.models.AuthResponse
 
class LoginUseCase(private val authApi: AuthApi) {
    suspend operator fun invoke(email: String, password: String): AuthResponse {
        return authApi.login(email, password)
    }
} 