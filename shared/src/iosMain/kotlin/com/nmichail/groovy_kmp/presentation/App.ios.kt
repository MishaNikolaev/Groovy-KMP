package com.nmichail.groovy_kmp.presentation

import androidx.compose.ui.window.ComposeUIViewController
import com.nmichail.groovy_kmp.di.appModule
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatform.getKoin

object KoinInitializer {
    fun doInit() {
        startKoin {
            modules(appModule)
        }
    }
}

fun AppViewController() = ComposeUIViewController {
    App(viewModel = getKoin().get())
} 