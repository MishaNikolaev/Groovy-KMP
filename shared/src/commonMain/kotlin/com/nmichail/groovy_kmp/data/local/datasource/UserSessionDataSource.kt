package com.nmichail.groovy_kmp.data.local.datasource

import com.nmichail.groovy_kmp.data.local.model.UserSession

interface UserSessionDataSource {
    suspend fun saveSession(session: UserSession)
    suspend fun getSession(): UserSession?
    suspend fun clearSession()
}