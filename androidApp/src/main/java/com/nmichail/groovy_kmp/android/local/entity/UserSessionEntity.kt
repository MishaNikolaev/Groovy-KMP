package com.nmichail.groovy_kmp.android.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_session")
data class UserSessionEntity(
    @PrimaryKey val email: String,
    val username: String,
    val token: String
)