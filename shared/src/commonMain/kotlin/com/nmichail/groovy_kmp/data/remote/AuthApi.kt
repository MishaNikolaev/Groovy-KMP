package com.nmichail.groovy_kmp.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import com.nmichail.groovy_kmp.domain.models.AuthRequest
import com.nmichail.groovy_kmp.domain.models.AuthResponse

class AuthApi(private val client: HttpClient) {
    suspend fun login(email: String, password: String): AuthResponse {
        val response: HttpResponse = client.post("http://127.0.0.1:8080/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(AuthRequest(email, password))
        }
        return response.body()
    }
} 