package com.nmichail.groovy_kmp.di

import com.nmichail.groovy_kmp.domain.MusicServiceController
import com.nmichail.groovy_kmp.platform.service.MusicServiceControllerImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val playerModuleAndroid = module {
    single<MusicServiceController> { MusicServiceControllerImpl(androidContext().applicationContext) }
} 