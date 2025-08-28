package com.nmichail.groovy_kmp.domain.usecases

import com.nmichail.groovy_kmp.domain.models.AuthResponse
import com.nmichail.groovy_kmp.domain.repository.RegisterRepository

class RegisterUseCase(private val registerRepository: RegisterRepository) {
    suspend operator fun invoke(email: String, password: String, username: String): AuthResponse {
        return registerRepository.register(email, password, username)
    }
} 