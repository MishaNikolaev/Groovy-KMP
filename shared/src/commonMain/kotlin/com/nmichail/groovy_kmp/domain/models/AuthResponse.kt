package com.nmichail.groovy_kmp.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val user: User? = null,
    val token: String? = null,
    val error: String? = null
)

@Serializable
data class User(
    val id: String,
    val email: String,
    val username: String
)