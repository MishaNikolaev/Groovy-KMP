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

                val request = AuthRequest(email, password, email)

                val json = Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                }
                val jsonString = json.encodeToString(AuthRequest.serializer(), request)

                val response: HttpResponse = client.post("http://127.0.0.1:8080/auth/login") {
                    contentType(ContentType.Application.Json)
                    setBody(jsonString)
                }

                val responseBody = response.body<AuthResponse>()

                responseBody
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
        }
    }
} 