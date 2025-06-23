package com.nmichail.groovy_kmp.presentation

import androidx.compose.ui.window.ComposeUIViewController
import com.nmichail.groovy_kmp.di.allModules
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatform.getKoin

object KoinInitializer {
    fun doInit() {
        startKoin {
            modules(allModules)
        }
    }
}

fun AppViewController() = ComposeUIViewController {
    App()
} 