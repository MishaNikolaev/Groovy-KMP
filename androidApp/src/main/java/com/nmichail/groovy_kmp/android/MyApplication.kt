package com.nmichail.groovy_kmp.android

import android.app.Application
import com.nmichail.groovy_kmp.di.allModules
import com.nmichail.groovy_kmp.android.di.sessionModuleAndroid
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
            modules(allModules + sessionModuleAndroid)
        }
    }
}