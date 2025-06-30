package com.nmichail.groovy_kmp.android.di

import android.content.Context
import com.nmichail.groovy_kmp.data.manager.SessionManager
import com.nmichail.groovy_kmp.data.local.datasource.UserSessionDataSource
import com.nmichail.groovy_kmp.android.local.datasource.UserSessionDataSourceImpl
import org.koin.dsl.module

val sessionModuleAndroid = module {
    single<UserSessionDataSource> { UserSessionDataSourceImpl(get<Context>()) }
    single { SessionManager(get()) }
} 