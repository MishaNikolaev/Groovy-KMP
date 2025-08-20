package com.nmichail.groovy_kmp.data.remote

import com.nmichail.groovy_kmp.domain.models.AuthRequest
import com.nmichail.groovy_kmp.domain.models.AuthResponse
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthApi(private val client: HttpClient) {
    private val baseUrl = getServerHost()

    suspend fun login(request: AuthRequest): AuthResponse {
        return try {
            println("🔐 AuthApi: Attempting login to $baseUrl/auth/login")
            println("🔐 AuthApi: Request body: $request")
            
            val response = client.post("$baseUrl/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            
            println("🔐 AuthApi: Login response status: ${response.status}")
            val responseBody = response.body<AuthResponse>()
            println("🔐 AuthApi: Login response body: $responseBody")
            
            responseBody
        } catch (e: Exception) {
            println("❌ AuthApi: Login failed with exception: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }

    suspend fun register(request: AuthRequest): AuthResponse {
        return try {
            println("🔐 AuthApi: Attempting registration to $baseUrl/auth/register")
            println("🔐 AuthApi: Request body: $request")
            
            val response = client.post("$baseUrl/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            
            println("🔐 AuthApi: Register response status: ${response.status}")
            val responseBody = response.body<AuthResponse>()
            println("🔐 AuthApi: Register response body: $responseBody")
            
            responseBody
        } catch (e: Exception) {
            println("❌ AuthApi: Registration failed with exception: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }

    suspend fun logout(): Boolean {
        return try {
            val response = client.post("$baseUrl/auth/logout")
            response.status.isSuccess()
        } catch (e: Exception) {
            false
        }
    }
} 