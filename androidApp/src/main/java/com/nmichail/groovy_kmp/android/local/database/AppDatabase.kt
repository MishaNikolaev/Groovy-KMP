package com.nmichail.groovy_kmp.android.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nmichail.groovy_kmp.android.local.dao.UserSessionDao
import com.nmichail.groovy_kmp.android.local.entity.UserSessionEntity

@Database(entities = [UserSessionEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userSessionDao(): UserSessionDao
}