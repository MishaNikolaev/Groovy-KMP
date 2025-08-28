package com.nmichail.groovy_kmp.domain.repository

import com.nmichail.groovy_kmp.domain.models.AuthResponse

interface RegisterRepository {
    suspend fun register(email: String, password: String, username: String): AuthResponse
} 