package com.nmichail.groovy_kmp.android

import android.app.Application
import com.nmichail.groovy_kmp.di.appModule
import com.nmichail.groovy_kmp.di.sessionModuleCommon
import com.nmichail.groovy_kmp.di.playerModule
import com.nmichail.groovy_kmp.data.local.ApplicationContextHolder
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ApplicationContextHolder.context = this
        startKoin {
            androidContext(this@MyApplication)
            modules(listOf(appModule, sessionModuleCommon, playerModule))
        }
    }
}