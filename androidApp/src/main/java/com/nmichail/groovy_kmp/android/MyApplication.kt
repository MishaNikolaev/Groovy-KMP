package com.nmichail.groovy_kmp.android

import android.app.Application
import com.nmichail.groovy_kmp.di.appModule
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(appModule)
        }
    }
}