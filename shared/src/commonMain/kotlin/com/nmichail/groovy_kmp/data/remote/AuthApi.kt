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

class AuthApi(private val client: HttpClient) {
    suspend fun login(email: String, password: String): AuthResponse {
        return withContext(Dispatchers.IO) {
            try {
                val request = AuthRequest(email = email, password = password, username = email)
                //Here my mac ip, you can change it to yours.
                val response: HttpResponse = client.post("http://192.168.0.6:8080/auth/login") {

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
                //Here my mac ip, you can change it to yours.
                val response: HttpResponse = client.post("http://192.168.0.6:8080/auth/register") {
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