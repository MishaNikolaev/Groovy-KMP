package com.nmichail.groovy_kmp.di

import io.ktor.client.*
import io.ktor.client.engine.okhttp.OkHttp

actual fun provideHttpClient(): HttpClient = HttpClient(OkHttp)