package com.nmichail.groovy_kmp.di

import com.nmichail.groovy_kmp.data.local.datasource.InMemoryUserSessionDataSource
import com.nmichail.groovy_kmp.data.manager.SessionManager
import com.nmichail.groovy_kmp.data.local.datasource.UserSessionDataSource
import org.koin.dsl.module

val sessionModuleCommon = module {
    single<UserSessionDataSource> { InMemoryUserSessionDataSource() }
    single { SessionManager(get()) }
} 