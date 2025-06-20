package com.nmichail.groovy_kmp.domain.repository

import com.nmichail.groovy_kmp.domain.models.AuthResponse

interface AuthRepository {
    suspend fun login(email: String, password: String): AuthResponse
}