package com.nmichail.groovy_kmp.data.remote

actual fun getServerHost(): String {
    // Try localhost first for development, fallback to the original IP
    val host = "http://10.0.2.2:8080" // 10.0.2.2 is localhost from Android emulator
    // val host = "http://192.168.0.6:8080" // Original IP for physical device
    println("🌐 ServerHostProvider: Using server host: $host")
    return host
} 