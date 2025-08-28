package com.nmichail.groovy_kmp.data.remote

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

suspend fun checkServerHealth(client: HttpClient, baseUrl: String): Boolean {
    return try {
        println("🏥 Checking server health at $baseUrl/health")
        val response = client.get("$baseUrl/health")
        val isHealthy = response.status.isSuccess()
        println("🏥 Server health check result: $isHealthy (${response.status})")
        isHealthy
    } catch (e: Exception) {
        println("🏥 Server health check failed: ${e.message}")
        false
    }
}

suspend fun checkServerConnectivity(client: HttpClient, baseUrl: String): Boolean {
    return try {
        println("🔌 Checking server connectivity at $baseUrl")
        val response = client.get("$baseUrl")
        val isConnected = response.status.isSuccess()
        println("🔌 Server connectivity check result: $isConnected (${response.status})")
        isConnected
    } catch (e: Exception) {
        println("🔌 Server connectivity check failed: ${e.message}")
        false
    }
} 