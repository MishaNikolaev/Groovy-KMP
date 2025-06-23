package com.nmichail.groovy_kmp.data.manager

import com.nmichail.groovy_kmp.data.local.datasource.UserSessionDataSource
import com.nmichail.groovy_kmp.data.local.model.UserSession

class SessionManager(private val dataSource: UserSessionDataSource) {
    suspend fun saveSession(session: UserSession) = dataSource.saveSession(session)
    suspend fun getSession(): UserSession? = dataSource.getSession()
    suspend fun clearSession() = dataSource.clearSession()
}