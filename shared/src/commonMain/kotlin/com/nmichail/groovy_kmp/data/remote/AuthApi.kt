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
import android.util.Log
import kotlinx.serialization.json.Json

class AuthApi(private val client: HttpClient) {
    suspend fun login(email: String, password: String): AuthResponse {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("AuthApi", "Отправляем запрос на логин")
                Log.d("AuthApi", "Email: $email, Password: $password")
                
                val request = AuthRequest(email, password, email)
                Log.d("AuthApi", "Request body: $request")
                
                val json = Json { 
                    prettyPrint = true 
                    isLenient = true 
                    ignoreUnknownKeys = true 
                }
                val jsonString = json.encodeToString(AuthRequest.serializer(), request)
                Log.d("AuthApi", "Serialized JSON: $jsonString")
                
                val response: HttpResponse = client.post("http://127.0.0.1:8080/auth/login") {
                    contentType(ContentType.Application.Json)
                    setBody(jsonString)
                }
                
                Log.d("AuthApi", "Response status: ${response.status}")
                val responseBody = response.body<AuthResponse>()
                Log.d("AuthApi", "Response body: $responseBody")
                
                responseBody
            } catch (e: Exception) {
                Log.e("AuthApi", "Ошибка при логине: ${e.message}")
                e.printStackTrace()
                throw e
            }
        }
    }
} 