package com.nmichail.groovy_kmp.core.network

import io.ktor.client.HttpClient

expect class HttpClientProvider {
    fun create(): HttpClient
}
