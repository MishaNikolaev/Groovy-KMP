package com.nmichail.groovy_kmp

// Функция для получения IP адреса сервера
// ВАЖНО: Измените IP адрес на актуальный для вашей сети!
// 
// Как узнать IP адрес:
// macOS: ifconfig | grep "inet " | grep -v 127.0.0.1
// Windows: ipconfig | findstr "IPv4"
// Linux: ip addr show | grep "inet " | grep -v 127.0.0.1
//
// Возможные варианты:
// - 192.168.0.6 (ваш текущий IP)
// - 192.168.1.100 (для другой сети)
// - 10.0.2.2 (для Android эмулятора)
// - localhost (для локальной разработки)

actual fun getServerHost(): String {
    val host = "192.168.0.5"
    println("[ServerHostProvider] Using host: $host")
    return host
}

