package com.nmichail.groovy_kmp.data.local.datasource

import com.nmichail.groovy_kmp.data.local.model.UserSession

class InMemoryUserSessionDataSource : UserSessionDataSource {
    private var session: UserSession? = null

    override suspend fun saveSession(session: UserSession) {
        this.session = session
    }

    override suspend fun getSession(): UserSession? = session

    override suspend fun clearSession() {
        session = null
    }
}