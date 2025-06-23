package com.nmichail.groovy_kmp.android.local.datasource

import android.content.Context
import androidx.room.Room
import com.nmichail.groovy_kmp.android.local.database.AppDatabase
import com.nmichail.groovy_kmp.android.local.entity.UserSessionEntity
import com.nmichail.groovy_kmp.data.local.model.UserSession
import com.nmichail.groovy_kmp.data.local.datasource.UserSessionDataSource

class UserSessionDataSourceImpl(context: Context) : UserSessionDataSource {
    private val db = Room.databaseBuilder(
        context,
        AppDatabase::class.java, "app_db"
    ).build()
    private val dao = db.userSessionDao()

    override suspend fun saveSession(session: UserSession) {
        dao.saveSession(UserSessionEntity(session.email, session.username, session.token))
    }

    override suspend fun getSession(): UserSession? {
        return dao.getSession()?.let { UserSession(it.email, it.username, it.token) }
    }

    override suspend fun clearSession() {
        dao.clearSession()
    }
} 