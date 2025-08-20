package com.nmichail.groovy_kmp.domain.usecases

import com.nmichail.groovy_kmp.domain.models.AuthResponse
import com.nmichail.groovy_kmp.domain.repository.AuthRepository

class LoginUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): AuthResponse {
        return authRepository.login(email, password)
    }
} 