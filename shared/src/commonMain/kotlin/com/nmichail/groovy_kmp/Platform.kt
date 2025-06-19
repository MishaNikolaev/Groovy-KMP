package com.nmichail.groovy_kmp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform