package com.nmichail.groovy_kmp.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import com.nmichail.groovy_kmp.domain.models.AuthRequest
import com.nmichail.groovy_kmp.domain.models.AuthResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import com.nmichail.groovy_kmp.getServerHost

class AuthApi(private val client: HttpClient) {
    suspend fun login(email: String, password: String): AuthResponse {
        return withContext(Dispatchers.IO) {
            try {
                val request = AuthRequest(email = email, password = password, username = email)
                val response: HttpResponse = client.post("http://${getServerHost()}:8080/auth/login") {
                    contentType(ContentType.Application.Json)
                    setBody(request)
                }
                val responseBody = response.body<AuthResponse>()
                responseBody
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
        }
    }

    suspend fun register(email: String, password: String, username: String): AuthResponse {
        return withContext(Dispatchers.IO) {
            try {
                val request = com.nmichail.groovy_kmp.domain.models.RegisterRequest(email = email, password = password, username = username)
                val response: HttpResponse = client.post("http://${getServerHost()}:8080/auth/register") {
                    contentType(ContentType.Application.Json)
                    setBody(request)
                }
                response.body()
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
        }
    }
} 