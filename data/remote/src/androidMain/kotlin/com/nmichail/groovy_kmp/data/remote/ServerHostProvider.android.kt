package com.nmichail.groovy_kmp.data.remote

actual fun getServerHost(): String {
    // Try different IPs for different scenarios
    val hosts = listOf(
        "http://10.0.2.2:8080", // Android emulator
        "http://192.168.0.6:8080", // Physical device - original IP
        "http://192.168.0.5:8080", // Physical device - alternative IP
        "http://192.168.0.10:8080", // Physical device - another alternative
        "http://localhost:8080" // Local development
    )
    
    // For now, let's use the working IP address
    // You can change this based on your setup
    val host = "http://192.168.0.5:8080"
    println("🌐 ServerHostProvider: Using server host: $host")
    println("🌐 ServerHostProvider: Available hosts: ${hosts.joinToString(", ")}")
    return host
} 